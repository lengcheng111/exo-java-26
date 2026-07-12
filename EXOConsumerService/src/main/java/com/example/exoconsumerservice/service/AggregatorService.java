package com.example.exoconsumerservice.service;


import com.example.exoconsumerservice.dto.ResultMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AggregatorService {

    private static final Duration TTL = Duration.ofHours(24);

    private final RedisTemplate<String, ResultMessage> resultRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    public AggregatorService(
            RedisTemplate<String, ResultMessage> resultRedisTemplate,
            RedisTemplate<String, String> stringRedisTemplate) {

        this.resultRedisTemplate = resultRedisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void add(ResultMessage result) {
        String jobKey = jobKey(result.jobId());

        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(jobKey))) {
            throw new IllegalArgumentException(
                    "Job not found: " + result.jobId()
            );
        }

        resultRedisTemplate.opsForList()
                .rightPush(resultKey(result.jobId()), result);

        Long done = stringRedisTemplate.opsForHash()
                .increment(jobKey, "done", 1);

        int total = Integer.parseInt(
                getValue(jobKey, "total")
        );

        if (done != null && done >= total) {
            stringRedisTemplate.opsForHash()
                    .put(jobKey, "status", "COMPLETED");
        }

        stringRedisTemplate.expire(jobKey, TTL);
        resultRedisTemplate.expire(resultKey(result.jobId()), TTL);
    }

    private String getValue(String key, String field) {
        Object value = stringRedisTemplate.opsForHash().get(key, field);

        if (value == null) {
            throw new IllegalStateException(
                    "Missing Redis field: " + field
            );
        }

        return value.toString();
    }

    private String jobKey(String jobId) {
        return "job:" + jobId;
    }

    private String resultKey(String jobId) {
        return "job:" + jobId + ":results";
    }
}