package com.tsong.cmall.order.service;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.Order;
import com.tsong.cmall.order.mapper.dto.OrderPayStatusDTO;
import com.tsong.cmall.order.web.vo.OrderDetailVO;
import com.tsong.cmall.order.web.vo.OrderItemVO;

import java.math.BigDecimal;
import java.util.List;

public interface IOrderService {

    /**
     * @Description 保存订单
     * @Param [user, couponUserId, myShoppingCartItems]
     * @Return java.lang.String
     */
    String saveOrder(Long userId, Long couponUserId, Long addressId, Long[] cartItemIds);

    /**
     * @Description 获取秒杀订单号
     * @Param [user, couponUserId, myShoppingCartItems]
     * @Return java.lang.String
     */
    String getSeckillOrderNo(Long userId, Long seckillId, Long seckillSuccessId, String seckillSecretKey);

    /**
     * @Description 用订单id获取订单详情
     * @Param [orderId]
     * @Return com.tsong.cmall.controller.vo.OrderDetailVO
     */
    OrderDetailVO getOrderDetailByOrderId(Long orderId);

    /**
     * @Description 获取订单详情，用于返回前端
     * @Param [orderNo, userId]
     * @Return com.tsong.cmall.controller.vo.OrderDetailVO
     */
    OrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * @Description 获取订单详情
     * @Param [orderNo]
     * @Return com.tsong.cmall.entity.Order
     */
    Order getOrderByOrderNo(String orderNo);

    /**
     * @Description 查询订单的状态（支付状态、订单状态）
     * @Param [orderId]
     * @Return int
     */
    OrderPayStatusDTO getOrderStatus(Long orderId);

    /**
     * @Description 我的订单列表
     * @Param [pageUtil]
     * @Return com.tsong.cmall.util.PageResult
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * @Description 手动取消订单
     * @Param [orderNo, userId]
     * @Return java.lang.String
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * @Description 删除订单（隐藏）
     * @Param [orderNo, userId]
     * @Return java.lang.String
     */
    String deleteOrder(String orderNo, Long userId);

    /**
     * @Description 确认收货
     * @Param [orderNo, userId]
     * @Return java.lang.String
     */
    String finishOrder(String orderNo, Long userId);

    /**
     * @Description 支付成功
     * @Param [orderNo, payType]
     * @Return java.lang.String
     */
    String paySuccess(String orderNo, int payType);

    /**
     * @Description 获得订单项目
     * @Param [id]
     * @Return java.util.List<com.tsong.cmall.controller.vo.OrderItemVO>
     */
    List<OrderItemVO> getOrderItems(Long id);

    /**
     * @Description 处理未支付的订单
     * @Param [orderId]
     * @Return int
     */
    void handleUnpaidOrder(Long orderId);

    /**
     * @Description 生成秒杀订单
     * @Param [seckillSuccessId, userId]
     * @Return java.lang.String
     */
    void handleSeckillSaveOrder(Long userId, Long seckillId, Long goodsId,
                                Long addressId, BigDecimal seckillPrice);
}
