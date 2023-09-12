package com.tsong.cmall.admin.goods.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/5/9 18:29
 */
@Data
public class BatchUrlParam implements Serializable {
    @Schema(title = "url数组")
    @NotNull(message = "url数组不能为空")
    String[] urls;
}
