package com.tsong.cmall.order.mapper;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.entity.Order;
import com.tsong.cmall.order.mapper.dto.OrderPayStatusDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Tsong
 * @date: 2023/03/20/09:31
 */
public interface OrderMapper {
    int insertSelective(Order row);

    Order selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(Order row);

    int updateByPrimaryKey(Order row);

    OrderPayStatusDTO selectStatusByPrimaryKey(Long orderId);

    Order selectByOrderNo(String orderNo);

    List<Order> selectOrderList(PageQueryUtil pageUtil);

    int closeOrder(@Param("orderIds")List<Long> orderIds, int orderStatus);

    String selectOrderNoByUserIdAndSeckillId(Long userId, Long seckillId);
}