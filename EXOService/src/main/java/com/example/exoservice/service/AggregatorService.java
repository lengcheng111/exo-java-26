package com.example.exoservice.service;


import com.example.exoservice.dto.JobState;
import com.example.exoservice.dto.ResultMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

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

    public void createJob(String jobId, int total) {
        String key = jobKey(jobId);

        stringRedisTemplate.opsForHash().putAll(
                key,
                Map.of(
                        "total", String.valueOf(total),
                        "done", "0",
                        "status", "PROCESSING"
                )
        );

        stringRedisTemplate.expire(key, TTL);
        resultRedisTemplate.expire(resultKey(jobId), TTL);
    }

    public JobState get(String jobId) {
        String key = jobKey(jobId);

        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return null;
        }

        int total = Integer.parseInt(getValue(key, "total"));

        List<ResultMessage> results = resultRedisTemplate
                .opsForList()
                .range(resultKey(jobId), 0, -1);

        return new JobState(
                total,
                results == null ? List.of() : results
        );
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