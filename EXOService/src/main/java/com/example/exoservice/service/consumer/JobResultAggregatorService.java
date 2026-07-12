package com.example.exoservice.service.consumer;

import com.example.exoservice.dto.ResultMessage;
import com.example.exoservice.helper.KeyUtil;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class JobResultAggregatorService {

    private static final Duration TTL = Duration.ofHours(24);

    private final ReactiveRedisTemplate<String, String> stringRedisTemplate;
    private final ReactiveRedisTemplate<String, ResultMessage> resultRedisTemplate;

    public JobResultAggregatorService(ReactiveRedisTemplate<String, String> stringRedisTemplate,
                                      ReactiveRedisTemplate<String, ResultMessage> resultRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.resultRedisTemplate = resultRedisTemplate;
    }

    public Mono<Void> addResult(ResultMessage result) {
        String jobKey = KeyUtil.makeJobKey(result.getJobId());
        String resultKey = KeyUtil.makeResultKey(result.getJobId());

        return resultRedisTemplate.hasKey(jobKey)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(
                        new IllegalArgumentException("Job not found: " + result.getJobId())))
                .flatMap(ignored ->
                        resultRedisTemplate.opsForList()
                                .rightPush(resultKey, result)
                                .then(stringRedisTemplate.expire(jobKey, TTL))
                                .then(stringRedisTemplate.expire(resultKey, TTL))
                )
                .then();
    }


}