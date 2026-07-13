package com.example.exoservice.service.consumer;


import com.example.exoservice.dto.UserMessage;
import org.springframework.kafka.support.Acknowledgment;

public interface WorkerService {
    void consume(UserMessage request, Acknowledgment acknowledgment);
}
