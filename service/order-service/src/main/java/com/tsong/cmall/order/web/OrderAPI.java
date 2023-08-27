package com.tsong.cmall.order.web;

import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.order.service.IOrderService;
import com.tsong.cmall.order.web.params.SaveOrderParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tsong
 * @Date 2023/3/31 23:40
 */
@RestController
@Tag(name = "order", description = "1-7.订单操作相关接口")
@RequestMapping("/api/order")
public class OrderAPI {
    @Autowired
    private IOrderService orderService;

    @PostMapping("/save")
    @Operation(summary = "提交订单接口", description = "传参为地址id、待结算的购物项id数组、领券id")
    public Result<String> saveOrder(@Parameter(name = "订单参数") @RequestBody @Valid SaveOrderParam saveOrderParam,
                                    Long userId) {
        Long couponUserId = saveOrderParam.getCouponUserId();
        Long addressId = saveOrderParam.getAddressId();
        Long[] cartItemIds = saveOrderParam.getCartItemIds();
        String saveOrderResult = orderService.saveOrder(userId, couponUserId, addressId, cartItemIds);
        Result result = ResultGenerator.genSuccessResult();
        result.setData(saveOrderResult);
        return result;
    }

    @GetMapping("/seckillOrderNo")
    @Operation(summary = "秒杀订单号查询接口", description = "")
    public Result getSeckillOrderNo(@Parameter(name = "秒杀id") @RequestParam Long seckillId,
                                    @Parameter(name = "秒杀成功id") @RequestParam Long seckillSuccessId,
                                    @Parameter(name = "秒杀成功密钥") @RequestParam String seckillSecretKey,
                                    Long userId) {
        Result result = ResultGenerator.genSuccessResult();
        result.setData(orderService.getSeckillOrderNo(userId, seckillId, seckillSuccessId, seckillSecretKey));
        return result;
    }

    @GetMapping("/{orderNo}")
    @Operation(summary = "订单详情接口", description = "传参为订单号")
    public Result orderDetailPage(@Parameter(name = "订单号") @PathVariable("orderNo") String orderNo,
                                                 Long userId) {
        return ResultGenerator.genSuccessResult(orderService.getOrderDetailByOrderNo(orderNo, userId));
    }

    @GetMapping("/")
    @Operation(summary = "订单列表接口", description = "传参为页码")
    public Result orderList(@Parameter(name = "页码") @RequestParam(required = false) Integer pageNumber,
                            @Parameter(name = "订单状态:0.待支付 1.待确认 2.待发货 3:已发货 4.交易成功") @RequestParam(required = false) Integer status,
                            Long userId) {
        Map<String, Object> params = new HashMap<>(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("userId", userId);
        params.put("orderStatus", status);
        params.put("isDeleted", (byte) 0);
        params.put("page", pageNumber);
        params.put("limit", Constants.MY_ORDERS_PAGE_LIMIT);
        params.put("sortField", "create_time"); // 要写数据库中的字段
        params.put("order", "desc");
        //封装分页请求参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(orderService.getMyOrders(pageUtil));
    }

    @PutMapping("/cancel/{orderNo}")
    @Operation(summary = "订单取消接口", description = "传参为订单号")
    public Result cancelOrder(@Parameter(name = "订单号") @PathVariable("orderNo") String orderNo,
                              Long userId) {
        String cancelOrderResult = orderService.cancelOrder(orderNo, userId);
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/delete/{orderNo}")
    @Operation(summary = "订单删除接口", description = "传参为订单号")
    public Result deleteOrder(@Parameter(name = "订单号") @PathVariable("orderNo") String orderNo,
                              Long userId) {
        String deleteOrderResult = orderService.deleteOrder(orderNo, userId);
        if (ServiceResultEnum.SUCCESS.getResult().equals(deleteOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(deleteOrderResult);
        }
    }

    @PutMapping("/finish/{orderNo}")
    @Operation(summary = "确认收货接口", description = "传参为订单号")
    public Result finishOrder(@Parameter(name = "订单号") @PathVariable("orderNo") String orderNo,
                              Long userId) {
        String finishOrderResult = orderService.finishOrder(orderNo, userId);
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    @GetMapping("/paySuccess")
    @Operation(summary = "模拟支付成功回调的接口", description = "传参为订单号和支付方式")
    public Result paySuccess(@Parameter(name = "订单号") @RequestParam("orderNo") String orderNo,
                             @Parameter(name = "支付方式") @RequestParam("payType") int payType) {
        String payResult = orderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

}
