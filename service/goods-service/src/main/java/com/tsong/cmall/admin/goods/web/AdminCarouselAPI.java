package com.tsong.cmall.admin.goods.web;

import com.tsong.cmall.admin.goods.service.IAdminCarouselService;
import com.tsong.cmall.admin.goods.web.params.CarouselAddParam;
import com.tsong.cmall.admin.goods.web.params.CarouselEditParam;
import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.entity.Carousel;
import com.tsong.cmall.params.BatchIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tsong
 * @Date 2023/4/3 13:41
 */
@RestController
@Tag(name = "Admin Carousel", description = "2-1.后台管理系统轮播图模块接口")
@RequestMapping("/admin/carousel")
public class AdminCarouselAPI {
    private static final Logger logger = LoggerFactory.getLogger(AdminCarouselAPI.class);
    @Autowired
    private IAdminCarouselService adminCarouselService;

    /**
     * 列表
     */
    @GetMapping(value = "/list")
    @Operation(summary = "轮播图列表", description = "轮播图列表")
    public Result list(@RequestParam(required = false) @Parameter(name = "页码") Integer pageNumber,
                       @RequestParam(required = false) @Parameter(name = "每页条数") Integer pageSize,
                       Long adminId) {
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        Map<String, Object> params = new HashMap<>(4);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(adminCarouselService.getCarouselPage(pageUtil));
    }

    /**
     * 添加
     */
    @PostMapping(value = "/")
    @Operation(summary = "新增轮播图", description = "新增轮播图")
    public Result save(@RequestBody @Valid CarouselAddParam carouselAddParam, Long adminId) {
        String result = adminCarouselService.saveCarousel(carouselAddParam);
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
    @Operation(summary = "修改轮播图信息", description = "修改轮播图信息")
    public Result update(@RequestBody @Valid CarouselEditParam carouselEditParam, Long adminId) {
        String result = adminCarouselService.updateCarousel(carouselEditParam);
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
    @Operation(summary = "获取单条轮播图信息", description = "根据id查询")
    public Result info(@PathVariable("id") Integer id, Long adminId) {
        Carousel carousel = adminCarouselService.getCarouselById(id);
        if (carousel == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(carousel);
    }

    /**
     * 删除
     */
    @PostMapping(value = "/delete")
    @Operation(summary = "批量删除轮播图信息", description = "批量删除轮播图信息")
    public Result delete(@RequestBody BatchIdParam batchIdParam, Long adminId) {
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (adminCarouselService.deleteBatch(batchIdParam.getIds())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}
