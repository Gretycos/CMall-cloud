package com.tsong.feign.clients.user.address.fallback;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.feign.clients.user.address.AddressClient;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @Author Tsong
 * @Date 2023/8/24 15:27
 */
public class AddressClientFallbackFactory implements FallbackFactory<AddressClient> {
    @Override
    public AddressClient create(Throwable cause) {
        return new AddressClient() {
            Result result = ResultGenerator.genFailResult(cause.getMessage());
            @Override
            public Result getAddressById(Long addressId) {
                return result;
            }
        };
    }
}
