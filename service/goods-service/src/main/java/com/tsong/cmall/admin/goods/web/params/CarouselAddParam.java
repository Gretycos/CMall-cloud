package com.tsong.cmall.admin.goods.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author Tsong
 * @Date 2023/4/3 13:46
 */
@Data
public class CarouselAddParam {
    @Schema(title = "轮播图URL地址")
    @NotEmpty(message = "轮播图URL不能为空")
    private String carouselUrl;

    @Schema(title = "轮播图跳转地址")
    @NotEmpty(message = "轮播图跳转地址不能为空")
    private String redirectUrl;

    @Schema(title = "排序值")
    @Min(value = 1, message = "carouselRank最低为1")
    @Max(value = 200, message = "carouselRank最高为200")
    @NotNull(message = "carouselRank不能为空")
    private Integer carouselRank;
}
