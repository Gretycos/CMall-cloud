package com.tsong.cmall.user.web;

import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.entity.UserAddress;
import com.tsong.cmall.user.service.IUserAddressService;
import com.tsong.cmall.user.web.params.SaveUserAddressParam;
import com.tsong.cmall.user.web.params.UpdateUserAddressParam;
import com.tsong.cmall.user.web.vo.UserAddressVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @Author Tsong
 * @Date 2023/4/2 23:38
 */
@RestController
@Tag(name = "User Address", description = "1-6.商城个人地址相关接口")
@RequestMapping("/api/user/address")
public class UserAddressAPI {
    @Autowired
    private IUserAddressService userAddressService;

    @GetMapping("/")
    @Operation(summary = "我的收货地址列表", description = "")
    public Result addressList(@RequestParam Long userId) {
        return ResultGenerator.genSuccessResult(userAddressService.getMyAddresses(userId));
    }

    @PostMapping("/")
    @Operation(summary = "添加地址", description = "")
    public Result<Boolean> saveUserAddress(@Parameter(name = "新增地址参数")@RequestBody @Valid SaveUserAddressParam saveUserAddressParam,
                                           @RequestParam Long userId) {
        UserAddress userAddress = new UserAddress();
        BeanUtil.copyProperties(saveUserAddressParam, userAddress);
        userAddress.setUserId(userId);
        Boolean saveResult = userAddressService.saveUserAddress(userAddress);
        //添加成功
        if (saveResult) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult("添加失败");
    }

    @PutMapping("/")
    @Operation(summary = "修改地址", description = "")
    public Result<Boolean> updateUserAddress(@Parameter(name = "修改地址参数")@RequestBody @Valid UpdateUserAddressParam updateUserAddressParam,
                                             @RequestParam Long userId) {
        UserAddress userAddressFromDB = userAddressService.getUserAddressById(updateUserAddressParam.getAddressId());
        if (!userId.equals(userAddressFromDB.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDDEN_ERROR.getResult());
        }
        UserAddress userAddress = new UserAddress();
        BeanUtil.copyProperties(updateUserAddressParam, userAddress);
        userAddress.setUserId(userId);
        Boolean updateResult = userAddressService.updateUserAddress(userAddress);
        //修改成功
        if (updateResult) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult("修改失败");
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "获取收货地址详情", description = "传参为地址id")
    public Result getUserAddress(@Parameter(name = "地址id")@PathVariable("addressId") Long addressId,
                                 @RequestParam Long userId) {
        UserAddress userAddress = userAddressService.getUserAddressById(addressId);
        UserAddressVO userAddressVO = new UserAddressVO();
        BeanUtil.copyProperties(userAddress, userAddressVO);
        if (!userId.equals(userAddress.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDDEN_ERROR.getResult());
        }
        return ResultGenerator.genSuccessResult(userAddressVO);
    }

    @GetMapping("/default")
    @Operation(summary = "获取默认收货地址", description = "无传参")
    public Result getDefaultUserAddress(@RequestParam Long userId) {
        UserAddress mallUserAddressById = userAddressService.getMyDefaultAddressByUserId(userId);
        return ResultGenerator.genSuccessResult(mallUserAddressById);
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "删除收货地址", description = "传参为地址id")
    public Result deleteAddress(@Parameter(name = "地址id") @PathVariable("addressId") Long addressId,
                                @RequestParam Long userId) {
        UserAddress mallUserAddressById = userAddressService.getUserAddressById(addressId);
        if (!userId.equals(mallUserAddressById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDDEN_ERROR.getResult());
        }
        Boolean deleteResult = userAddressService.deleteById(addressId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }
}
