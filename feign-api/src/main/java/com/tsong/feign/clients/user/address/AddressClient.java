package com.tsong.feign.clients.user.address;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.entity.UserAddress;
import com.tsong.feign.clients.user.address.fallback.AddressClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author Tsong
 * @Date 2023/8/24 15:25
 */
@FeignClient(value = "user-service", fallbackFactory = AddressClientFallbackFactory.class)
public interface AddressClient {
    String RPC_SUFFIX = "/rpc/user/address";

    @GetMapping(RPC_SUFFIX + "/byId")
    Result<UserAddress> getAddressById(@RequestParam Long addressId);
}
