package com.example.exoconsumerservice.config;

import com.example.exoconsumerservice.dto.ResultMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ResultMessage> resultRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, ResultMessage> template =
                new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(
                new JacksonJsonRedisSerializer<>(ResultMessage.class)
        );

        template.afterPropertiesSet();
        return template;
    }
}