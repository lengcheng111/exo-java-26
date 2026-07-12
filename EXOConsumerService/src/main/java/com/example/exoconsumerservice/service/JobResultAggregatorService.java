package com.example.exoconsumerservice.service;


import com.example.exoconsumerservice.dto.ResultMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class JobResultAggregatorService {

    private static final Duration TTL = Duration.ofHours(24);

    private final RedisTemplate<String, ResultMessage> resultRedisTemplate;

    public JobResultAggregatorService(RedisTemplate<String, ResultMessage> resultRedisTemplate) {
        this.resultRedisTemplate = resultRedisTemplate;
    }

    public void recordResult(ResultMessage result) {
        String jobKey = jobKey(result.jobId());

        if (!Boolean.TRUE.equals(resultRedisTemplate.hasKey(jobKey))) {
            throw new IllegalArgumentException(
                    "Job not found: " + result.jobId()
            );
        }

        resultRedisTemplate.opsForList()
                .rightPush(resultKey(result.jobId()), result);

        resultRedisTemplate.expire(jobKey(result.jobId()), TTL);
        resultRedisTemplate.expire(resultKey(result.jobId()), TTL);
    }

    private String jobKey(String jobId) {
        return "job:" + jobId;
    }

    private String resultKey(String jobId) {
        return "job:" + jobId + ":results";
    }
}