package com.tsong.cmall.user.web.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Tsong
 * @Date 2023/4/2 23:43
 */
@Data
public class SaveUserAddressParam implements Serializable {
    @Schema(title = "收件人名称")
    @NotNull(message = "收件人名称不能为空")
    @NotEmpty(message = "收件人名称不能为空")
    private String userName;

    @Schema(title = "收件人联系方式")
    @NotNull(message = "收件人联系方式不能为空")
    @NotEmpty(message = "收件人联系方式不能为空")
    private String userPhone;

    @Schema(title = "是否默认地址 0-不是 1-是")
    @NotNull(message = "默认标识不能为空")
    private Byte defaultFlag;

    @Schema(title = "省")
    @NotNull(message = "省份不能为空")
    @NotEmpty(message = "省份不能为空")
    private String provinceName;

    @Schema(title = "市")
    @NotEmpty(message = "城市不能为空")
    @NotNull(message = "城市不能为空")
    private String cityName;

    @Schema(title = "区/县")
    @NotEmpty(message = "区/县不能为空")
    @NotNull(message = "区/县不能为空")
    private String regionName;

    @Schema(title = "详细地址")
    @NotEmpty(message = "详细地址不能为空")
    @NotNull(message = "详细地址不能为空")
    private String detailAddress;
}
