package com.medic.analysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class S3ObjectStorageClient implements ObjectStorageClient {

    private final S3Client s3Client;
    private final Set<String> checkedBuckets = ConcurrentHashMap.newKeySet();

    @Override
    public StoredObjectContent get(String bucket, String key) {
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
                GetObjectRequest.builder().bucket(bucket).key(key).build(),
                ResponseTransformer.toInputStream()
        );
        GetObjectResponse metadata = response.response();
        return new StoredObjectContent(bucket, key, metadata.contentType(), metadata.contentLength(), response);
    }

    @Override
    public StoredObject put(String bucket, String key, String contentType, InputStream inputStream, long sizeBytes) {
        try {
            ensureBucket(bucket);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest)) {
                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(key)
                                .contentType(contentType)
                                .contentLength(sizeBytes)
                                .build(),
                        RequestBody.fromInputStream(digestInputStream, sizeBytes)
                );
            }
            return new StoredObject(bucket, key, contentType, sizeBytes, HexFormat.of().formatHex(digest.digest()));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to write analysis artifact", exception);
        }
    }

    private void ensureBucket(String bucket) {
        if (checkedBuckets.contains(bucket)) {
            return;
        }
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException exception) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        }
        checkedBuckets.add(bucket);
    }
}
