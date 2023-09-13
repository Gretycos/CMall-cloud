package com.tsong.cmall.admin.user.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/4/3 17:43
 */
@Data
public class AdminNameUpdateParam implements Serializable {
    @NotEmpty(message = "loginUserName不能为空")
    @Schema(title = "用户名")
    private String loginUserName;

    @NotEmpty(message = "nickName不能为空")
    @Schema(title = "昵称")
    private String nickName;
}
