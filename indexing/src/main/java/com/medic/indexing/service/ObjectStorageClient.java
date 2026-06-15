package com.medic.indexing.service;

public interface ObjectStorageClient {

    String readText(String bucket, String key);
}
