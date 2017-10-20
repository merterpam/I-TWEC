package com.erpam.mert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.erpam.mert"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}