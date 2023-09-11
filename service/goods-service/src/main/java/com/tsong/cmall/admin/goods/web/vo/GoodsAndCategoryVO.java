package com.tsong.cmall.admin.goods.web.vo;

import com.tsong.cmall.entity.GoodsCategory;
import com.tsong.cmall.entity.GoodsInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/4/3 16:43
 */
@Data
public class GoodsAndCategoryVO implements Serializable {
    @Schema(title = "商品")
    private GoodsInfo goodsInfo;

    @Schema(title = "一级分类")
    private GoodsCategory firstCategory;

    @Schema(title = "二级分类")
    private GoodsCategory secondCategory;

    @Schema(title = "三级分类")
    private GoodsCategory thirdCategory;
}
