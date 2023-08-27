package com.tsong.cmall.shopping_cart.web;

import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.entity.ShoppingCartItem;
import com.tsong.cmall.shopping_cart.service.IShoppingCartService;
import com.tsong.cmall.shopping_cart.web.params.CartConfirmParam;
import com.tsong.cmall.shopping_cart.web.params.SaveCartItemParam;
import com.tsong.cmall.shopping_cart.web.params.UpdateCartItemParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @Author Tsong
 * @Date 2023/4/2 21:49
 */
@RestController
@Tag(name = "Shopping Cart", description = "1-5.商城购物车相关接口")
@RequestMapping("/api/shopping-cart")
public class ShoppingCartAPI {
    @Autowired
    private IShoppingCartService shoppingCartService;

    @GetMapping("/")
    @Operation(summary = "购物车列表(网页移动端不分页)", description = "")
    public Result cartItemList(Long userId) {
        return ResultGenerator.genSuccessResult(shoppingCartService.getMyShoppingCartItems(userId));
    }

    @PostMapping("/")
    @Operation(summary = "添加商品到购物车接口", description = "传参为商品id、数量")
    public Result saveShoppingCartItem(@Parameter(name = "保存购物车项参数") @RequestBody @Valid SaveCartItemParam saveCartItemParam,
                                       Long userId) {
        String saveResult = shoppingCartService.saveShoppingCartItem(saveCartItemParam, userId);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/")
    @Operation(summary = "修改购物项数据", description = "传参为购物项id、数量")
    public Result updateShoppingCartItem(@Parameter(name = "更新购物车项参数") @RequestBody @Valid UpdateCartItemParam updateCartItemParam,
                                         Long userId) {
        String updateResult = shoppingCartService.updateShoppingCartItem(updateCartItemParam, userId);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/{ShoppingCartItemId}")
    @Operation(summary = "删除购物项", description = "传参为购物项id")
    public Result updateShoppingCartItem(@Parameter(name = "购物车项id") @PathVariable("ShoppingCartItemId") Long shoppingCartItemId,
                                         Long userId) {
        ShoppingCartItem shoppingCartItem = shoppingCartService.getShoppingCartItemById(shoppingCartItemId);
        if (userId.equals(shoppingCartItem.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDDEN_ERROR.getResult());
        }
        Boolean deleteResult = shoppingCartService.deleteById(shoppingCartItemId, userId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @PostMapping("/confirm")
    @Operation(summary = "根据购物项id数组查询购物项明细和可用优惠券", description = "确认订单页面使用")
    public Result confirmCartItem(@Parameter(name = "购物车项id列表") @RequestBody @Valid CartConfirmParam cartConfirmParam,
                                  Long userId) {
        Long[] cartItemIds = cartConfirmParam.getCartItemIds();
        return ResultGenerator.genSuccessResult(shoppingCartService.getCartItemsForConfirmPage(Arrays.asList(cartItemIds), userId));
    }
}
