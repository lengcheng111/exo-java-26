package com.example.exoservice.service.producer;

import com.example.exoservice.dto.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {
    private final KafkaTemplate<String, UserMessage> kafkaTemplate;

    @Value("${topic.user-check}")
    private String topic;

    public UserProducer(KafkaTemplate<String, UserMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(UserMessage message) {
        kafkaTemplate.send(topic, message.getEmail(), message);
    }
}
