package com.example.exoservice.config;

import com.example.exoservice.helper.KeyUtil;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.consumer.retry.interval}") long interval,
            @Value("${kafka.consumer.retry.attempts}") long attempts) {

        long retryCount = Math.max(0, attempts - 1);

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, exception) ->
                                new TopicPartition(
                                        record.topic() + KeyUtil.SUFFIX_DLT_TOPIC,
                                        record.partition()
                                )
                );

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(
                        recoverer,
                        new FixedBackOff(interval, retryCount)
                );

        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class
        );

        return errorHandler;
    }
}