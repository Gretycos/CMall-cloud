package com.tsong.cmall.order.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author Tsong
 * @Date 2023/3/24 19:10
 */
@Component
public class RedisCache {
    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 尝试加锁
     *
     * @param lockKey 锁的键
     * @param expireTime 锁的过期时间（单位：秒）
     * @return 返回锁的唯一标识，用于解锁
     */
    public String tryLock(String lockKey, long expireTime) {
        String lockValue = UUID.randomUUID().toString(); // 生成唯一标识
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, expireTime, TimeUnit.SECONDS); // NX + EX
        return Boolean.TRUE.equals(success) ? lockValue : null;
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁的键
     * @param lockValue 锁的唯一标识
     * @return 是否成功释放
     */
    public boolean unlock(String lockKey, String lockValue) {
        String luaScript = """
                if redis.call('get', KEYS[1]) == ARGV[1] then
                    return redis.call('del', KEYS[1])
                else
                    return 0
                end
                """;
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        // 执行 Lua 脚本，确保释放锁的原子性
        Number execute = (Number) redisTemplate.execute(redisScript, List.of(lockKey), lockValue);

        return execute != null && execute.longValue() > 0;
    }

}
