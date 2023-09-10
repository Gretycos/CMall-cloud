package com.tsong.cmall.goods.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HomePageCategoryVO implements Serializable {
    @Schema(title = "当前一级分类id")
    private Long categoryId;

    @Schema(title = "当前分类级别")
    private Byte categoryLevel;

    @Schema(title = "当前一级分类名称")
    private String categoryName;

    @Schema(title = "二级分类列表")
    private List<SecondLevelCategoryVO> secondLevelCategoryVOList;
}
