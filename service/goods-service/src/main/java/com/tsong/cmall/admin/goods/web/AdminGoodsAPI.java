package com.tsong.cmall.admin.goods.web;

import com.tsong.cmall.admin.goods.service.IAdminGoodsService;
import com.tsong.cmall.admin.goods.web.params.GoodsAddParam;
import com.tsong.cmall.admin.goods.web.params.GoodsEditParam;
import com.tsong.cmall.admin.goods.web.vo.GoodsAndCategoryVO;
import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.params.BatchIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tsong
 * @Date 2023/9/11 16:50
 */
@RestController
@Tag(name = "AdminGoods", description = "2-3.后台管理系统商品模块接口")
@RequestMapping("/admin/goods")
public class AdminGoodsAPI {
//    private static final Logger logger = LoggerFactory.getLogger(AdminGoodsAPI.class);
    @Autowired
    private IAdminGoodsService adminGoodsService;
    /**
     * 列表
     */
    @GetMapping(value = "/")
    @Operation(summary = "商品列表", description = "可根据名称和上架状态筛选")
    public Result list(@RequestParam(required = false) @Parameter(name = "页码") Integer pageNumber,
                       @RequestParam(required = false) @Parameter(name = "每页条数") Integer pageSize,
                       @RequestParam(required = false) @Parameter(name = "商品名称") String goodsName,
                       @RequestParam(required = false) @Parameter(name = "上架状态 1-上架 0-下架") Integer goodsSaleStatus,
                       Long adminId) {
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize == null || pageSize < 10 || pageSize > 100){
            pageSize = 10;
        }
        Map<String, Object> params = new HashMap<>(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (StringUtils.hasText(goodsName)) {
            params.put("goodsName", goodsName);
        }
        if (goodsSaleStatus != null) {
            params.put("goodsSaleStatus", goodsSaleStatus);
        }
        params.put("createUser", adminId);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(adminGoodsService.getGoodsListPage(pageUtil));
    }

    /**
     * 添加
     */
    @PostMapping(value = "/")
    @Operation(summary = "新增商品信息", description = "新增商品信息")
    public Result save(@RequestBody @Valid GoodsAddParam goodsAddParam, Long adminId) {
        String result = adminGoodsService.saveGoods(goodsAddParam, adminId);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @PutMapping(value = "/")
    @Operation(summary = "修改商品信息", description = "修改商品信息")
    public Result update(@RequestBody @Valid GoodsEditParam goodsEditParam, Long adminId) {
        String result = adminGoodsService.updateGoods(goodsEditParam, adminId);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取单条商品信息", description = "根据id查询")
    public Result info(@PathVariable("id") Long id, Long adminId) {
        GoodsAndCategoryVO goodsAndCategoryVO = adminGoodsService.getGoodsByIdAndAdminId(id, adminId);
        return ResultGenerator.genSuccessResult(goodsAndCategoryVO);
    }

    /**
     * 批量修改销售状态
     */
    @PutMapping(value = "/status/{saleStatus}")
    @Operation(summary = "批量修改销售状态", description = "批量修改销售状态")
    public Result editSaleStatus(@RequestBody @Valid BatchIdParam batchIdParam,
                                 @PathVariable("saleStatus") int saleStatus,
                                 Long adminId) {
        if (batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (saleStatus != Constants.SALE_STATUS_UP && saleStatus != Constants.SALE_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (adminGoodsService.batchUpdateSaleStatus(batchIdParam.getIds(), saleStatus, adminId)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    @GetMapping("/name/{goodsId}")
    @Operation(summary = "商品名接口", description = "传参为商品id")
    public Result goodsName(@Parameter(name = "商品id") @PathVariable("goodsId") Long goodsId,
                            Long adminId) {
        if (goodsId < 1) {
            return ResultGenerator.genFailResult("参数异常");
        }
        return ResultGenerator.genSuccessResult(adminGoodsService.getGoodsName(goodsId, adminId));
    }

    @GetMapping("/name/all")
    @Operation(summary = "管理员商品查询列表接口", description = "")
    public Result searchAllGoodsIdsAndNames(Long adminId) {
        return ResultGenerator.genSuccessResult(adminGoodsService.getAllGoodsNameByAdminId(adminId));
    }

    @GetMapping("/carousel/{goodsId}")
    @Operation(summary = "商品轮播图接口", description = "传参为商品id")
    public Result goodsCarousel(@Parameter(name = "商品id") @PathVariable("goodsId") Long goodsId,
                               Long adminId) {
        if (goodsId < 1) {
            return ResultGenerator.genFailResult("参数异常");
        }
        return ResultGenerator.genSuccessResult(adminGoodsService.getGoodsCarouselById(goodsId, adminId));
    }
}
