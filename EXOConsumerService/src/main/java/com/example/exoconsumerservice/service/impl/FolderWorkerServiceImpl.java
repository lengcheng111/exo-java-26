package com.example.exoconsumerservice.service.impl;

import com.example.exoconsumerservice.dto.Inconsistency;
import com.example.exoconsumerservice.dto.ResultMessage;
import com.example.exoconsumerservice.dto.ResultMessageStatus;
import com.example.exoconsumerservice.dto.UserMessage;
import com.example.exoconsumerservice.service.AggregatorService;
import com.example.exoconsumerservice.service.FolderService;
import com.example.exoconsumerservice.service.FolderWorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FolderWorkerServiceImpl implements FolderWorkerService {

    private static final Logger log =
            LoggerFactory.getLogger(FolderWorkerServiceImpl.class);

    private final FolderService folderService;
    private final AggregatorService aggregatorService;

    public FolderWorkerServiceImpl(
            FolderService folderService,
            AggregatorService aggregatorService) {

        this.folderService = folderService;
        this.aggregatorService = aggregatorService;
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
            List<String> messages = folderService.compare(request.getEmail())
                    .stream()
                    .map(Inconsistency::message)
                    .toList();

            aggregatorService.add(
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

            throw e;
        }
    }
}