package com.tsong.cmall.user.web;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.user.service.IUserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Tsong
 * @Date 2023/8/24 15:23
 */
@RestController
@RequestMapping("/rpc/user/address")
public class UserAddressWeb {
    @Autowired
    private IUserAddressService addressService;

    @GetMapping( "/byId")
    @Operation(summary = "通过地址id获得地址", description = "rpc")
    public Result getAddressById(Long addressId){
        return ResultGenerator.genSuccessResult(addressService.getUserAddressById(addressId));
    }
}
