package com.tsong.cmall.admin.goods.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/4/3 16:05
 */
@Data
public class GoodsCategoryEditParam implements Serializable {

    @Schema(title = "待修改分类id")
    @NotNull(message = "分类id不能为空")
    @Min(value = 1, message = "分类id不能为空")
    private Long categoryId;

    @Schema(title = "分类层级")
    @NotNull(message = "categoryLevel不能为空")
    @Min(value = 1, message = "分类级别最低为1")
    @Max(value = 3, message = "分类级别最高为3")
    private Byte categoryLevel;

    @Schema(title = "父类id")
    @NotNull(message = "parentId不能为空")
    @Min(value = 0, message = "parentId最低为0")
    private Long parentId;

    @Schema(title = "分类名称")
    @NotEmpty(message = "categoryName不能为空")
    @Length(max = 16,message = "分类名称过长")
    private String categoryName;

    @Schema(title = "排序值")
    @Min(value = 1, message = "categoryRank最低为1")
    @Max(value = 200, message = "categoryRank最高为200")
    @NotNull(message = "categoryRank不能为空")
    private Integer categoryRank;
}
