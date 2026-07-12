package com.example.exoservice.config;

import com.example.exoservice.dto.ResultMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    @Primary
    public ReactiveRedisTemplate<String, ResultMessage> resultRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();

        JacksonJsonRedisSerializer<ResultMessage> valueSerializer =
                new JacksonJsonRedisSerializer<>(ResultMessage.class);

        RedisSerializationContext<String, ResultMessage> context =
                RedisSerializationContext
                        .<String, ResultMessage>newSerializationContext(keySerializer)
                        .key(keySerializer)
                        .hashKey(keySerializer)
                        .value(valueSerializer)
                        .hashValue(valueSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(
                connectionFactory,
                context
        );
    }

    public ReactiveRedisTemplate<String, String> stringRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();

        JacksonJsonRedisSerializer<String> valueSerializer =
                new JacksonJsonRedisSerializer<>(String.class);

        RedisSerializationContext<String, String> context =
                RedisSerializationContext
                        .<String, String>newSerializationContext(keySerializer)
                        .key(keySerializer)
                        .hashKey(keySerializer)
                        .value(valueSerializer)
                        .hashValue(valueSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(
                connectionFactory,
                context
        );
    }
}