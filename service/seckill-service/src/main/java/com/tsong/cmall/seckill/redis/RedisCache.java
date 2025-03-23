package com.tsong.cmall.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
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
     * string类型递增
     *
     * @param key 缓存的键值
     * @return 递增后返回值
     */
    public Long increment(final String key) {
        if (redisTemplate.hasKey(key)){
            return redisTemplate.opsForValue().increment(key);
        }
        return 0L;
    }

    /**
     * 库存递减且登记userId
     *
     * @param stockKey redis键
     * @return 递减后返回值
     */
    public Long luaDecrement(final String stockKey, final String recordKey, final Long userId) {
        // 大于0才自减
        String lua = """
                local c = redis.call('get',KEYS[1])
                if c and tonumber(c) == 0 then
                    return -1;
                end
                
                local record = redis.call('sismember',KEYS[2],ARGV[1])
                if record == 1 then
                    return -2;
                end
                
                c = redis.call('decr',KEYS[1])
                
                redis.call('sadd',KEYS[2],ARGV[1])
                
                return c;""";

        RedisScript<Long> redisScript = new DefaultRedisScript<>(lua, Long.class);
        // execute(script, KEYS, ARGV)
        Number execute = (Number) redisTemplate.execute(redisScript, List.of(stockKey, recordKey), userId);
        if (execute == null) {
            return -1L;
        }
        return execute.longValue();
    }

    /**
     * 设置redis键值对
     *
     * @param key   redis键
     * @param value redis值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> void setNXCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 设置redis键值对
     *
     * @param key      redis键
     * @param value    redis值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public <T> void setNXCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
    }


    /**
     * 获得缓存的基本对象
     *
     * @param key redis键
     * @return redis值
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key redis键
     * @return boolean
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    public Long luaDeleteUrlVO(final String key) {
        String lua = """
                local urlJSON = redis.call('get',KEYS[1])
                if urlJSON then
                    local url = cjson.decode(urlJSON)
                    if url.seckillStatusEnum == 'START' then 
                        redis.call('del',KEYS[1])
                        return 1
                    end
                end
                return 0
                """;
        RedisScript<Long> redisScript = new DefaultRedisScript<>(lua, Long.class);
        // execute(script, KEYS, ARGV)
        Number execute = (Number) redisTemplate.execute(redisScript, List.of(key));
        if (execute == null) {
            return 0L;
        }
        return execute.longValue();
    }

    /**
     *  移除缓存Set中的元素
     * @param key
     * @param value
     * @return
     */
    public Long deleteCacheSetMember(final String key, final Object value) {
        return redisTemplate.opsForSet().remove(key,value);
    }

    /**
     * 判断key-set中是否存在value
     *
     * @param key   缓存键值
     * @param value 缓存的数据
     * @return boolean
     */
    public Boolean containsCacheSet(final String key, final Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }
}
