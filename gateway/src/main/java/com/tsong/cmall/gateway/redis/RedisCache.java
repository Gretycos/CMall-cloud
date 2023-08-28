package com.tsong.cmall.gateway.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * @Author Tsong
 * @Date 2023/3/24 19:10
 */
@Component
public class RedisCache {
    @Autowired
    public RedisTemplate redisTemplate;

    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

}
