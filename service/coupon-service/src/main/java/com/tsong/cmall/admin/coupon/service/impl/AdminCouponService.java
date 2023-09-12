package com.tsong.cmall.admin.coupon.service.impl;

import com.tsong.cmall.admin.coupon.mapper.AdminCouponMapper;
import com.tsong.cmall.admin.coupon.service.IAdminCouponService;
import com.tsong.cmall.admin.coupon.web.params.CouponAddParam;
import com.tsong.cmall.admin.coupon.web.params.CouponEditParam;
import com.tsong.cmall.admin.coupon.web.vo.AdminCouponVO;
import com.tsong.cmall.common.constants.Constants;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.*;
import com.tsong.cmall.entity.Coupon;
import com.tsong.cmall.entity.GoodsCategory;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.feign.clients.goods.GoodsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.tsong.cmall.common.enums.ServiceResultEnum.RPC_ERROR;


/**
 * @Author Tsong
 * @Date 2023/3/21 22:58
 */
@Service
public class AdminCouponService implements IAdminCouponService {
    @Autowired
    private AdminCouponMapper adminCouponMapper;
    @Autowired
    private GoodsClient goodsClient;

    @Override
    public PageResult getCouponPage(PageQueryUtil pageUtil) {
        List<Coupon> couponList = adminCouponMapper.selectCouponList(pageUtil);
        List<AdminCouponVO> couponVOList = BeanUtil.copyList(couponList, AdminCouponVO.class);
        int total = adminCouponMapper.getTotalCoupons(pageUtil);
        if (total > 0){
            Map[] maps = getCategoryOrGoodsNamesMap(couponList);
            Map<Long, String> categoryNamesMap = maps[0];
            Map<Long, String> goodsCategoryNamesMap = maps[1];
            for (AdminCouponVO adminCouponVO : couponVOList) {
                StringBuilder namesBuilder = new StringBuilder();
                for (String id : adminCouponVO.getGoodsValue().split(",")) {
                    if (adminCouponVO.getGoodsType() == 1){
                        namesBuilder.append(categoryNamesMap.get(Long.valueOf(id))+"分区");
                    } else if (adminCouponVO.getGoodsType() == 2){
                        namesBuilder.append(goodsCategoryNamesMap.get(Long.valueOf(id)) + "分区指定商品");
                    }
                    namesBuilder.append(",");
                }
                if (!namesBuilder.isEmpty()){
                    namesBuilder.deleteCharAt(namesBuilder.length() - 1);
                }
                adminCouponVO.setGoodsValueNames(namesBuilder.toString());
            }
        }
        return new PageResult(couponVOList, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public boolean saveCoupon(CouponAddParam couponAddParam) {
        Coupon coupon = new Coupon();
        BeanUtil.copyProperties(couponAddParam, coupon);
        coupon.setCreateTime(new Date());
        if(coupon.getCouponType() == 2){
            String code = MD5Util.MD5Encode(Constants.COUPON_CODE + System.currentTimeMillis(), Constants.UTF_ENCODING)
                    .substring(8, 24);
            coupon.setCouponCode(code);
        }
        Date startDate = coupon.getCouponStartTime();
        Date endDate = coupon.getCouponEndTime();
        if (startDate != null && endDate != null && endDate.getTime() < startDate.getTime()){
            CMallException.fail("结束时间小于开始时间");
        }
        return adminCouponMapper.insertSelective(coupon) > 0;
    }

    @Override
    public boolean updateCoupon(CouponEditParam couponEditParam) {
        Coupon coupon = new Coupon();
        BeanUtil.copyProperties(couponEditParam, coupon);
        coupon.setUpdateTime(new Date());
        return adminCouponMapper.updateByPrimaryKeySelective(coupon) > 0;
    }

    @Override
    public Coupon getCouponById(Long id) {
        return adminCouponMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean deleteCouponById(Long id) {
        return adminCouponMapper.deleteByPrimaryKey(id) > 0;
    }

    private Map[] getCategoryOrGoodsNamesMap(List<Coupon> couponList){
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> goodsIds = new HashSet<>();

        for (Coupon coupon : couponList) {
            if (coupon.getGoodsType() == 1){
                for (String id : coupon.getGoodsValue().split(",")) {
                    categoryIds.add(Long.valueOf(id));
                }
            } else if (coupon.getGoodsType() == 2){
                for (String id : coupon.getGoodsValue().split(",")) {
                    goodsIds.add(Long.valueOf(id));
                }
            }
        }
        List<GoodsInfo> goodsInfoList = new ArrayList<>();
        if (!goodsIds.isEmpty()){
            Result<List<GoodsInfo>> goodsListRes = goodsClient.getGoodsListByIds(goodsIds.stream().toList());
            if (goodsListRes.getResultCode() != 200) {
                CMallException.fail(RPC_ERROR.getResult());
            }
            goodsInfoList = goodsListRes.getData();
            for (GoodsInfo goodsInfo : goodsInfoList) {
                categoryIds.add(goodsInfo.getGoodsCategoryId());
            }
        }
        List<GoodsCategory> goodsCategoryList = new ArrayList<>();
        if (!categoryIds.isEmpty()){
            Result<List<GoodsCategory>> goodsCategoryListRes = goodsClient.getGoodsCategoryListByIds(categoryIds.stream().toList());
            if (goodsCategoryListRes.getResultCode() != 200) {
                CMallException.fail(RPC_ERROR.getResult());
            }
            goodsCategoryList = goodsCategoryListRes.getData();
        }

        Map<Long, String> categoryNamesMap = goodsCategoryList.stream().collect(
                Collectors.toMap(GoodsCategory::getCategoryId, GoodsCategory::getCategoryName));
        Map<Long, String> goodsCategoryNamesMap = goodsInfoList.stream().collect(
                Collectors.toMap(GoodsInfo::getGoodsId, e -> categoryNamesMap.get(e.getGoodsCategoryId())));
        return new Map[]{categoryNamesMap, goodsCategoryNamesMap};
    }
}
