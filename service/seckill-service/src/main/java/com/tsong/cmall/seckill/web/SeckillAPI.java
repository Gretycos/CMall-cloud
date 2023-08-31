package com.tsong.cmall.seckill.web;

import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.MD5Util;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import com.tsong.cmall.entity.Seckill;
import com.tsong.cmall.seckill.service.ISeckillService;
import com.tsong.cmall.seckill.web.params.SeckillExeParam;
import com.tsong.cmall.seckill.web.vo.SeckillGoodsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @Author Tsong
 * @Date 2023/4/6 21:05
 */
@RestController
@Tag(name = "seckill", description = "1-9.秒杀页面接口")
@RequestMapping("/api/seckill")
public class SeckillAPI {
    private static final Logger logger = LoggerFactory.getLogger(SeckillAPI.class);
    @Autowired
    private ISeckillService seckillService;

    @GetMapping("/time/now")
    @Operation(summary = "获取服务器时间", description = "")
    public Result getTimeNow() {
        return ResultGenerator.genSuccessResult(new Date().getTime());
    }

    @GetMapping("/checkStock/{seckillId}")
    @Operation(summary = "判断秒杀商品的虚拟库存是否足够", description = "")
    public Result seckillCheckStock(@Parameter(name = "秒杀事件id") @PathVariable Long seckillId) {
        if (!seckillService.hasStock(seckillId)) {
            return ResultGenerator.genFailResult("秒杀商品库存不足");
        }
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/exposer/{seckillId}")
    @Operation(summary = "暴露秒杀链接", description = "")
    public Result exposeUrl(@Parameter(name = "秒杀事件id") @PathVariable Long seckillId) {
        return ResultGenerator.genSuccessResult(seckillService.exposeUrl(seckillId));
    }

    @PostMapping("/execute")
    @Operation(summary = "处理秒杀", description = "")
    public Result execute(@Parameter(name = "秒杀事件参数") @RequestBody @Valid SeckillExeParam seckillExeParam,
                          @RequestParam Long userId){
        String md5 = seckillExeParam.getMd5();
        Long seckillId = seckillExeParam.getSeckillId();
        Long addressId = seckillExeParam.getAddressId();
        // 判断md5信息是否合法
        if (!md5.equals(MD5Util.MD5Encode(seckillId.toString(), Constants.UTF_ENCODING))) {
            CMallException.fail("秒杀商品不存在");
        }
        return ResultGenerator.genSuccessResult(seckillService.executeSeckill(seckillId, userId, addressId));
    }

    @GetMapping("/list")
    @Operation(summary = "秒杀商品列表", description = "")
    public Result seckillGoodsList() {
        // 直接返回配置的秒杀商品列表
        return ResultGenerator.genSuccessResult(seckillService.getSeckillGoodsList());
    }

    @GetMapping("/{seckillId}")
    @Operation(summary = "秒杀商品信息", description = "")
    public Result seckillGoodsDetail(@Parameter(name = "秒杀事件id") @PathVariable Long seckillId){
        // 返回秒杀商品详情VO，如果秒杀时间未到，不允许访问详情页，也不允许返回数据，参数为秒杀id
        // 根据返回的数据解析出秒杀的事件id，发起秒杀
        // 不访问详情页不会获取到秒杀的事件id，不然容易被猜到url路径从而直接发起秒杀请求
        SeckillGoodsVO seckillGoodsVO = seckillService.getSeckillGoodsDetail(seckillId);
        if (seckillGoodsVO == null) {
            return ResultGenerator.genFailResult("秒杀商品已下架");
        }
        return ResultGenerator.genSuccessResult(seckillGoodsVO);
    }

}
