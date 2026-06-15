package com.medic.document.service;

import java.io.InputStream;

public interface ObjectStorageClient {

    StoredObject put(String bucket, String key, String contentType, InputStream inputStream, long sizeBytes);
}
