package com.tsong.cmall.shopping_cart.mapper;

import com.tsong.cmall.entity.ShoppingCartItem;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface ShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insertSelective(ShoppingCartItem row);

    ShoppingCartItem selectByPrimaryKey(Long cartItemId);

    int updateByPrimaryKeySelective(ShoppingCartItem row);

    List<ShoppingCartItem> selectByPrimaryKeys(List<Long> cartItemIds);

    ShoppingCartItem selectByUserIdAndGoodsId(Long mallUserId, Long goodsId);

    List<ShoppingCartItem> selectByUserIdAndCartItemIds(Long mallUserId, List<Long> cartItemIds);

    List<ShoppingCartItem> selectByUserId(Long mallUserId, int number);

    int selectCountByUserId(Long mallUserId);

    int deleteBatch(List<Long> ids);
}