package com.medic.analysis.service;

import java.io.IOException;
import java.io.InputStream;

public record StoredObjectContent(
        String bucket,
        String key,
        String contentType,
        long sizeBytes,
        InputStream inputStream
) implements AutoCloseable {

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
