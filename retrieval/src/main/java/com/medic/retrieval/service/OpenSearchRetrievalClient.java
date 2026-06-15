package com.medic.retrieval.service;

import com.medic.retrieval.dto.RetrievalResultResponse;
import com.medic.retrieval.dto.RetrievalSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "search.index", name = "enabled", havingValue = "true")
public class OpenSearchRetrievalClient implements RetrievalClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${search.index.url:http://localhost:9200}")
    private String searchIndexUrl;

    @Value("${search.index.name:medical-ai-chunks}")
    private String indexName;

    @Override
    public List<RetrievalResultResponse> search(UUID userId, RetrievalSearchRequest request) {
        JsonNode response = restClientBuilder.baseUrl(searchIndexUrl)
                .build()
                .post()
                .uri("/{index}/_search", indexName)
                .body(searchBody(userId, request))
                .retrieve()
                .body(JsonNode.class);
        return mapHits(response);
    }

    private Map<String, Object> searchBody(UUID userId, RetrievalSearchRequest request) {
        return Map.of(
                "size", request.topK(),
                "query", Map.of(
                        "bool", Map.of(
                                "filter", List.of(Map.of("term", Map.of("userId", userId.toString()))),
                                "must", List.of(Map.of("multi_match", Map.of(
                                        "query", request.query(),
                                        "fields", List.of("title^2", "content", "sparseTerms")
                                )))
                        )
                )
        );
    }

    private List<RetrievalResultResponse> mapHits(JsonNode response) {
        List<RetrievalResultResponse> results = new ArrayList<>();
        JsonNode hits = response.path("hits").path("hits");
        if (!hits.isArray()) {
            return results;
        }
        for (JsonNode hit : hits) {
            JsonNode source = hit.path("_source");
            results.add(new RetrievalResultResponse(
                    UUID.fromString(source.path("sourceId").asString()),
                    source.path("sourceType").asString(),
                    source.path("title").asString(),
                    source.path("content").asString(),
                    BigDecimal.valueOf(hit.path("_score").asDouble()),
                    optionalUuid(source.path("documentId")),
                    optionalUuid(source.path("extractionId")),
                    optionalUuid(source.path("chunkId")),
                    optionalText(source.path("artifactUri")),
                    optionalInteger(source.path("pageNumber"))
            ));
        }
        return results;
    }

    private UUID optionalUuid(JsonNode node) {
        if (node.isMissingNode() || node.isNull() || node.asString().isBlank()) {
            return null;
        }
        return UUID.fromString(node.asString());
    }

    private String optionalText(JsonNode node) {
        if (node.isMissingNode() || node.isNull() || node.asString().isBlank()) {
            return null;
        }
        return node.asString();
    }

    private Integer optionalInteger(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        return node.asInt();
    }
}
