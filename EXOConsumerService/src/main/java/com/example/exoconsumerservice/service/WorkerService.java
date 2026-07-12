package com.example.exoconsumerservice.service;

import com.example.exoconsumerservice.dto.UserMessage;

public interface WorkerService {
    void consume(UserMessage request);
}
