package com.medic.healthrecord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HealthRecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthRecordApplication.class, args);
    }
}
