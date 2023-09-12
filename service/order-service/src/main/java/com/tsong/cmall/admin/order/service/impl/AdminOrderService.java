package com.tsong.cmall.admin.order.service.impl;

import com.tsong.cmall.admin.order.mapper.AdminOrderAddressMapper;
import com.tsong.cmall.admin.order.mapper.AdminOrderItemMapper;
import com.tsong.cmall.admin.order.mapper.AdminOrderMapper;
import com.tsong.cmall.admin.order.service.IAdminOrderService;

import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.entity.*;

import com.tsong.cmall.order.enums.OrderStatusEnum;
import com.tsong.cmall.order.enums.PayTypeEnum;
import com.tsong.cmall.vo.order.OrderDetailVO;
import com.tsong.cmall.vo.order.OrderItemVO;
import com.tsong.feign.clients.coupon.CouponClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.tsong.cmall.common.enums.ServiceResultEnum.RPC_ERROR;


/**
 * @Author Tsong
 * @Date 2023/3/24 15:44
 */
@Service
public class AdminOrderService implements IAdminOrderService {
    @Autowired
    private AdminOrderMapper adminOrderMapper;
    @Autowired
    private AdminOrderItemMapper adminOrderItemMapper;
    @Autowired
    private AdminOrderAddressMapper adminOrderAddressMapper;
    @Autowired
    private CouponClient couponClient;

    @Override
    public PageResult<Order> getOrdersPage(PageQueryUtil pageUtil) {
        int total = adminOrderMapper.getTotalOrders(pageUtil);
        List<Order> orderList = adminOrderMapper.selectOrderList(pageUtil);
        return new PageResult<>(orderList, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public String checkDone(Long[] ids) {
        // 查询所有的订单 判断状态 修改状态和更新时间
        List<Order> orderList = adminOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        List<String> errorOrderNoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderList)) {
            for (Order order : orderList) {
                if (order.getIsDeleted() == 1) { // 订单被删除
                    errorOrderNoList.add(order.getOrderNo());
                    continue;
                }
                if (order.getOrderStatus() != 1) { // 订单不是已支付
                    errorOrderNoList.add(order.getOrderNo());
                }
            }
            if (errorOrderNoList.isEmpty()) {
                // 所选的订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (adminOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                // 所选的订单此时不可执行出库操作
                if (errorOrderNoList.size() <= 5) {
                    return errorOrderNoList + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        // 未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String checkOut(Long[] ids) {
        // 查询所有的订单 判断状态 修改状态和更新时间
        List<Order> orderList = adminOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        List<String> errorOrderNoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderList)) {
            for (Order order : orderList) {
                if (order.getIsDeleted() == 1) {
                    errorOrderNoList.add(order.getOrderNo());
                    continue;
                }
                if (order.getOrderStatus() != 1 && order.getOrderStatus() != 2) {
                    errorOrderNoList.add(order.getOrderNo());
                }
            }
            if (errorOrderNoList.isEmpty()) {
                // 订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (adminOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                // 订单此时不可执行出库操作
                if (errorOrderNoList.size() <= 5) {
                    return errorOrderNoList + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        // 未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String closeOrder(Long[] ids) {
        // 查询所有的订单 判断状态 修改状态和更新时间
        List<Order> orderList = adminOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        List<String> errorOrderNoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderList)) {
            for (Order order : orderList) {
                // isDeleted=1 一定为已关闭订单
                if (order.getIsDeleted() == 1) {
                    errorOrderNoList.add(order.getOrderNo());
                    continue;
                }
                // 已关闭或者已完成 无法关闭订单
                if (order.getOrderStatus() == 4 || order.getOrderStatus() < 0) {
                    errorOrderNoList.add(order.getOrderNo());
                }
            }
            if (StringUtils.isEmpty(errorOrderNoList.toString())) {
                // 订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (adminOrderMapper.closeOrder(Arrays.asList(ids), OrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                // 订单此时不可执行关闭操作
                if (errorOrderNoList.size() <= 5) {
                    return errorOrderNoList + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        // 未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }


    @Override
    public OrderDetailVO getOrderDetailByOrderId(Long orderId) {
        Order order = adminOrderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            CMallException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        return genOrderDetailVO(order);
    }

    /**
     * 生成订单细节VO
     * 细节中要展示每个订单项的实际付款
     * */
    private OrderDetailVO genOrderDetailVO(Order order){
        // 获取订单项数据
        List<OrderItem> orderItems = adminOrderItemMapper.selectByOrderId(order.getOrderId());
        if (CollectionUtils.isEmpty(orderItems)) {
            CMallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        // 拷贝到订单项VOList
        List<OrderItemVO> orderItemVOList = BeanUtil.copyList(orderItems, OrderItemVO.class);
        OrderAddress orderAddress = adminOrderAddressMapper.selectByPrimaryKey(order.getOrderId());
        if (orderAddress == null){
            CMallException.fail(ServiceResultEnum.ORDER_ADDRESS_NULL_ERROR.getResult());
        }
        // 订单VO
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtil.copyProperties(order, orderDetailVO);
        orderDetailVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(orderDetailVO.getOrderStatus()).getName());
        orderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(orderDetailVO.getPayType()).getName());
        orderDetailVO.setUserAddress(orderAddress.toString());
        // 计算每个订单项的实际付款价格
        calPaidPrice(order, orderItemVOList);
        orderDetailVO.setOrderItemVOList(orderItemVOList);

        // 优惠券信息
        Result<Coupon> couponRes = couponClient.getCouponByOrderId(order.getOrderId());
        if (couponRes.getResultCode() != 200) {
            CMallException.fail(RPC_ERROR.getResult());
        }
        Coupon coupon = couponRes.getData();
        if (coupon != null) {
            orderDetailVO.setDiscount(new BigDecimal(coupon.getDiscount()));
        }
        return orderDetailVO;
    }


    private void calPaidPrice(Order order, List<OrderItemVO> orderItemVOList){
        BigDecimal paidTotal = order.getTotalPrice();
        BigDecimal totalPrice = orderItemVOList.stream()
                .map(e -> e.getSellingPrice().multiply(new BigDecimal(e.getGoodsCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        for (OrderItemVO orderItemVO : orderItemVOList) {
            BigDecimal sellingPrice = orderItemVO.getSellingPrice().multiply(new BigDecimal(orderItemVO.getGoodsCount()));
            if (paidTotal.compareTo(totalPrice) == 0){
                orderItemVO.setPaidPrice(sellingPrice);
            }else{
                orderItemVO.setPaidPrice(sellingPrice.divide(totalPrice,2,RoundingMode.HALF_UP).multiply(paidTotal));
            }
        }
    }
}
