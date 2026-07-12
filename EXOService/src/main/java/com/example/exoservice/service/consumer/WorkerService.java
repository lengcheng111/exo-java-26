package com.example.exoservice.service.consumer;


import com.example.exoservice.dto.UserMessage;

public interface WorkerService {
    void consume(UserMessage request);
}
