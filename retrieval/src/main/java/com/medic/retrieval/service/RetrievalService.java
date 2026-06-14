package com.medic.retrieval.service;

import com.medic.retrieval.dto.RetrievalResponse;
import com.medic.retrieval.dto.RetrievalResultResponse;
import com.medic.retrieval.dto.RetrievalSearchRequest;
import com.medic.retrieval.entity.RetrievalQueryEntity;
import com.medic.retrieval.repository.RetrievalQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final RetrievalQueryRepository retrievalQueryRepository;
    private final RetrievalClient retrievalClient;

    @Transactional
    public RetrievalResponse search(UUID userId, RetrievalSearchRequest request) {
        long started = System.nanoTime();
        List<RetrievalResultResponse> results = retrievalClient.search(userId, request);
        long latencyMs = Math.max(0, (System.nanoTime() - started) / 1_000_000);
        RetrievalQueryEntity query = retrievalQueryRepository.save(new RetrievalQueryEntity(
                userId,
                request.query(),
                request.mode(),
                request.topK(),
                latencyMs,
                results.size()
        ));
        return new RetrievalResponse(query.getId(), query.getQueryText(), query.getMode(), query.getLatencyMs(), results);
    }
}
