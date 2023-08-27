package com.tsong.feign.clients.goods;

import com.tsong.cmall.common.util.Result;
import com.tsong.feign.clients.goods.fallback.GoodsClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/8/20 22:14
 */
@FeignClient(value = "goods-service", fallbackFactory = GoodsClientFallbackFactory.class)
public interface GoodsClient {
    String RPC_SUFFIX = "/rpc/goods";

    @GetMapping(RPC_SUFFIX + "/")
    Result getGoodsById(Long id);

    @GetMapping(RPC_SUFFIX + "/list")
    Result getGoodsListByIds(List<Long> ids);

    @GetMapping(RPC_SUFFIX + "/category/list")
    Result getGoodsCategoryListByIds(List<Long> ids);
}
