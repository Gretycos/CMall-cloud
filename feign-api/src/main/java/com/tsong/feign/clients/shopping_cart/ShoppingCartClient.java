package com.tsong.feign.clients.shopping_cart;

/**
 * @Author Tsong
 * @Date 2023/8/22 13:09
 */

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.vo.shopping_cart.ShoppingCartItemVO;
import com.tsong.feign.clients.shopping_cart.fallback.ShoppingCartClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "shopping-cart-service", fallbackFactory = ShoppingCartClientFallbackFactory.class)
public interface ShoppingCartClient {
    String RPC_SUFFIX = "/rpc/shopping-cart";

    @DeleteMapping(RPC_SUFFIX + "/batch")
    Result<Boolean> deleteCartItemsByIds(@RequestBody List<Long> cartItemIds);

    @GetMapping(RPC_SUFFIX + "/cartItems")
    Result<List<ShoppingCartItemVO>> getCartItemsByIds(@RequestParam List<Long> cartItemIds);
}
