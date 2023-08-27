package com.tsong.feign.clients.shopping_cart;

/**
 * @Author Tsong
 * @Date 2023/8/22 13:09
 */

import com.tsong.cmall.common.util.Result;
import com.tsong.feign.clients.shopping_cart.fallback.ShoppingCartClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "shopping-cart-service", fallbackFactory = ShoppingCartClientFallbackFactory.class)
public interface ShoppingCartClient {
    String RPC_SUFFIX = "/rpc/shopping-cart";

    @DeleteMapping(RPC_SUFFIX + "/batch")
    Result deleteCartItemsByIds(List<Long> cartItemIds);

    @GetMapping(RPC_SUFFIX + "/cartItems")
    Result getCartItemsByIds(List<Long> cartItemIds);
}
