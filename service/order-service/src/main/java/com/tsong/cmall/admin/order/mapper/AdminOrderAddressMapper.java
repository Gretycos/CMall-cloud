package com.tsong.cmall.admin.order.mapper;

import com.tsong.cmall.entity.OrderAddress;

/**
 * @Author: Tsong
 * @date: 2023/03/27/05:14
 */
public interface AdminOrderAddressMapper {
    OrderAddress selectByPrimaryKey(Long orderId);
}