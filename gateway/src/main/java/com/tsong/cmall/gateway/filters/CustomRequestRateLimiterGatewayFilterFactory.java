package com.tsong.cmall.gateway.filters;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.HttpStatusHolder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author Tsong
 * @Date 2024/12/30 03:53
 */
//@Component
public class CustomRequestRateLimiterGatewayFilterFactory extends RequestRateLimiterGatewayFilterFactory {

    private final RateLimiter defaultRateLimiter;
    private final KeyResolver defaultKeyResolver;

    public CustomRequestRateLimiterGatewayFilterFactory(RateLimiter defaultRateLimiter, @Qualifier("keyResolver") KeyResolver defaultKeyResolver) {
        super(defaultRateLimiter, defaultKeyResolver);
        this.defaultRateLimiter = defaultRateLimiter;
        this.defaultKeyResolver = defaultKeyResolver;
    }

    @Override
    public GatewayFilter apply(Config config) {
        KeyResolver resolver = (config.getKeyResolver() == null) ? defaultKeyResolver : config.getKeyResolver();
        RateLimiter<Object> limiter = (config.getRateLimiter() == null) ? defaultRateLimiter : config.getRateLimiter();
        boolean denyEmpty = config.getDenyEmptyKey() == null ? false : config.getDenyEmptyKey();
        HttpStatusHolder emptyKeyStatus = HttpStatusHolder.parse(config.getEmptyKeyStatus() == null ? this.getEmptyKeyStatusCode() : config.getEmptyKeyStatus());
        return (exchange, chain) -> {
            return resolver.resolve(exchange).defaultIfEmpty("____EMPTY_KEY__").flatMap((key) -> {
                if ("____EMPTY_KEY__".equals(key)) {
                    if (denyEmpty) {
                        ServerWebExchangeUtils.setResponseStatus(exchange, emptyKeyStatus);
                        return exchange.getResponse().setComplete();
                    } else {
                        return chain.filter(exchange);
                    }
                } else {
                    String routeId = config.getRouteId();
                    if (routeId == null) {
                        Route route = (Route)exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                        routeId = route.getId();
                    }

                    return limiter.isAllowed(routeId, key).flatMap((response) -> {
                        Iterator var4 = response.getHeaders().entrySet().iterator();

                        while(var4.hasNext()) {
                            Map.Entry<String, String> header = (Map.Entry)var4.next();
                            exchange.getResponse().getHeaders().add((String)header.getKey(), (String)header.getValue());
                        }

                        if (response.isAllowed()) {
                            return chain.filter(exchange);
                        } else {
                            ServerWebExchangeUtils.setResponseStatus(exchange, config.getStatusCode());
                            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            String responseBody = "{ \"resultCode\": 429, \"message\": \"请求被限流，请稍后重试\" }";
                            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
                            return exchange.getResponse().writeWith(Mono.just(buffer));
                        }
                    });
                }
            });
        };
    }

}
