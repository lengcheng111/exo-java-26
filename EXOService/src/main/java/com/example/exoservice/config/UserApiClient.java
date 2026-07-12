package com.example.exoservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Configuration
public class UserApiClient {

    private final WebClient webClient;

    public UserApiClient(@Value("${mock-api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<String> getEmails() {
        String[] emails = webClient
                .get()
                .uri("/users")
                .retrieve()
                .bodyToMono(String[].class)
                .block();

        return emails != null ? Arrays.asList(emails) : List.of();
    }
}