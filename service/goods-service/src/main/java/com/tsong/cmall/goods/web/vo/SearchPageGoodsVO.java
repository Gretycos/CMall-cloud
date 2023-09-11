package com.tsong.cmall.goods.web.vo;

import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.entity.GoodsInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/3/24 00:32
 */
@Data
@NoArgsConstructor
public class SearchPageGoodsVO implements Serializable {
    @Schema(title = "商品id")
    private Long goodsId;

    @Schema(title = "商品名称")
    private String goodsName;

    @Schema(title = "商品简介")
    private String goodsIntro;

    @Schema(title = "商品类别，第三级")
    private Long goodsCategoryId;

    @Schema(title = "商品图片地址")
    private String goodsCoverImg;

    @Schema(title = "商品价格")
    private BigDecimal sellingPrice;

    @Schema(title = "商品标签")
    private String tag;

    @Schema(title = "广告位")
    private Boolean isAD;

    @Schema(title = "查询建议")
    private List<String> suggestion;

    public SearchPageGoodsVO(GoodsInfo goods){
        BeanUtil.copyProperties(goods, this);
        // 字符串过长导致文字超出的问题
        if (goodsName.length() > 28) {
            goodsName = goodsName.substring(0, 28) + "...";
            this.setGoodsName(goodsName);
        }
        if (goodsIntro.length() > 30) {
            goodsIntro = goodsIntro.substring(0, 30) + "...";
            this.setGoodsIntro(goodsIntro);
        }
        suggestion = new ArrayList<>();
        String[] tags = goods.getTag().split(",");
        suggestion.addAll(Arrays.stream(tags).toList());
    }
}
