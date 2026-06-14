package com.medic.retrieval.service;

import com.medic.retrieval.dto.RetrievalResultResponse;
import com.medic.retrieval.dto.RetrievalSearchRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class StubRetrievalClient implements RetrievalClient {

    @Override
    public List<RetrievalResultResponse> search(UUID userId, RetrievalSearchRequest request) {
        return List.of(new RetrievalResultResponse(
                UUID.randomUUID(),
                "RESEARCH_STUB",
                "Sparse retrieval placeholder",
                "Connect BM25, sparse neural, dense, or hybrid retrieval backend here.",
                BigDecimal.ONE
        ));
    }
}
