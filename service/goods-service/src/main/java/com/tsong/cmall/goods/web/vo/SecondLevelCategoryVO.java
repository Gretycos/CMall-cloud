package com.tsong.cmall.goods.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SecondLevelCategoryVO implements Serializable {
    @Schema(title = "当前二级分类id")
    private Long categoryId;

    @Schema(title = "父级分类id")
    private Long parentId;

    @Schema(title = "当前分类级别")
    private Byte categoryLevel;

    @Schema(title = "当前二级分类名称")
    private String categoryName;

    @Schema(title = "三级分类列表")
    private List<ThirdLevelCategoryVO> thirdLevelCategoryVOList;
}
