package com.tsong.cmall.gateway.filters;

import com.tsong.cmall.gateway.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.tsong.cmall.common.constants.Constants.MALL_USER_TOKEN_KEY;
import static com.tsong.cmall.common.constants.Constants.TOKEN_LENGTH;

/**
 * @Author Tsong
 * @Date 2023/8/28 16:12
 */
@Order(10)
@Component
public class AuthFilter implements GlobalFilter {
    @Autowired
    private RedisCache redisCache;
    private static AntPathMatcher antPathMatcher;
    private static List<String> whiteList = new ArrayList<>();
    static {
        antPathMatcher = new AntPathMatcher();
        whiteList.add("/api/homepage/**");
        whiteList.add("/api/user/login");
        whiteList.add("/api/user/register");
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求头
        ServerHttpRequest request = exchange.getRequest();
        if (isInWhiteList(request.getPath().toString())){
            // 白名单直接放行
            return chain.filter(exchange);
        }
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get("token");
        if (tokens != null && !tokens.isEmpty()){
            String token = tokens.get(0);
            if (StringUtils.hasLength(token) && token.length() == TOKEN_LENGTH){
                Long userId  = redisCache.getCacheObject(MALL_USER_TOKEN_KEY + token);
                if (userId != null){
                    // 获取参数表
                    MultiValueMap<String, String> queryParams = request.getQueryParams();
                    // 设置参数
                    queryParams.set("userId", userId.toString());
                    // 修改请求并放行
                    return chain.filter(exchange.mutate().request(request).build());
                }
            }
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private boolean isInWhiteList(String path) {
        for (String pattern : whiteList) {
            if (antPathMatcher.match(pattern, path)){
                return true;
            }
        }
        return false;
    }
}
