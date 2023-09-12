package com.tsong.cmall.admin.order.mapper;

import com.tsong.cmall.entity.OrderItem;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface AdminOrderItemMapper {

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<OrderItem> selectByOrderId(Long orderId);
}