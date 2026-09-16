package com.iomp.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;

@Configuration
public class RedisConfig {

    @Bean
    ReactiveStringRedisTemplate reactiveStringRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        return new ReactiveStringRedisTemplate(connectionFactory);
    }
}