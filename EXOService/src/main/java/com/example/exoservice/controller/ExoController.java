package com.example.exoservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@RestController
public class ExoController {

    private final RestTemplate restTemplate;

    private final WebClient webClient;

    public ExoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/inconsistencies")
    Test getInConsistencies() {
        ResponseEntity<Arrays> forEntity = this.restTemplate.getForEntity("http://localhost:8080/users", Arrays.class);
        Arrays body = forEntity.getBody();
        return new Test("1", "test@gmail.com");
    }

    record Test(String id, String email){}
}
