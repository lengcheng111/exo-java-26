package com.example.exoservice.service.consumer.impl;

import com.example.exoservice.dto.Inconsistency;
import com.example.exoservice.dto.ResultMessage;
import com.example.exoservice.dto.ResultMessageStatus;
import com.example.exoservice.dto.UserMessage;
import com.example.exoservice.service.consumer.ComparisonService;
import com.example.exoservice.service.consumer.JobResultAggregatorService;
import com.example.exoservice.service.consumer.WorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
            topics = "${topic.user-check}",
            groupId = "${kafka.consumer.group-id}",
            concurrency = "${kafka.consumer.concurrency}"
    )
    public void consume(
            UserMessage request,
            Acknowledgment acknowledgment
    ) {
        process(request)
                .doOnSuccess(ignored -> {
                    acknowledgment.acknowledge();

                    log.info(
                            "Processed and committed jobId={}, email={}",
                            request.getJobId(),
                            request.getEmail()
                    );
                })
                .doOnError(ex -> log.error(
                        "Failed to process jobId={}, email={}. Offset will not be committed",
                        request != null ? request.getJobId() : null,
                        request != null ? request.getEmail() : null,
                        ex
                ))
                .block();
    }

    private Mono<Void> process(UserMessage request) {
        return Mono.defer(() -> {
            if (request == null
                    || request.getJobId() == null
                    || request.getEmail() == null) {
                return Mono.error(new IllegalArgumentException("Invalid request"));
            }

            return comparisonService.compare(request.getEmail())
                    .map(inconsistencies -> inconsistencies.stream()
                            .map(Inconsistency::getMessage)
                            .toList()
                    )
                    .flatMap(messages ->
                            jobResultAggregatorService.addResult(
                                    new ResultMessage(
                                            request.getJobId(),
                                            request.getEmail(),
                                            ResultMessageStatus.SUCCESS,
                                            messages
                                    )
                            )
                    )
                    .then();
        });
    }
}