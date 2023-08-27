package com.tsong.feign.clients.shopping_cart.fallback;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.feign.clients.shopping_cart.ShoppingCartClient;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/8/22 13:47
 */
public class ShoppingCartClientFallbackFactory implements FallbackFactory<ShoppingCartClient> {
    @Override
    public ShoppingCartClient create(Throwable cause) {
        return new ShoppingCartClient() {
            Result result = ResultGenerator.genFailResult(cause.getMessage());
            @Override
            public Result deleteCartItemsByIds(List<Long> cartItemIds) {
                return result;
            }

            @Override
            public Result getCartItemsByIds(List<Long> cartItemIds) {
                return result;
            }
        };
    }
}
