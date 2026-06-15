package com.medic.indexing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "search.index", name = "enabled", havingValue = "true")
public class OpenSearchIndexClient implements SearchIndexClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${search.index.url:http://localhost:9200}")
    private String searchIndexUrl;

    @Value("${search.index.name:medical-ai-chunks}")
    private String indexName;

    @Override
    public void index(IndexDocument document) {
        restClientBuilder.baseUrl(searchIndexUrl)
                .build()
                .put()
                .uri("/{index}/_doc/{id}", indexName, document.chunkId())
                .body(document)
                .retrieve()
                .toBodilessEntity();
    }
}
