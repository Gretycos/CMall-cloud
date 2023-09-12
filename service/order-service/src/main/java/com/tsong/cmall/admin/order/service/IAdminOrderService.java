package com.tsong.cmall.admin.order.service;

import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.Order;
import com.tsong.cmall.vo.order.OrderDetailVO;

public interface IAdminOrderService {
    /**
     * @Description 根据查询条件获得订单页
     * @Param [pageUtil]
     * @Return com.tsong.cmall.util.PageResult<com.tsong.cmall.entity.Order>
     */
    PageResult<Order> getOrdersPage(PageQueryUtil pageUtil);

    /**
     * @Description 配货
     * @Param [ids]
     * @Return java.lang.String
     */

    String checkDone(Long[] ids);

    /**
     * @Description 出库
     * @Param [ids]
     * @Return java.lang.String
     */
    String checkOut(Long[] ids);

    /**
     * @Description 关闭订单
     * @Param [ids]
     * @Return java.lang.String
     */
    String closeOrder(Long[] ids);

    /**
     * @Description 用订单id获取订单详情
     * @Param [orderId]
     * @Return com.tsong.cmall.controller.vo.OrderDetailVO
     */
    OrderDetailVO getOrderDetailByOrderId(Long orderId);
}
