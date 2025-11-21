package com.mentelibre.gateway_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class GatewayServiceApplication {

    @Value("${test.message:default}")
    private String testMessage;

    @PostConstruct
    public void init() {
        System.out.println(">>> TEST MESSAGE: " + testMessage);
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
