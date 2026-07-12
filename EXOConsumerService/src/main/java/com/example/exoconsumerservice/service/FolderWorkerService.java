package com.example.exoconsumerservice.service;

import com.example.exoconsumerservice.dto.UserMessage;

public interface FolderWorkerService {
    void consume(UserMessage request);
}
