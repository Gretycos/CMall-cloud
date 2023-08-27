package com.tsong.feign.clients.goods.fallback;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.dto.StockNumDTO;
import com.tsong.feign.clients.goods.GoodsClient;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/8/20 22:16
 */
public class GoodsClientFallbackFactory implements FallbackFactory<GoodsClient> {
    @Override
    public GoodsClient create(Throwable cause) {
        return new GoodsClient() {
            Result result = ResultGenerator.genFailResult(cause.getMessage());
            @Override
            public Result getGoodsById(Long id) {
                return result;
            }

            @Override
            public Result getGoodsListByIds(List<Long> ids) {
                return result;
            }

            @Override
            public Result getGoodsCategoryListByIds(List<Long> ids) {
                return result;
            }
        };
    }
}
