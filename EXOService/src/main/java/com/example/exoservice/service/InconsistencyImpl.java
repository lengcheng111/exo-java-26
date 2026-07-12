package com.example.exoservice.service;

import com.example.exoservice.config.UserApiClient;
import com.example.exoservice.dto.UserMessage;
import org.springframework.stereotype.Service;

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
    public String getInconsistency() {

        List<String> emails = apiClient.getEmails();

        if (emails == null || emails.isEmpty()) {
            return null;
        }

        String jobId = UUID.randomUUID().toString();

        emails = emails.stream().distinct().toList();

        aggregatorService.createJob(jobId, emails.size());

        for (String email : emails) {
            producer.publish(new UserMessage(jobId, email));
        }

        return jobId;
    }
}
