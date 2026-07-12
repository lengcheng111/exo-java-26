package com.example.exoservice.service;

import com.example.exoservice.config.UserApiClient;
import com.example.exoservice.dto.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
public class InconsistencyImpl implements InconsistencyService {
    private final UserApiClient apiClient;
    private final UserProducer producer;
    private final AggregatorService aggregatorService;

    public InconsistencyImpl(UserApiClient apiClient, UserProducer producer, AggregatorService aggregatorService) {
        this.apiClient = apiClient;
        this.producer = producer;
        this.aggregatorService = aggregatorService;
    }

    @Override
    public Mono<String> getInconsistency() {
        return Mono.fromCallable(apiClient::getEmails)
                .flatMap(emails -> {
                    if (emails.isEmpty()) {
                        return Mono.empty();
                    }

                    List<String> distinctEmails = emails.stream()
                            .distinct()
                            .toList();

                    String jobId = UUID.randomUUID().toString();

                    return aggregatorService.createJob(jobId, distinctEmails.size())
                            .thenMany(
                                    Flux.fromIterable(distinctEmails)
                                            .flatMap(email ->
                                                    Mono.fromRunnable(() ->
                                                            producer.publish(new UserMessage(jobId, email))
                                                    )
                                            )
                            )
                            .then(Mono.just(jobId));
                });
    }
}
