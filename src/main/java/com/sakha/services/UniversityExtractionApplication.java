package com.sakha.services;

/**
 * Created by root on 21/12/15.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class UniversityExtractionApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniversityExtractionApplication.class, args);
    }

}
