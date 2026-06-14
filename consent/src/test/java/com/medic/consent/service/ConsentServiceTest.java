package com.medic.consent.service;

import com.medic.consent.dto.GrantConsentRequest;
import com.medic.consent.entity.ConsentEntity;
import com.medic.consent.entity.ConsentScope;
import com.medic.consent.entity.ConsentStatus;
import com.medic.consent.repository.ConsentRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConsentServiceTest {

    @Test
    void grantStoresActiveConsent() {
        // Arrange
        ConsentRepository repository = mock(ConsentRepository.class);
        ConsentService service = new ConsentService(repository);
        UUID userId = UUID.randomUUID();
        GrantConsentRequest request = new GrantConsentRequest(ConsentScope.AI_ANALYSIS, "documents");
        when(repository.save(any(ConsentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var response = service.grant(userId, request);

        // Assert
        assertThat(response.scope()).isEqualTo(ConsentScope.AI_ANALYSIS);
        assertThat(response.status()).isEqualTo(ConsentStatus.ACTIVE);
    }

    @Test
    void consentsMapStoredRows() {
        // Arrange
        ConsentRepository repository = mock(ConsentRepository.class);
        ConsentService service = new ConsentService(repository);
        UUID userId = UUID.randomUUID();
        ConsentEntity consent = new ConsentEntity(userId, ConsentScope.DATA_EXPORT, "export");
        when(repository.findByUserIdOrderByGrantedAtDesc(userId)).thenReturn(List.of(consent));

        // Act
        var response = service.consents(userId);

        // Assert
        assertThat(response).extracting("scope").containsExactly(ConsentScope.DATA_EXPORT);
    }

    @Test
    void revokeChangesStatus() {
        // Arrange
        ConsentRepository repository = mock(ConsentRepository.class);
        ConsentService service = new ConsentService(repository);
        UUID userId = UUID.randomUUID();
        ConsentEntity consent = new ConsentEntity(userId, ConsentScope.RESEARCH_EVALUATION, "research");
        when(repository.findByIdAndUserId(consent.getId(), userId)).thenReturn(Optional.of(consent));
        when(repository.save(consent)).thenReturn(consent);

        // Act
        var response = service.revoke(userId, consent.getId());

        // Assert
        assertThat(response.status()).isEqualTo(ConsentStatus.REVOKED);
        assertThat(response.revokedAt()).isNotNull();
    }

    @Test
    void revokeRejectsMissingConsent() {
        // Arrange
        ConsentRepository repository = mock(ConsentRepository.class);
        ConsentService service = new ConsentService(repository);
        UUID userId = UUID.randomUUID();
        UUID consentId = UUID.randomUUID();
        when(repository.findByIdAndUserId(consentId, userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatThrownBy(() -> service.revoke(userId, consentId))
                .isInstanceOf(ConsentNotFoundException.class);
    }
}
