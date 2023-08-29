package com.tsong.cmall.coupon.service.impl;

import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.util.BeanUtil;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.coupon.mapper.CouponMapper;
import com.tsong.cmall.coupon.mapper.UserCouponRecordMapper;
import com.tsong.cmall.coupon.service.ICouponService;
import com.tsong.cmall.coupon.web.vo.CouponVO;
import com.tsong.cmall.vo.MyCouponVO;
import com.tsong.cmall.vo.ShoppingCartItemVO;
import com.tsong.cmall.entity.Coupon;
import com.tsong.cmall.entity.GoodsCategory;
import com.tsong.cmall.entity.GoodsInfo;
import com.tsong.cmall.entity.UserCouponRecord;
import com.tsong.feign.clients.goods.GoodsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;


/**
 * @Author Tsong
 * @Date 2023/3/21 22:58
 */
@Service
public class CouponService implements ICouponService {
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private UserCouponRecordMapper userCouponRecordMapper;
    @Autowired
    private GoodsClient goodsClient;

    @Override
    public Coupon getCouponById(Long id) {
        return couponMapper.selectByPrimaryKey(id);
    }

    @Override
    public Coupon getByUserCouponId(Long id) {
        UserCouponRecord userCouponRecord = userCouponRecordMapper.selectByPrimaryKey(id);
        return couponMapper.selectByPrimaryKey(userCouponRecord.getCouponId());
    }

    @Override
    public int updateUserCouponRecord(UserCouponRecord userCouponRecord) {
        return userCouponRecordMapper.updateByPrimaryKeySelective(userCouponRecord);
    }

    @Override
    public Boolean sendNewUserCoupons(Long userId) {
        List<Coupon> coupons = couponMapper.selectAvailableGivenCoupon();
        for (Coupon coupon : coupons) {
            UserCouponRecord userCouponRecord = UserCouponRecord.builder()
                    .userId(userId)
                    .couponId(coupon.getCouponId())
                    .build();
            if (userCouponRecordMapper.insertSelective(userCouponRecord) <= 0){
                CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
                return false;
            }
        }
        return true;
    }

    @Override
    public PageResult selectAvailableCoupons(Long userId, PageQueryUtil pageUtil) {
        // 查询当前上架的普通券
        List<Coupon> coupons = couponMapper.selectAvailableCoupon(pageUtil);
        List<CouponVO> couponVOList = BeanUtil.copyList(coupons, CouponVO.class);
        int total = coupons.size();
        if (total > 0){
            Map[] maps = getCategoryOrGoodsNamesMap(coupons);
            Map<Long, String> categoryNamesMap = maps[0];
            Map<Long, String> goodsCategoryNamesMap = maps[1];

            for (CouponVO couponVO : couponVOList) {
                if (userId != null) {
                    // 查找领券记录
                    int num = userCouponRecordMapper.getUserCouponCount(userId, couponVO.getCouponId());
                    if (num > 0) {
                        // 领过券了
                        couponVO.setHasReceived(true);
                    }
                }
                if (couponVO.getCouponTotal() != 0) { // 0 是无限的意思
                    // 没有库存了
                    if (couponVO.getCouponTotal() == 1) {
                        couponVO.setSoldOut(true);
                    }
                }
                // 处理限制值的名字
                StringBuilder namesBuilder = new StringBuilder();
                for (String id : couponVO.getGoodsValue().split(",")) {
                    if (couponVO.getGoodsType() == 1){
                        namesBuilder.append(categoryNamesMap.get(Long.valueOf(id)) + "分区");
                    } else if (couponVO.getGoodsType() == 2){
                        namesBuilder.append(goodsCategoryNamesMap.get(Long.valueOf(id)) + "分区指定商品");
                    }
                    namesBuilder.append(",");
                }
                if (!namesBuilder.isEmpty()){
                    namesBuilder.deleteCharAt(namesBuilder.length() - 1);
                }
                couponVO.setGoodsValueNames(namesBuilder.toString());
            }
        }
        return new PageResult(couponVOList, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    /**
     * @Description 用户领优惠券
     * @Param [couponId, userId]
     * @Return boolean
     */
    @Override
    @Transactional
    public boolean saveCouponUser(Long couponId, Long userId, String couponCode) {
        Coupon coupon;
        if (couponCode != null){
            coupon = couponMapper.selectByCode(couponCode);
            if (coupon == null){
                CMallException.fail("优惠券不存在！");
            }
        } else {
            coupon = couponMapper.selectByPrimaryKey(couponId);
        }
        if (coupon.getCouponLimit() != 0) { // 0 是该用户可以不限次数领该券
            // 查询该用户获得该券的数量
            int num = userCouponRecordMapper.getUserCouponCount(userId, coupon.getCouponId());
            if (num != 0) {
                CMallException.fail("优惠券已经领过了,无法再次领取！");
            }
        }
        if (coupon.getCouponTotal() == 1) {
            CMallException.fail("优惠券已经领完了！");
        }
        if (coupon.getCouponTotal() != 0) { // 0 是无限张数
            // couponTotal -= 1;
            // 这里where total > 1
            if (couponMapper.reduceCouponTotal(coupon.getCouponId()) <= 0) {
                CMallException.fail("优惠券领取失败！");
            }
        }
        // coupon.total > 1 || coupon.total == 0
        UserCouponRecord couponUserRecord = new UserCouponRecord();
        couponUserRecord.setUserId(userId);
        couponUserRecord.setCouponId(coupon.getCouponId());
        return userCouponRecordMapper.insertSelective(couponUserRecord) > 0;
    }

    @Override
    public PageResult selectMyCoupons(PageQueryUtil pageUtil) {
        List<UserCouponRecord> userCouponRecordList = userCouponRecordMapper.selectMyCouponRecords(pageUtil);
        int total = userCouponRecordList.size();
        List<MyCouponVO> myCouponVOList = new ArrayList<>();
        if (total > 0) {
            // 从领券记录转化成用户领券视图
            getMyCouponVOList(myCouponVOList, userCouponRecordList);
        }
        return new PageResult(myCouponVOList, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public List<MyCouponVO> selectAllMyAvailableCoupons(Long userId) {
        List<UserCouponRecord> userCouponRecordList = userCouponRecordMapper.selectMyAvailableCoupons(userId);
        List<Long> couponIds = userCouponRecordList.stream().map(UserCouponRecord::getCouponId).toList();
        List<MyCouponVO> myCouponVOList = new ArrayList<>();
        if (!couponIds.isEmpty()) {
            // 从领券记录转化成用户领券视图
            getMyCouponVOList(myCouponVOList, userCouponRecordList);
        }
        return myCouponVOList;
    }

    @Override
    public List<MyCouponVO> selectCouponsForOrderConfirm(List<ShoppingCartItemVO> myShoppingCartItems, BigDecimal priceTotal, Long userId) {
        List<UserCouponRecord> userCouponRecordList = userCouponRecordMapper.selectMyAvailableCoupons(userId);
        List<Long> couponIds = userCouponRecordList.stream().map(UserCouponRecord::getCouponId).toList();
        List<MyCouponVO> myCouponVOList = new ArrayList<>();
        if (!couponIds.isEmpty()) {
            // 从领券记录转化成用户领券视图
            getMyCouponVOList(myCouponVOList, userCouponRecordList);
        }

        long nowTime = new Date().getTime();
        // 筛选可用的券
        return myCouponVOList.stream().filter(item -> {
            // 排除过期的和未开始的券
            if (item.getUseStatus() != 0 || (item.getCouponStartTime() !=null && nowTime < item.getCouponStartTime().getTime())) {
                return false;
            }
            // 判断使用条件
            boolean isValid = false;
            if (priceTotal.compareTo(new BigDecimal(item.getMin())) >= 0) {
                if (item.getGoodsType() == 0){ // 全场通用
                    isValid = true;
                } else {
                    String[] split = item.getGoodsValue().split(",");
                    // 券的可用分类id / 商品id
                    Set<Long> goodsValueSet = Arrays.stream(split).map(Long::valueOf).collect(toSet());
                    // 从购物车里查找物品
                    List<Long> goodsIds = myShoppingCartItems.stream().map(ShoppingCartItemVO::getGoodsId).toList();

                    if (item.getGoodsType() == 1) { // 指定分类可用
                        Result goodsListResult = goodsClient.getGoodsListByIds(goodsIds);
                        if (goodsListResult.getResultCode() != 200){
                            CMallException.fail("rpc error: " + goodsListResult.getMessage());
                        }
                        List<GoodsInfo> goodsList = (List<GoodsInfo>) goodsListResult.getData();
                        // 分类id集
                        Set<Long> categoryIds = goodsList.stream().map(GoodsInfo::getGoodsCategoryId).collect(toSet());
                        for (Long categoryId : categoryIds) {
                            if (goodsValueSet.contains(categoryId)) {
                                isValid = true;
                                break;
                            }
                        }
                    } else if (item.getGoodsType() == 2) { // 指定商品可用
                        for (Long goodsId : goodsIds) {
                            if (goodsValueSet.contains(goodsId)) {
                                isValid = true;
                                break;
                            }
                        }
                    }
                }
            }
            return isValid;
        }).sorted(Comparator.comparingInt(MyCouponVO::getDiscount)).toList();
    }

    private void getMyCouponVOList(List<MyCouponVO> myCouponVOList, List<UserCouponRecord> userCouponRecordList){
        // 获取用户领券的id集合
        List<Long> couponIdList = userCouponRecordList.stream().map(UserCouponRecord::getCouponId).toList();
        if (!CollectionUtils.isEmpty(couponIdList)) {
            Date now = new Date();
            Calendar sevenDaysAgo = Calendar.getInstance();
            sevenDaysAgo.setTime(now);
            sevenDaysAgo.add(Calendar.DATE, -7);

            // 用id集合查询券
            List<Coupon> couponList = couponMapper.selectByIds(couponIdList);

            Map[] maps = getCategoryOrGoodsNamesMap(couponList);
            Map<Long, String> categoryNamesMap = maps[0];
            Map<Long, String> goodsCategoryNamesMap = maps[1];

            // 未使用但过期的已领券
            List<UserCouponRecord> expiredAndNotUsedUserCouponRecordList = new ArrayList<>();
            // map，用id找coupon
            // 因为couponVO需要coupon的属性，以及userCoupon的属性
            Map<Long, Coupon> couponMap = couponList.stream().collect(toMap(Coupon::getCouponId, coupon -> coupon));
            for (UserCouponRecord userCouponRecord : userCouponRecordList) {
                MyCouponVO myCouponVO = new MyCouponVO();
                Coupon coupon = couponMap.get(userCouponRecord.getCouponId());
                if (coupon != null){
                    BeanUtil.copyProperties(coupon, myCouponVO);
                    myCouponVO.setCouponUserId(userCouponRecord.getCouponUserId());
                    myCouponVO.setCouponUserCreateTime(userCouponRecord.getCreateTime());
                    // 优惠券过期未使用
                    if (userCouponRecord.getUseStatus() != 1
                            // 如果没有过期时间则领取7日之后过期
                            // 如果有过期时间，则过期时间之后过期
                            && ((coupon.getCouponEndTime() == null && userCouponRecord.getCreateTime().getTime() < sevenDaysAgo.getTimeInMillis())
                                || (coupon.getCouponEndTime() != null && now.getTime() > coupon.getCouponEndTime().getTime()))
                    ){
                        myCouponVO.setUseStatus((byte) 2);
                        expiredAndNotUsedUserCouponRecordList.add(userCouponRecord);
                    } else {
                        myCouponVO.setUseStatus(userCouponRecord.getUseStatus());
                    }
                    // 处理限制值的名字
                    StringBuilder namesBuilder = new StringBuilder();
                    for (String id : myCouponVO.getGoodsValue().split(",")) {
                        if (myCouponVO.getGoodsType() == 1){
                            namesBuilder.append(categoryNamesMap.get(Long.valueOf(id))+"分区");
                        } else if (myCouponVO.getGoodsType() == 2){
                            namesBuilder.append(goodsCategoryNamesMap.get(Long.valueOf(id)) + "分区指定商品");
                        }
                        namesBuilder.append(",");
                    }
                    if (!namesBuilder.isEmpty()){
                        namesBuilder.deleteCharAt(namesBuilder.length() - 1);
                    }
                    myCouponVO.setGoodsValueNames(namesBuilder.toString());
                    myCouponVOList.add(myCouponVO);
                }
            }
            if (!expiredAndNotUsedUserCouponRecordList.isEmpty()){
                List<Long> userCouponRecordIds = expiredAndNotUsedUserCouponRecordList.stream()
                        .map(UserCouponRecord::getCouponUserId).toList();
                if (userCouponRecordMapper.expireBatch(userCouponRecordIds) <= 0){
                    CMallException.fail("设置用户已领券失效失败");
                }
            }
        }
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
            Result goodsListResult = goodsClient.getGoodsListByIds(goodsIds.stream().toList());
            if (goodsListResult.getResultCode() != 200){
                CMallException.fail("rpc error: " + goodsListResult.getMessage());
            }
            goodsInfoList = (List<GoodsInfo>) goodsListResult.getData();
            for (GoodsInfo goodsInfo : goodsInfoList) {
                categoryIds.add(goodsInfo.getGoodsCategoryId());
            }
        }
        List<GoodsCategory> goodsCategoryList = new ArrayList<>();
        if (!categoryIds.isEmpty()){
            Result goodsCategoryListResult = goodsClient.getGoodsCategoryListByIds(categoryIds.stream().toList());
            if (goodsCategoryListResult.getResultCode() != 200){

            }
            goodsCategoryList = (List<GoodsCategory>) goodsCategoryListResult.getData();
        }

        Map<Long, String> categoryNamesMap = goodsCategoryList.stream().collect(
                Collectors.toMap(GoodsCategory::getCategoryId, GoodsCategory::getCategoryName));
        Map<Long, String> goodsCategoryNamesMap = goodsInfoList.stream().collect(
                Collectors.toMap(GoodsInfo::getGoodsId, e -> categoryNamesMap.get(e.getGoodsCategoryId())));
        return new Map[]{categoryNamesMap, goodsCategoryNamesMap};
    }

    @Override
    public boolean deleteCouponUser(Long couponUserId) {
        return userCouponRecordMapper.deleteByPrimaryKey(couponUserId) > 0;
    }

    @Override
    public void releaseCoupon(Long orderId) {
        UserCouponRecord userCouponRecord = userCouponRecordMapper.getUserCouponByOrderId(orderId);
        if (userCouponRecord != null){
            userCouponRecord.setUseStatus((byte) 0);
            userCouponRecord.setUpdateTime(new Date());
            userCouponRecordMapper.updateByPrimaryKey(userCouponRecord);
        }
    }

    @Override
    @Transactional
    public void insertUserCouponRecordBatch(List<UserCouponRecord> userCouponRecordList) {
        for (UserCouponRecord userCouponRecord : userCouponRecordList) {
            if (userCouponRecordMapper.insertSelective(userCouponRecord) <= 0){
                CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
        }
    }

    @Override
    public Coupon getByOrderId(Long orderId) {
        UserCouponRecord userCouponRecord = userCouponRecordMapper.getUserCouponByOrderId(orderId);
        if (userCouponRecord != null){
            return couponMapper.selectByPrimaryKey(userCouponRecord.getCouponId());
        }
        return null;
    }
}
