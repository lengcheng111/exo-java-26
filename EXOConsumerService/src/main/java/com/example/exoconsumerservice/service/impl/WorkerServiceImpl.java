package com.example.exoconsumerservice.service.impl;

import com.example.exoconsumerservice.dto.Inconsistency;
import com.example.exoconsumerservice.dto.ResultMessage;
import com.example.exoconsumerservice.dto.ResultMessageStatus;
import com.example.exoconsumerservice.dto.UserMessage;
import com.example.exoconsumerservice.service.JobResultAggregatorService;
import com.example.exoconsumerservice.service.ComparisonService;
import com.example.exoconsumerservice.service.WorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WorkerServiceImpl implements WorkerService {

    private static final Logger log =
            LoggerFactory.getLogger(WorkerServiceImpl.class);

    private final ComparisonService comparisonService;
    private final JobResultAggregatorService jobResultAggregatorService;

    public WorkerServiceImpl(
            ComparisonService comparisonService,
            JobResultAggregatorService jobResultAggregatorService) {

        this.comparisonService = comparisonService;
        this.jobResultAggregatorService = jobResultAggregatorService;
    }

    @Override
    @KafkaListener(
            topics = "${kafka.topic.folder-check-request}",
            groupId = "${kafka.consumer.group-id}",
            concurrency = "${kafka.consumer.concurrency}"
    )
    public void consume(UserMessage request) {

        if (request == null
                || request.getJobId() == null
                || request.getEmail() == null) {
            throw new IllegalArgumentException("Invalid request");
        }

        try {
            List<String> messages = comparisonService.compare(request.getEmail())
                    .stream()
                    .map(Inconsistency::message)
                    .toList();

            jobResultAggregatorService.recordResult(
                    new ResultMessage(
                            request.getJobId(),
                            request.getEmail(),
                            ResultMessageStatus.SUCCESS,
                            messages
                    )
            );
        } catch (Exception e) {
            log.error(
                    "Failed to process jobId={}, email={}",
                    request.getJobId(),
                    request.getEmail(),
                    e
            );

            // should throw e: do not commit to kafka -> will retry
            throw e;
        }
    }
}