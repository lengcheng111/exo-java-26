package com.example.exoconsumerservice.config;

import com.example.exoconsumerservice.dto.ResultMessage;
import com.example.exoconsumerservice.dto.ResultMessageStatus;
import com.example.exoconsumerservice.dto.UserMessage;
import com.example.exoconsumerservice.service.AggregatorService;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;

@Configuration
public class FolderDLTConsumer {

    public static final String SUBFIX_DLT_TOPIC = ".DLT";

    private final AggregatorService aggregatorService;

    public FolderDLTConsumer(AggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    @KafkaListener(
            topics = "${kafka.topic.folder-check-request}" + SUBFIX_DLT_TOPIC,
            groupId = "${kafka.consumer.group-id}-dlt"
    )
    public void consume(UserMessage request) {

        aggregatorService.add(
                new ResultMessage(request.getJobId(), request.getEmail(), ResultMessageStatus.FAILED, List.of())
        );
    }
}