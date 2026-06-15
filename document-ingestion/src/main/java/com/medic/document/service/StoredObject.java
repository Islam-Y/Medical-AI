package com.medic.document.service;

public record StoredObject(
        String bucket,
        String key,
        String contentType,
        long sizeBytes,
        String checksumSha256
) {
    public String uri() {
        return "s3://" + bucket + "/" + key;
    }
}
