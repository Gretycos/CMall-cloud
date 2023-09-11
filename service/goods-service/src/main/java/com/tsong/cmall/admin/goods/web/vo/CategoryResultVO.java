package com.tsong.cmall.admin.goods.web.vo;

import com.tsong.cmall.entity.GoodsCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/4/3 14:30
 */
@Data
public class CategoryResultVO implements Serializable {
    @Schema(title = "二级分类列表")
    private List<GoodsCategory> secondLevelCategories;
    @Schema(title = "三级分类列表")
    private List<GoodsCategory> thirdLevelCategories;
}
