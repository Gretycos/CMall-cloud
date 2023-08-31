package com.tsong.feign.clients.goods;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.entity.GoodsCategory;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.feign.clients.goods.fallback.GoodsClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/8/20 22:14
 */
@FeignClient(value = "goods-service", fallbackFactory = GoodsClientFallbackFactory.class)
public interface GoodsClient {
    String RPC_SUFFIX = "/rpc/goods";

    @GetMapping(RPC_SUFFIX + "/")
    Result<GoodsInfo> getGoodsById(@RequestParam Long id);

    @GetMapping(RPC_SUFFIX + "/list")
    Result<List<GoodsInfo>> getGoodsListByIds(@RequestParam List<Long> ids);

    @GetMapping(RPC_SUFFIX + "/category/list")
    Result<List<GoodsCategory>> getGoodsCategoryListByIds(@RequestParam List<Long> ids);
}
