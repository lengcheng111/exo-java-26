package com.example.exoconsumerservice.config;

import com.example.exoconsumerservice.dto.ResultMessage;
import com.example.exoconsumerservice.dto.ResultMessageStatus;
import com.example.exoconsumerservice.dto.UserMessage;
import com.example.exoconsumerservice.service.JobResultAggregatorService;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;

@Configuration
public class FolderDLTConsumer {

    public static final String SUFFIX_DLT_TOPIC = ".DLT";

    private final JobResultAggregatorService jobResultAggregatorService;

    public FolderDLTConsumer(JobResultAggregatorService jobResultAggregatorService) {
        this.jobResultAggregatorService = jobResultAggregatorService;
    }

    @KafkaListener(
            topics = "${kafka.topic.folder-check-request}" + SUFFIX_DLT_TOPIC,
            groupId = "${kafka.consumer.group-id}-dlt"
    )
    public void consume(UserMessage request) {

        jobResultAggregatorService.recordResult(
                new ResultMessage(request.getJobId(), request.getEmail(), ResultMessageStatus.FAILED, List.of())
        );
    }
}