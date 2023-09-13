package com.tsong.cmall.admin.order.web;

import com.tsong.cmall.admin.order.service.IAdminOrderService;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.params.BatchIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tsong
 * @Date 2023/4/3 17:45
 */
@RestController
@Tag(name = "Admin Order", description = "2-5.后台管理系统订单模块接口")
@RequestMapping("/admin/order")
public class AdminOrderAPI {
//    private static final Logger logger = LoggerFactory.getLogger(AdminOrderAPI.class);
    @Autowired
    private IAdminOrderService adminOrderService;

    /**
     * 列表
     */
    @GetMapping(value = "/")
    @Operation(summary = "订单列表", description = "可根据订单号和订单状态筛选")
    public Result list(@RequestParam(required = false) @Parameter(name = "页码") Integer pageNumber,
                       @RequestParam(required = false) @Parameter(name = "每页条数") Integer pageSize,
                       @RequestParam(required = false) @Parameter(name = "订单号") String orderNo,
                       @RequestParam(required = false) @Parameter(name = "订单状态") Integer orderStatus,
                       Long adminId) {
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        Map<String, Object> params = new HashMap<>(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (StringUtils.hasText(orderNo)) {
            params.put("orderNo", orderNo);
        }
        if (orderStatus != null) {
            params.put("orderStatus", orderStatus);
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(adminOrderService.getOrdersPage(pageUtil));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "订单详情接口", description = "传参为订单id")
    public Result orderDetailPage(@Parameter(name = "订单id") @PathVariable("orderId") Long orderId,
                                                 Long adminId) {
        return ResultGenerator.genSuccessResult(adminOrderService.getOrderDetailByOrderId(orderId));
    }

    /**
     * 配货
     */
    @PutMapping(value = "/checkDone")
    @Operation(summary = "修改订单状态为配货成功", description = "批量修改")
    public Result checkDone(@RequestBody BatchIdParam batchIdParam, Long adminId) {
        if (batchIdParam==null||batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = adminOrderService.checkDone(batchIdParam.getIds());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 出库
     */
    @PutMapping(value = "/checkOut")
    @Operation(summary = "修改订单状态为已出库", description = "批量修改")
    public Result checkOut(@RequestBody BatchIdParam batchIdParam, Long adminId) {
        if (batchIdParam==null||batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = adminOrderService.checkOut(batchIdParam.getIds());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 关闭订单
     */
    @PutMapping(value = "/close")
    @Operation(summary = "修改订单状态为商家关闭", description = "批量修改")
    public Result closeOrder(@RequestBody @Valid BatchIdParam batchIdParam, Long adminId) {
        if (batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = adminOrderService.closeOrder(batchIdParam.getIds());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }
}
