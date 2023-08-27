package com.tsong.cmall.order.mapper;

import com.tsong.cmall.entity.OrderItem;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface OrderItemMapper {
    int insertSelective(OrderItem row);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<OrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<OrderItem> selectByOrderIds(List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(List<OrderItem> orderItems);
}