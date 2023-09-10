package com.tsong.cmall.goods.web;

import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.goods.service.IHomePageConfigService;
import com.tsong.cmall.goods.web.vo.HomePageCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/3/31 22:55
 */
@RestController
@Tag(name = "homepage", description = "1-1.商城首页接口")
@RequestMapping("/api/homepage")
public class HomePageAPI {
    @Autowired
    private IHomePageConfigService homePageConfigService;

    @GetMapping("/")
    @Operation(summary = "获取首页数据", description = "轮播图、新品、推荐等")
    public Result indexInfo() {
        return ResultGenerator.genSuccessResult(homePageConfigService.getHomePageInfo());
    }

    @GetMapping("/categories")
    @Operation(summary = "获取分类数据", description = "分类页面使用")
    public Result<List<HomePageCategoryVO>> getCategories() {
        List<HomePageCategoryVO> categories = homePageConfigService.getCategoriesForHomePage();
        if (CollectionUtils.isEmpty(categories)) {
            CMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(categories);
    }
}
