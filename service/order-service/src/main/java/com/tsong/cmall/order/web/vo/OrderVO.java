package com.tsong.cmall.order.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单列表的VO
 * @Author Tsong
 * @Date 2023/3/24 22:49
 */
@Data
public class OrderVO implements Serializable {
    @Schema(title = "订单id")
    private Long orderId;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "订单总价")
    private BigDecimal totalPrice;

    @Schema(title = "支付类型")
    private Byte payType;

    @Schema(title = "订单状态")
    private Byte orderStatus;

    @Schema(title = "订单状态字符串")
    private String orderStatusString;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "订单项目列表")
    private List<OrderItemVO> orderItemVOList;
}
