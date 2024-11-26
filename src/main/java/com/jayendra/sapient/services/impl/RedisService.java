package com.jayendra.sapient.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setKeyWithTTL(String key, Object value, long ttlInSeconds) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value.toString(), Duration.ofSeconds(ttlInSeconds));
    }

    public Object getKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}