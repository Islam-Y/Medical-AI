package com.medic.analysis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@Getter
@Setter
@ConfigurationProperties(prefix = "object-storage")
public class ObjectStorageProperties {

    private URI endpoint = URI.create("http://localhost:9000");
    private String region = "us-east-1";
    private String accessKey = "medicadmin";
    private String secretKey = "medicadmin123";
    private boolean pathStyleAccess = true;
    private Buckets buckets = new Buckets();

    @Getter
    @Setter
    public static class Buckets {
        private String originalDocuments = "medical-ai-documents";
        private String extractedArtifacts = "medical-ai-extractions";
    }
}
