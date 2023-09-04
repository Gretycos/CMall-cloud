package com.tsong.cmall.goods.web;

import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.cmall.goods.service.IGoodsService;
import com.tsong.cmall.goods.web.vo.GoodsDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tsong
 * @Date 2023/3/31 12:23
 */
@RestController
@Tag(name = "goods", description = "1-4.CMall商品相关接口")
@RequestMapping("/api/goods")
public class GoodsAPI {
    private static final Logger logger = LoggerFactory.getLogger(GoodsAPI.class);

    @Autowired
    private IGoodsService goodsService;

    @GetMapping("/search")
    @Operation(summary = "商品搜索接口", description = "根据关键字和分类id进行搜索")
    public Result goodsSearch(@RequestParam(required = false) @Parameter(name = "搜索关键字") String keyword,
                              @RequestParam(required = false) @Parameter(name = "分类id") Long goodsCategoryId,
                              @RequestParam(required = false) @Parameter(name = "排序字段") String orderBy,
                              @RequestParam(required = false) @Parameter(name = "排序方式") Integer order,
                              @RequestParam(required = false) @Parameter(name = "页码") Integer pageNumber,
                              @RequestParam Long userId) {

        logger.debug("goodsSearch, keyword={},goodsCategoryId={},orderBy={},pageNumber={},userId={}",
                keyword, goodsCategoryId, orderBy, pageNumber, userId);

        Map<String, Object> params = new HashMap<>();
        //两个搜索参数都为空，直接返回异常
        if (goodsCategoryId == null && !StringUtils.hasText(keyword)) {
            CMallException.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        if (order == null || order < 0){
            order = 0;
        }
        params.put("goodsCategoryId", goodsCategoryId);
        params.put("goodsSellStatus", Constants.SALE_STATUS_UP);
        params.put("page", pageNumber);
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        params.put("keyword", keyword);
        params.put("order", order == 0 ? "ASC" : "DESC");
        params.put("orderBy", StringUtils.hasText(orderBy) ? orderBy : "sellingPrice");
        //封装商品数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(goodsService.searchGoodsInfo(pageUtil));
    }

    @GetMapping("/detail/{goodsId}")
    @Operation(summary = "商品详情接口", description = "传参为商品id")
    public Result goodsDetail(@Parameter(name = "商品id") @PathVariable("goodsId") Long goodsId,
                              @RequestParam Long userId) {
        logger.debug("goodsDetail, goodsId={},userId={}", goodsId, userId);
        if (goodsId < 1) {
            return ResultGenerator.genFailResult("参数异常");
        }
        GoodsInfo goods = goodsService.getGoodsInfoById(goodsId);
        if (Constants.SALE_STATUS_UP != goods.getGoodsSaleStatus()) {
            CMallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        GoodsDetailVO goodsDetailVO = new GoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        return ResultGenerator.genSuccessResult(goodsDetailVO);
    }

    @GetMapping("/suggestion")
    @Operation(summary = "输入框提示", description = "传入关键词")
    public Result goodsSuggestion(@RequestParam String key,
                                  @RequestParam Long userId) {
        if (!StringUtils.hasText(key)){
            CMallException.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        return ResultGenerator.genSuccessResult(goodsService.getSuggestions(key));
    }
}
