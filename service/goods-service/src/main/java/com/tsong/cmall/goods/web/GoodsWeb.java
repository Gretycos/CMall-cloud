package com.tsong.cmall.goods.web;

import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.dto.StockNumDTO;
import com.tsong.cmall.entity.GoodsCategory;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.cmall.goods.service.IGoodsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/8/22 13:12
 */
@RestController
@RequestMapping("/rpc/goods")
public class GoodsWeb {
    @Autowired
    private IGoodsService goodsService;

    @GetMapping("/")
    @Operation(summary = "获得商品", description = "rpc")
    public Result<GoodsInfo> getGoodsById(@RequestParam Long id){
        return ResultGenerator.genSuccessResult(goodsService.getGoodsInfoById(id));
    }


    @GetMapping("/list")
    @Operation(summary = "商品列表", description = "rpc")
    public Result<List<GoodsInfo>> getGoodsListByIds(@RequestParam List<Long> ids){
        return ResultGenerator.genSuccessResult(goodsService.getGoodsByIds(ids));
    }

    @GetMapping("/category/list")
    @Operation(summary = "商品分类列表", description = "rpc")
    public Result<List<GoodsCategory>> getGoodsCategoryListByIds(@RequestParam List<Long> ids){
        return ResultGenerator.genSuccessResult(goodsService.getGoodsCategoryByIds(ids));
    }

//    @PostMapping("/recover/stock")
//    @Operation(summary = "商品恢复库存", description = "rpc")
//    public Result recoverGoodsStock(List<StockNumDTO> stockNumDTOS){
//        return ResultGenerator.genSuccessResult(goodsService.recoverStockNum(stockNumDTOS));
//    }
}
