package com.tsong.cmall.goods.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class ThirdLevelCategoryVO implements Serializable {
    @Schema(title = "分类id")
    private Long categoryId;

    @Schema(title = "分类等级")
    private Byte categoryLevel;

    @Schema(title = "分类名")
    private String categoryName;
}
