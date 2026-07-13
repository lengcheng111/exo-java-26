package com.example.exoservice.config;

import com.example.exoservice.dto.ResultMessage;
import com.example.exoservice.dto.ResultMessageStatus;
import com.example.exoservice.dto.UserMessage;
import com.example.exoservice.helper.KeyUtil;
import com.example.exoservice.service.consumer.JobResultAggregatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;

@Configuration
public class FolderDLTConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(FolderDLTConsumer.class);

    private final JobResultAggregatorService jobResultAggregatorService;

    public FolderDLTConsumer(JobResultAggregatorService jobResultAggregatorService) {
        this.jobResultAggregatorService = jobResultAggregatorService;
    }

    @KafkaListener(
            topics = "${topic.user-check}" + KeyUtil.SUFFIX_DLT_TOPIC,
            groupId = "${kafka.consumer.group-id}-dlt"
    )
    public void consume(UserMessage request) {
        jobResultAggregatorService.addResult(new ResultMessage(request.getJobId(), request.getEmail(), ResultMessageStatus.FAILED, List.of()))
                .doOnSuccess(v -> log.info("Recorded result for {}", request.getJobId()))
                .doOnError(ex -> log.error("Failed to record result", ex))
                .subscribe();
    }
}