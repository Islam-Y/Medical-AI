package com.medic.analysis.service;

import java.io.InputStream;

public interface ObjectStorageClient {

    StoredObjectContent get(String bucket, String key);

    StoredObject put(String bucket, String key, String contentType, InputStream inputStream, long sizeBytes);
}
