package com.tsong.cmall.admin.seckill.web;

import com.tsong.cmall.admin.seckill.service.IAdminSeckillService;
import com.tsong.cmall.admin.seckill.web.params.SeckillAddParam;
import com.tsong.cmall.admin.seckill.web.params.SeckillEditParam;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
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
 * @Date 2023/4/12 13:44
 */
@RestController
@Tag(name = "Admin Seckill", description = "2-9.后台管理秒杀模块接口")
@RequestMapping("/admin/seckill")
public class AdminSeckillAPI {
//    private static final Logger logger = LoggerFactory.getLogger(AdminSeckillAPI.class);
    @Autowired
    private IAdminSeckillService adminSeckillService;

    @GetMapping("/")
    @Operation(summary = "秒杀商品列表", description = "")
    public Result seckillList(@Parameter(name = "页码") @RequestParam(required = false) Integer pageNumber,
                             @Parameter(name = "每页条数") @RequestParam(required = false) Integer pageSize,
                             Long adminId) {
        Map<String, Object> params = new HashMap<>(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize == null || pageSize < 10 || pageSize > 100){
            pageSize = 10;
        }
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(adminSeckillService.getSeckillPage(pageUtil));
    }

    @PostMapping("/")
    @Operation(summary = "新增秒杀", description = "")
    public Result saveSeckill(@Parameter(name = "秒杀新增参数") @RequestBody @Valid SeckillAddParam seckillAddParam,
                             Long adminId) {

        boolean result = adminSeckillService.saveSeckill(seckillAddParam);
        if (!result){
            return ResultGenerator.genFailResult("新增秒杀失败");
        }

        return ResultGenerator.genSuccessResult();
    }

    @PutMapping("/")
    @Operation(summary = "修改秒杀", description = "")
    public Result updateSeckill(@Parameter(name = "秒杀修改参数") @RequestBody @Valid SeckillEditParam seckillEditParam,
                               Long adminId){
        boolean result = adminSeckillService.updateSeckill(seckillEditParam);
        if (!result){
            return ResultGenerator.genFailResult("更新秒杀失败");
        }
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @Operation(summary = "秒杀详情", description = "")
    public Result seckillInfo(@Parameter(name = "秒杀id") @PathVariable("id") Long id,
                             Long adminId) {
        return ResultGenerator.genSuccessResult(adminSeckillService.getSeckillVOById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除秒杀", description = "")
    public Result deleteSeckill(@Parameter(name = "秒杀id") @PathVariable Long id,
                                Long adminId) {
        boolean result = adminSeckillService.deleteSeckillById(id);
        if (!result){
            return ResultGenerator.genFailResult("删除秒杀失败");
        }
        return ResultGenerator.genSuccessResult();
    }

}
