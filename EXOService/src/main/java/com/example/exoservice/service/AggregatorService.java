package com.example.exoservice.service;


import com.example.exoservice.dto.JobState;
import com.example.exoservice.dto.ResultMessage;
import com.example.exoservice.helper.KeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
public class AggregatorService {

    private static final Logger log = LoggerFactory.getLogger(AggregatorService.class);


    private static final Duration TTL = Duration.ofHours(24);

    private final ReactiveRedisTemplate<String, String> stringRedisTemplate;
    private final ReactiveRedisTemplate<String, ResultMessage> resultRedisTemplate;

    public AggregatorService(ReactiveRedisTemplate<String, String> stringRedisTemplate,
                             ReactiveRedisTemplate<String, ResultMessage> resultRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;

        this.resultRedisTemplate = resultRedisTemplate;
    }

    public Mono<Void> createJob(String jobId, int total) {
        String key = KeyUtil.makeJobKey(jobId);

        return stringRedisTemplate.opsForHash()
                .putAll(
                        key,
                        Map.of("total", String.valueOf(total))
                )
                .then(stringRedisTemplate.expire(key, TTL))
                .then(resultRedisTemplate.expire(KeyUtil.makeResultKey(jobId), TTL))
                .then();
    }

    public Mono<JobState> get(String jobId) {
        String jobKey = KeyUtil.makeJobKey(jobId);
        String resultKey = KeyUtil.makeResultKey(jobId);

        log.info("Getting job state: jobKey={}, resultKey={}", jobKey, resultKey);

        return resultRedisTemplate.hasKey(jobKey)
                .doOnNext(exists ->
                        log.info("Redis hasKey({}) = {}", jobKey, exists)
                )
                .filter(Boolean::booleanValue)
                .flatMap(exists ->
                        stringRedisTemplate.opsForHash()
                                .get(jobKey, "total")
                                .doOnNext(total ->
                                        log.info("Redis hash [{}][total] = {}", jobKey, total)
                                )
                )
                .flatMap(total ->
                        resultRedisTemplate.opsForList()
                                .range(resultKey, 0, -1)
                                .doOnNext(result ->
                                        log.info("Redis list item: {}", result)
                                )
                                .collectList()
                                .doOnNext(results ->
                                        log.info("Redis list size = {}, data = {}", results.size(), results)
                                )
                                .map(results -> new JobState(
                                        Integer.parseInt(total.toString()),
                                        results
                                ))
                )
                .doOnSuccess(jobState ->
                        log.info("Final JobState = {}", jobState)
                )
                .doOnError(error ->
                        log.error("Error getting job state for jobId={}", jobId, error)
                );
    }
}