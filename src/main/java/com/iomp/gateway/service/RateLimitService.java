package com.iomp.gateway.service;

import java.time.Duration;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class RateLimitService {

    private final ReactiveStringRedisTemplate redisTemplate;

    private static final long REQUEST_LIMIT = 100;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    public RateLimitService(
            ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> isAllowed(String username) {

        String key = "rate_limit:user:" + username;

        return redisTemplate.opsForValue()
                .increment(key)
                .flatMap(count -> {

                    if (count == 1) {
                        return redisTemplate.expire(key, WINDOW)
                                .thenReturn(true);
                    }

                    return Mono.just(count <= REQUEST_LIMIT);
                });
    }
}