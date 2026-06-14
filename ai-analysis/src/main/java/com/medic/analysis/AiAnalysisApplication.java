package com.medic.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AiAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAnalysisApplication.class, args);
    }
}
