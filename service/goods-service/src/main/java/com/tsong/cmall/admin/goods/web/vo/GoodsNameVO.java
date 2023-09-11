package com.tsong.cmall.admin.goods.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/5/20 18:14
 */
@Data
public class GoodsNameVO implements Serializable {
    @Schema(title = "商品id")
    private Long goodsId;

    @Schema(title = "商品名称")
    private String goodsName;
}
