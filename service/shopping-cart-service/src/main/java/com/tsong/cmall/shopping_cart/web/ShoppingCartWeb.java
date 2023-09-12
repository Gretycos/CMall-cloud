package com.tsong.cmall.shopping_cart.web;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.shopping_cart.service.IShoppingCartService;
import com.tsong.cmall.vo.shopping_cart.ShoppingCartItemVO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/8/22 13:21
 */
@RestController
@RequestMapping("/rpc/shopping-cart")
public class ShoppingCartWeb {
    @Autowired
    private IShoppingCartService shoppingCartService;

    @DeleteMapping("/batch")
    @Operation(summary = "用ids删除购物车项", description = "rpc")
    public Result<Boolean> deleteCartItemsByIds(@RequestBody List<Long> cartItemIds){
        return ResultGenerator.genSuccessResult(shoppingCartService.deleteByIds(cartItemIds));
    }

    @GetMapping("/cartItems")
    @Operation(summary = "用ids查询购物车项", description = "rpc")
    Result<List<ShoppingCartItemVO>> getCartItemsByIds(@RequestParam List<Long> cartItemIds){
        return ResultGenerator.genSuccessResult(shoppingCartService.getShoppingCartItemsByIds(cartItemIds));
    }
}
