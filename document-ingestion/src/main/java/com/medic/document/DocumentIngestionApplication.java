package com.medic.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DocumentIngestionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentIngestionApplication.class, args);
    }
}
