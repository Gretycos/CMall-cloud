package com.tsong.cmall.user.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/4/1 17:34
 */
@Data
public class MallUserUpdateParam implements Serializable {
    @Schema(title = "用户昵称")
    @NotEmpty(message = "昵称不能为空")
    @Length(max = 16,message = "昵称过长")
    private String nickName;

    @Schema(title = "个性签名")
    @NotEmpty(message = "个性签名不能为空")
    @Length(max = 140,message = "个性签名过长")
    private String introduceSign;
}
