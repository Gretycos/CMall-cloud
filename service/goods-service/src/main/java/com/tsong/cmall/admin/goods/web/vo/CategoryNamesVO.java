package com.tsong.cmall.admin.goods.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/5/29 22:58
 */
@Data
public class CategoryNamesVO implements Serializable {
    @Schema(title = "分类id")
    private Long categoryId;

    @Schema(title = "分类名字")
    private String categoryName;
}
