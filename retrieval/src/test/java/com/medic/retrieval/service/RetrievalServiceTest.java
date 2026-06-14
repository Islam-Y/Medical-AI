package com.medic.retrieval.service;

import com.medic.retrieval.dto.RetrievalMode;
import com.medic.retrieval.dto.RetrievalResultResponse;
import com.medic.retrieval.dto.RetrievalSearchRequest;
import com.medic.retrieval.entity.RetrievalQueryEntity;
import com.medic.retrieval.repository.RetrievalQueryRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RetrievalServiceTest {

    @Test
    void searchPersistsQueryAndReturnsResults() {
        // Arrange
        RetrievalQueryRepository repository = mock(RetrievalQueryRepository.class);
        RetrievalClient client = mock(RetrievalClient.class);
        RetrievalService service = new RetrievalService(repository, client);
        UUID userId = UUID.randomUUID();
        RetrievalSearchRequest request = new RetrievalSearchRequest("vitamin d", RetrievalMode.SPARSE_NEURAL, 3);
        RetrievalResultResponse result = new RetrievalResultResponse(UUID.randomUUID(), "OBSERVATION", "Vitamin D", "22 ng/mL", BigDecimal.ONE);
        when(client.search(userId, request)).thenReturn(List.of(result));
        when(repository.save(any(RetrievalQueryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var response = service.search(userId, request);

        // Assert
        assertThat(response.results()).containsExactly(result);
        assertThat(response.mode()).isEqualTo(RetrievalMode.SPARSE_NEURAL);
        verify(repository).save(any(RetrievalQueryEntity.class));
    }

    @Test
    void stubClientReturnsPlaceholderResult() {
        // Arrange
        StubRetrievalClient client = new StubRetrievalClient();
        RetrievalSearchRequest request = new RetrievalSearchRequest("query", RetrievalMode.BM25, 1);

        // Act
        List<RetrievalResultResponse> response = client.search(UUID.randomUUID(), request);

        // Assert
        assertThat(response).hasSize(1);
        assertThat(response.get(0).sourceType()).isEqualTo("RESEARCH_STUB");
    }
}
