package com.tsong.cmall.order.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderDetailVO implements Serializable {
    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "订单价格")
    private BigDecimal totalPrice;

    @Schema(title = "订单支付状态码")
    private Byte payStatus;

    @Schema(title = "订单支付方式")
    private Byte payType;

    @Schema(title = "订单支付方式")
    private String payTypeString;

    @Schema(title = "订单支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

    @Schema(title = "订单状态码")
    private Byte orderStatus;

    @Schema(title = "订单状态")
    private String orderStatusString;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(title = "订单优惠")
    private BigDecimal discount;

    @Schema(title = "用户地址")
    private String userAddress;

    @Schema(title = "订单商品列表")
    private List<OrderItemVO> orderItemVOList;
}
