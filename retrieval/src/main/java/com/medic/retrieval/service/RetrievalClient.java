package com.medic.retrieval.service;

import com.medic.retrieval.dto.RetrievalResultResponse;
import com.medic.retrieval.dto.RetrievalSearchRequest;

import java.util.List;
import java.util.UUID;

public interface RetrievalClient {

    List<RetrievalResultResponse> search(UUID userId, RetrievalSearchRequest request);
}
