package com.tsong.cmall.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/4/3 13:55
 */
@Data
public class BatchIdParam implements Serializable {
    @Schema(title = "id数组")
    @NotNull(message = "id数组不能为空")
    Long[] ids;
}
