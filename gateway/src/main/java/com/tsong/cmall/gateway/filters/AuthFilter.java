package com.tsong.cmall.gateway.filters;

import com.tsong.cmall.gateway.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.tsong.cmall.common.constants.Constants.*;

/**
 * @Author Tsong
 * @Date 2023/8/28 16:12
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    @Autowired
    private RedisCache redisCache;
    private static AntPathMatcher antPathMatcher;
    private static List<String> whiteList = new ArrayList<>();
    static {
        antPathMatcher = new AntPathMatcher();
        whiteList.add("/api/homepage/**");
        whiteList.add("/admin/user/login");
        whiteList.add("/api/user/login");
        whiteList.add("/api/user/register");
        whiteList.add("/upload/**");

    }

    private enum Header {
        USER("userId", MALL_USER_ID_TOKEN_KEY),
        ADMIN("adminId", ADMIN_USER_ID_TOKEN_KEY);
        private final String headerKey;
        private final String redisKey;
        Header(String headerKey, String redisKey){
            this.headerKey = headerKey;
            this.redisKey = redisKey;
        }

        public String getHeaderKey() {
            return headerKey;
        }

        public String getRedisKey() {
            return redisKey;
        }
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
        List<String> userIds = headers.get("user-id");
        List<String> adminIds = headers.get("admin-id");
        if (tokens != null && !tokens.isEmpty()){
            String token = tokens.get(0);
            ServerHttpRequest newRequest = null;
            if (userIds != null && !userIds.isEmpty()) {
                // 是用户
                Long userId = Long.valueOf(userIds.get(0));
                newRequest = getNewRequest(request, token, userId, Header.USER);
            } else if (adminIds != null && !adminIds.isEmpty()){
                // 是管理员
                Long adminId = Long.valueOf(adminIds.get(0));
                newRequest = getNewRequest(request, token, adminId, Header.ADMIN);
            }
            if (newRequest != null) {
                return chain.filter(exchange.mutate().request(newRequest).build());
            }
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private ServerHttpRequest getNewRequest(ServerHttpRequest request, String token, Long id,  Header header) {
        if (StringUtils.hasLength(token) && token.length() == TOKEN_LENGTH){
            // 用id找token，再比对token的效率更高
            String tokenStored  = redisCache.getCacheObject(header.getRedisKey() + id);
            // 登录过且token一致
            if (StringUtils.hasLength(tokenStored) && tokenStored.equals(token)){
                URI uri = request.getURI();
                String originalQuery = uri.getQuery();
                StringBuilder query = new StringBuilder((originalQuery == null ? "" : originalQuery));
                // 有参数
                if (StringUtils.hasText(originalQuery)
                        && originalQuery.charAt(originalQuery.length() - 1) != '&'){
                    query.append('&');
                }
                // 新增参数
                query.append(header.getHeaderKey() + "=" + id);
                // 修改uri
                URI newUri = UriComponentsBuilder.fromUri(uri)
                        .replaceQuery(query.toString()).build().toUri();
                // 修改请求并放行
                return request.mutate().uri(newUri).build();
            }
        }
        return null;
    }

    private boolean isInWhiteList(String path) {
        for (String pattern : whiteList) {
            if (antPathMatcher.match(pattern, path)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
