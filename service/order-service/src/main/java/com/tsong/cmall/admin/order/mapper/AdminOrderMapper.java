package com.tsong.cmall.admin.order.mapper;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface AdminOrderMapper {
    Order selectByPrimaryKey(Long orderId);

    List<Order> selectOrderList(PageQueryUtil pageUtil);

    int getTotalOrders(PageQueryUtil pageUtil);

    List<Order> selectByPrimaryKeys(@Param("orderIds")List<Long> orderIds);

    int checkOut(@Param("orderIds")List<Long> orderIds);

    int closeOrder(@Param("orderIds")List<Long> orderIds, int orderStatus);

    int checkDone(@Param("orderIds") List<Long> orderIds);
}