package com.tsong.cmall.gateway.filters.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * @Author Tsong
 * @Date 2024/12/30 02:55
 */
@Configuration
public class RateLimiterConfig {
    @Bean
    public KeyResolver keyResolver() {
        return exchange -> {
            // 使用请求路径作为限流的键
            String url = exchange.getRequest().getPath().toString();
            return Mono.just(url);
        };
    }
}
