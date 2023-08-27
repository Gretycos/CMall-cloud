package com.tsong.cmall.order.mapper;

import com.tsong.cmall.entity.OrderAddress;

/**
 * @Author: Tsong
 * @date: 2023/03/27/05:14
 */
public interface OrderAddressMapper {
    int insertSelective(OrderAddress row);

    OrderAddress selectByPrimaryKey(Long orderId);
}