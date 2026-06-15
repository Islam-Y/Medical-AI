package com.medic.indexing.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "search.index", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoOpSearchIndexClient implements SearchIndexClient {

    @Override
    public void index(IndexDocument document) {
        // Search backend is optional for local unit workflows.
    }
}
