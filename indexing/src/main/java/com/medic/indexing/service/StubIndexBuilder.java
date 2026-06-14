package com.medic.indexing.service;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class StubIndexBuilder implements IndexBuilder {

    @Override
    public String buildSparseTerms(String content) {
        return Arrays.stream(content.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(term -> !term.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.joining(" "));
    }
}
