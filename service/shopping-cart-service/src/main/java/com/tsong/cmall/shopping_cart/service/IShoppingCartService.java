package com.tsong.cmall.shopping_cart.service;

import com.tsong.cmall.entity.ShoppingCartItem;
import com.tsong.cmall.shopping_cart.web.params.SaveCartItemParam;
import com.tsong.cmall.shopping_cart.web.params.UpdateCartItemParam;
import com.tsong.cmall.shopping_cart.web.vo.ShoppingCartConfirmVO;
import com.tsong.cmall.vo.ShoppingCartItemVO;

import java.util.List;

public interface IShoppingCartService {
    /**
     * @Description 保存商品至购物车中
     * @Param [shoppingCartItem]
     * @Return java.lang.String
     */
    String saveShoppingCartItem(SaveCartItemParam saveCartItemParam, Long userId);

    /**
     * @Description 修改购物车中的属性
     * @Param [shoppingCartItem]
     * @Return java.lang.String
     */
    String updateShoppingCartItem(UpdateCartItemParam updateCartItemParam, Long userId);

    /**
     * @Description 获取购物项详情
     * @Param [shoppingCartItemId]
     * @Return com.tsong.cmall.entity.ShoppingCartItem
     */
    ShoppingCartItem getShoppingCartItemById(Long shoppingCartItemId);

    /**
     * @Description 结算页购物项列表
     * @Param [cartItemIds, userId]
     * @Return java.util.List<com.tsong.cmall.controller.vo.ShoppingCartItemVO>
     */
    ShoppingCartConfirmVO getCartItemsForConfirmPage(List<Long> cartItemIds, Long userId);

    /**
     * @Description 删除购物车中的商品
     * @Param [shoppingCartItemId, userId]
     * @Return java.lang.Boolean
     */
    Boolean deleteById(Long shoppingCartItemId, Long userId);

    /**
     * @Description 获取我的购物车中的列表数据
     * @Param [mallUserId]
     * @Return java.util.List<com.tsong.cmall.controller.vo.ShoppingCartItemVO>
     */
    List<ShoppingCartItemVO> getMyShoppingCartItems(Long mallUserId);

    /**
     * @Description 批量删除购物车项
     * @Param [cartItemIds]
     * @Return java.lang.Boolean
     */
    Boolean deleteByIds(List<Long> cartItemIds);

    List<ShoppingCartItemVO> getShoppingCartItemsByIds(List<Long> cartItemIds);
}
