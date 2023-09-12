package com.tsong.cmall.admin.goods.web;

import com.tsong.cmall.admin.goods.service.IAdminHomePageConfigService;
import com.tsong.cmall.admin.goods.web.params.HomePageConfigAddParam;
import com.tsong.cmall.admin.goods.web.params.HomePageConfigEditParam;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.entity.HomePageConfig;

import com.tsong.cmall.goods.enums.HomePageConfigTypeEnum;
import com.tsong.cmall.params.BatchIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tsong
 * @Date 2023/4/3 16:49
 */
@RestController
@Tag(name = "Admin HomePage Config", description = "2-4.后台管理系统首页配置模块接口")
@RequestMapping("/admin/homepage")
public class AdminHomePageConfigAPI {
//    private static final Logger logger = LoggerFactory.getLogger(AdminHomePageConfigAPI.class);
    @Autowired
    private IAdminHomePageConfigService adminHomePageConfigService;

    /**
     * 列表
     */
    @GetMapping(value = "/")
    @Operation(summary = "首页配置列表", description = "首页配置列表")
    public Result list(@RequestParam(required = false) @Parameter(name = "页码") Integer pageNumber,
                       @RequestParam(required = false) @Parameter(name = "每页条数") Integer pageSize,
                       @RequestParam(required = false) @Parameter(name = "1-搜索框热搜 2-搜索下拉框热搜 3-(首页)热销商品 4-(首页)新品上线 5-(首页)为你推荐")
                           Integer configType, Long adminId) {
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        HomePageConfigTypeEnum indexConfigTypeEnum = HomePageConfigTypeEnum.getHomePageConfigTypeEnumByType(configType);
        if (indexConfigTypeEnum.equals(HomePageConfigTypeEnum.DEFAULT)) {
            return ResultGenerator.genFailResult("非法参数！");
        }
        Map<String, Object> params = new HashMap<>(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("configType", configType);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(adminHomePageConfigService.getConfigsPage(pageUtil));
    }

    /**
     * 添加
     */
    @PostMapping(value = "/")
    @Operation(summary = "新增首页配置项", description = "新增首页配置项")
    public Result save(@RequestBody @Valid HomePageConfigAddParam homePageConfigAddParam, Long adminId) {
        String result = adminHomePageConfigService.saveHomePageConfig(homePageConfigAddParam);
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
    @Operation(summary = "修改首页配置项", description = "修改首页配置项")
    public Result update(@RequestBody @Valid HomePageConfigEditParam homePageConfigEditParam, Long adminId) {
        String result = adminHomePageConfigService.updateHomePageConfig(homePageConfigEditParam);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取单条首页配置项信息", description = "根据id查询")
    public Result info(@PathVariable("id") Long id, Long adminId) {
        HomePageConfig config = adminHomePageConfigService.getHomePageConfigById(id);
        if (config == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(config);
    }

    /**
     * 删除
     */
    @PostMapping(value = "/delete")
    @Operation(summary = "批量删除首页配置项信息", description = "批量删除首页配置项信息")
    public Result delete(@RequestBody @Valid BatchIdParam batchIdParam, Long adminId) {
        if (batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (adminHomePageConfigService.deleteBatch(batchIdParam.getIds())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }
}
