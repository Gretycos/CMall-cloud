package com.tsong.cmall.order.service.impl;

import com.tsong.cmall.common.enums.ServiceResultEnum;
import com.tsong.cmall.common.exception.CMallException;
import com.tsong.cmall.common.mq.MessageHandler;
import com.tsong.cmall.common.mq.MyMsg;
import com.tsong.cmall.common.util.*;
import com.tsong.cmall.entity.*;
import com.tsong.cmall.msg.SeckillStockMsg;
import com.tsong.cmall.order.enums.OrderStatusEnum;
import com.tsong.cmall.order.enums.PayStatusEnum;
import com.tsong.cmall.order.enums.PayTypeEnum;
import com.tsong.cmall.order.mapper.OrderAddressMapper;
import com.tsong.cmall.order.mapper.OrderItemMapper;
import com.tsong.cmall.order.mapper.OrderMapper;
import com.tsong.cmall.dto.StockNumDTO;
import com.tsong.cmall.order.mapper.dto.OrderPayStatusDTO;
import com.tsong.cmall.order.redis.RedisCache;
import com.tsong.cmall.order.service.IOrderService;
import com.tsong.cmall.vo.order.OrderDetailVO;
import com.tsong.cmall.vo.order.OrderItemVO;
import com.tsong.cmall.vo.order.OrderVO;
import com.tsong.cmall.vo.shopping_cart.ShoppingCartItemVO;
import com.tsong.feign.clients.user.address.AddressClient;
import com.tsong.feign.clients.coupon.CouponClient;
import com.tsong.feign.clients.goods.GoodsClient;
import com.tsong.feign.clients.shopping_cart.ShoppingCartClient;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tsong.cmall.common.constants.Constants.*;
import static com.tsong.cmall.common.constants.MQExchangeCons.CMALL_DIRECT;
import static com.tsong.cmall.common.constants.MQRoutingKeyCons.*;
import static com.tsong.cmall.common.enums.ServiceResultEnum.RPC_ERROR;
import static java.util.stream.Collectors.groupingBy;

/**
 * @Author Tsong
 * @Date 2023/3/24 15:44
 */
@Service
public class OrderService implements IOrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private OrderAddressMapper orderAddressMapper;
    @Resource
    private ShoppingCartClient shoppingCartClient;
    @Resource
    private GoodsClient goodsClient;
    @Resource
    private CouponClient couponClient;
    @Resource
    private AddressClient addressClient;
    @Resource
    private MessageHandler messageHandler;
    @Resource
    private RedisCache redisCache;
    @Resource(name = "orderThreadPool")
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    @GlobalTransactional
    public String saveOrder(Long userId, Long couponUserId, Long addressId, Long[] cartItemIds){

        // 获得下单的token，确保幂等性
        String itemIdsStr = Arrays.stream(cartItemIds).map(Object::toString).collect(Collectors.joining(","));
        String orderToken = MD5Util.MD5Encode(userId+":"+addressId+":"+itemIdsStr, UTF_ENCODING);

        // 加锁下单
        String lockKey = ORDER_CREATE_LOCK + orderToken;
        String lockValue = redisCache.tryLock(lockKey, 10);
        if (lockValue == null) {
            CMallException.fail("请勿重复提交下单");
        }

        // 最终要同步的子线程任务列表
        List<CompletableFuture<?>> futureList = new ArrayList<>();

        // 购物车项目id表
        List<Long> itemIdList = Arrays.asList(cartItemIds);
        if (CollectionUtils.isEmpty(itemIdList)) {
            CMallException.fail(ServiceResultEnum.ORDER_GENERATE_ERROR.getResult());
        }

        // 查询参与结算的购物车项目
        Result<List<ShoppingCartItemVO>> cartItemsResult = shoppingCartClient.getCartItemsByIds(itemIdList);
        if (cartItemsResult.getResultCode() != 200) {
            CMallException.fail(RPC_ERROR.getResult() + cartItemsResult.getMessage());
        }
        List<ShoppingCartItemVO> shoppingCartItemList = cartItemsResult.getData();

        // 商品id表
        List<Long> goodsIds = shoppingCartItemList.stream()
                .map(ShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(goodsIds)) {
            CMallException.fail(ServiceResultEnum.ORDER_GENERATE_ERROR.getResult());
        }

        // 商品表
        Result<List<GoodsInfo>> goodsListResult = goodsClient.getGoodsListByIds(goodsIds);
        if (goodsListResult.getResultCode() != 200){
            CMallException.fail(RPC_ERROR.getResult() + goodsListResult.getMessage());
        }
        List<GoodsInfo> goodsInfoList = goodsListResult.getData();
        if (CollectionUtils.isEmpty(goodsInfoList)) {
            CMallException.fail(ServiceResultEnum.ORDER_GENERATE_ERROR.getResult());
        }

        // 检查是否包含已下架商品
        List<GoodsInfo> goodsListNotSelling = goodsInfoList.stream()
                .filter(goodsTemp -> goodsTemp.getGoodsSaleStatus() != SALE_STATUS_UP)
                .toList();
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            // goodsListNotSelling 对象非空则表示有下架商品
            CMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }

        // 判断商品库存
        // id 映射成GoodsInfo，相同id的合并
        Map<Long, GoodsInfo> goodsInfoMap = goodsInfoList.stream()
                .collect(Collectors.toMap(GoodsInfo::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        for (ShoppingCartItemVO shoppingCartItemVO : shoppingCartItemList) {
            // 查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!goodsInfoMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                CMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            // 存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > goodsInfoMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                CMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }


        // 购物车清空结算的项目
        futureList.add(CompletableFuture.runAsync(() -> {
                Result<Boolean> shoppingCartDeleteResult = shoppingCartClient.deleteCartItemsByIds(itemIdList);
                if (shoppingCartDeleteResult.getResultCode() != 200) {
                    CMallException.fail(RPC_ERROR.getResult() + shoppingCartDeleteResult.getMessage());
                }
            }, threadPoolExecutor)
        );

        // 查找领券记录
        CompletableFuture<Coupon> couponFuture;
        if (couponUserId != null) {
            couponFuture = CompletableFuture.supplyAsync(() -> {
                Result<Coupon> couponResult = couponClient.getCouponByCouponUserId(couponUserId);
                if (couponResult.getResultCode() != 200) {
                    CMallException.fail(RPC_ERROR.getResult() + couponResult.getMessage());
                }
                return couponResult.getData();
            }, threadPoolExecutor);
        } else {
            couponFuture = null;
        }

        // 生成订单号
        String orderNo = NumberUtil.genOrderNo();

        // 生成订单
        CompletableFuture<Long> genOrderFuture = CompletableFuture.supplyAsync(()->{
            Order order = Order.builder()
                    .orderNo(orderNo)
                    .userId(userId)
                    .build();

            // 总价
            BigDecimal priceTotal = new BigDecimal(0);
            for (ShoppingCartItemVO shoppingCartItemVO : shoppingCartItemList) {
                priceTotal = priceTotal
                        .add(shoppingCartItemVO.getSellingPrice().multiply(new BigDecimal(shoppingCartItemVO.getGoodsCount())))
                        .setScale(2, RoundingMode.HALF_UP) ;
            }
            // 如果使用了优惠券
            if (couponFuture != null) {
                try {
                    priceTotal = priceTotal.subtract(new BigDecimal(couponFuture.get().getDiscount()));
                } catch (Exception e) {
                    CMallException.fail(e.getMessage());
                }
            }

            if (priceTotal.compareTo(new BigDecimal(1)) < 0) {
                CMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
            }
            order.setTotalPrice(priceTotal);
            String extraInfo = "cmall-支付宝沙箱支付";
            order.setExtraInfo(extraInfo);
            // 生成订单并保存订单纪录
            if (orderMapper.insertSelective(order) <= 0) {
                CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            return order.getOrderId();
        }, threadPoolExecutor);

        // 获得订单Id，阻塞
        Long orderId = null;
        try {
            orderId = genOrderFuture.get();
        } catch (Exception e) {
            CMallException.fail(e.getMessage());
        }

        Long finalOrderId = orderId;
        // 更新优惠券使用状态
        if (couponUserId != null) {
            futureList.add(CompletableFuture.runAsync(() -> {
                UserCouponRecord userCouponRecord = UserCouponRecord.builder()
                        .couponUserId(couponUserId)
                        .orderId(finalOrderId)
                        .useStatus((byte) 1)
                        .usedTime(new Date())
                        .updateTime(new Date())
                        .build();
                Result<Integer> updateUserCouponResult = couponClient.updateUserCouponRecord(userCouponRecord);
                if (updateUserCouponResult.getResultCode() != 200){
                    CMallException.fail(RPC_ERROR.getResult() + updateUserCouponResult.getMessage());
                }
                if (updateUserCouponResult.getData() <= 0){
                    CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
                }
            }, threadPoolExecutor));
        }

        // 生成订单地址快照
        futureList.add(CompletableFuture.runAsync(() -> {
            // 查询用户地址
            UserAddress address = getUserAddress_RPC(addressId);
            genOrderAddressSnapshot(finalOrderId, address);
        }, threadPoolExecutor));

        // 生成所有的订单项快照，并保存至数据库
        futureList.add(CompletableFuture.runAsync(() -> {
            List<OrderItem> orderItemList = new ArrayList<>();
            for (ShoppingCartItemVO shoppingCartItemVO : shoppingCartItemList) {
                OrderItem orderItem = new OrderItem();
                // 使用BeanUtil工具类将ShoppingCartItemVO中的属性复制到OrderItem对象中
                BeanUtil.copyProperties(shoppingCartItemVO, orderItem);
                // OrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                orderItem.setOrderId(finalOrderId);
                orderItemList.add(orderItem);
            }
            // 保存订单项快照至数据库
            if (orderItemMapper.insertBatch(orderItemList) <= 0) {
                CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
        }, threadPoolExecutor));

        // 等待任务执行完
        for (CompletableFuture<?> future : futureList) {
            try {
                future.join();
            } catch (Exception e) {
                CMallException.fail(e.getMessage());
            }
        }

        List<MyMsg> myMsgList = new ArrayList<>();

        // 更新库存
        // 消息队列
        List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(shoppingCartItemList, StockNumDTO.class);
//        messageHandler.sendMessage(CMALL_DIRECT, GOODS_STOCK_DECREASE, stockNumDTOS);
        myMsgList.add(new MyMsg(CMALL_DIRECT, GOODS_STOCK_DECREASE, stockNumDTOS));

        // 订单超时未支付，超过300秒自动取消订单
        // 延迟队列
//        messageHandler.sendMessage(CMALL_DIRECT, ORDER_UNPAID, orderId);
        myMsgList.add(new MyMsg(CMALL_DIRECT, ORDER_UNPAID, orderId));

        messageHandler.sendMessageBatch(myMsgList);

        // 解锁下单
        redisCache.unlock(lockKey, lockValue);

        // 所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
        return orderNo;
    }

    @Override
    public String getSeckillOrderNo(Long userId, Long seckillId, String seckillSecretKey) {
        if (!seckillSecretKey.equals(
                MD5Util.MD5Encode(seckillId + userId + SECKILL_ORDER_SALT, UTF_ENCODING))){
            CMallException.fail("未查询到订单信息");
        }
        String orderNo = orderMapper.selectOrderNoByUserIdAndSeckillId(userId, seckillId);
        return orderNo == null ? "" : orderNo;
    }

    @Override
    public OrderDetailVO getOrderDetailByOrderId(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            CMallException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        return genOrderDetailVO(order);
    }

    @Override
    public OrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            CMallException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        // 验证是否是当前userId下的订单，否则报错
        if (!userId.equals(order.getUserId())) {
            CMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        return genOrderDetailVO(order);
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public OrderPayStatusDTO getOrderStatus(Long orderId) {
        OrderPayStatusDTO payStatusDTO = orderMapper.selectStatusByPrimaryKey(orderId);
        if (payStatusDTO == null){
            CMallException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        return payStatusDTO;
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        List<Order> orderList = orderMapper.selectOrderList(pageUtil);
        int total = orderMapper.getTotalOrders(pageUtil);
        List<OrderVO> orderVOList = new ArrayList<>();
        if (total > 0) {
            // 数据转换 将实体类转成vo
            orderVOList = BeanUtil.copyList(orderList, OrderVO.class);
            // 设置订单状态显示值
            for (OrderVO orderVO : orderVOList) {
                orderVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(orderVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = orderList.stream().map(Order::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<OrderItem> orderItemList = orderItemMapper.selectByOrderIds(orderIds);
                // orderId->此order包含的item表
                Map<Long, List<OrderItem>> itemByOrderIdMap = orderItemList.stream().collect(groupingBy(OrderItem::getOrderId));
                for (OrderVO orderVO : orderVOList) {
                    // 封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(orderVO.getOrderId())) {
                        List<OrderItem> orderItemListTemp = itemByOrderIdMap.get(orderVO.getOrderId());
                        // 将OrderItem对象列表转换成OrderItemVO对象列表
                        List<OrderItemVO> orderItemVOList = BeanUtil.copyList(orderItemListTemp, OrderItemVO.class);
                        orderVO.setOrderItemVOList(orderItemVOList);
                    }
                }
            }
        }
        return new PageResult(orderVOList, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            // 验证是否是当前userId下的订单，否则报错
            if (!userId.equals(order.getUserId())) {
                CMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            // 订单状态判断
            if (order.getOrderStatus().intValue() == OrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || order.getOrderStatus().intValue() == OrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || order.getOrderStatus().intValue() == OrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || order.getOrderStatus().intValue() == OrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            //修改订单状态 && 恢复库存
            if (orderMapper.closeOrder(
                    Collections.singletonList(order.getOrderId()),
                    OrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0
            && recoverStock(order.getOrderId())) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String deleteOrder(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null){
            // 验证是否是当前userId下的订单，否则报错
            if (!userId.equals(order.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            // 订单状态判断，如果不是关闭或完成的状态则报错
            if (order.getOrderStatus().intValue() >= 0
                    && order.getOrderStatus().intValue() != OrderStatusEnum.ORDER_SUCCESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            order.setIsDeleted((byte) 1);
            order.setUpdateTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(order) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            // 验证是否是当前userId下的订单，否则报错
            if (!userId.equals(order.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            // 订单状态判断 非出库状态下不进行修改操作
            if (order.getOrderStatus().intValue() != OrderStatusEnum.ORDER_EXPRESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            order.setOrderStatus((byte) OrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            order.setUpdateTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(order) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
        }
        // 订单状态判断 非待支付状态下不进行修改操作
        if (order.getOrderStatus().intValue() != OrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
        }
        order.setOrderStatus((byte) OrderStatusEnum.ORDER_PAID.getOrderStatus());
        order.setPayType((byte) payType);
        order.setPayStatus((byte) PayStatusEnum.PAY_SUCCESSFUL.getPayStatus());
        order.setPayTime(new Date());
        order.setUpdateTime(new Date());
        if (orderMapper.updateByPrimaryKeySelective(order) <= 0) {
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.SUCCESS.getResult();
    }

    @Override
    public List<OrderItemVO> getOrderItems(Long id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order != null) {
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getOrderId());
            // 获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                return BeanUtil.copyList(orderItems, OrderItemVO.class);
            }
        }
        return null;
    }

    @Override
    public void handleUnpaidOrder(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        assert order != null;
        order.setOrderStatus((byte) OrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus());
        order.setUpdateTime(new Date());
        if (orderMapper.updateByPrimaryKey(order) <= 0){
            CMallException.fail(ServiceResultEnum.CLOSE_ORDER_ERROR.getResult());
        }
        // 商品货品数量恢复
        // 根据订单查找商品表
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderId(orderId);
        if (!CollectionUtils.isEmpty(orderItemList)){
            Long seckillId = order.getSeckillId();
            // 如果是秒杀订单
            if (seckillId != null){
                Long userId = order.getUserId();
                recoverSeckillStock(userId, seckillId);
            } else {
                recoverStock(orderItemList);
                // 返还优惠券
                messageHandler.sendMessage(CMALL_DIRECT, COUPON_RECOVER, orderId);
            }
        }
    }

    @Override
    @Transactional
    public void handleSeckillSaveOrder(Long userId, Long seckillId, Long goodsId,
                                       Long addressId, BigDecimal seckillPrice) {

        // 获得下单的token，确保幂等性
        String orderToken = MD5Util.MD5Encode(userId+":"+addressId+":"+seckillId, UTF_ENCODING);

        // 加锁下单
        String lockKey = ORDER_CREATE_LOCK + orderToken;
        String lockValue = redisCache.tryLock(lockKey, 10);
        if (lockValue == null) {
            CMallException.fail("请勿重复提交下单");
        }

        // 最终要同步的子线程任务列表
        List<CompletableFuture<?>> futureList = new ArrayList<>();

        // 查找商品
        CompletableFuture<GoodsInfo> goodsInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<GoodsInfo> goodsResult = goodsClient.getGoodsById(goodsId);
            if (goodsResult.getResultCode() != 200) {
                CMallException.fail(RPC_ERROR.getResult() + goodsResult.getMessage());
            }
            return goodsResult.getData();
        }, threadPoolExecutor);

        // 保存订单
        CompletableFuture<Long> genOrderFuture = CompletableFuture.supplyAsync(() -> {
            // 生成订单号
            String orderNo = NumberUtil.genOrderNo();
            Order order = Order.builder()
                    .orderNo(orderNo)
                    .seckillId(seckillId)
                    .totalPrice(seckillPrice)
                    .userId(userId)
                    .orderStatus((byte) OrderStatusEnum.ORDER_PRE_PAY.getOrderStatus())
                    .build();
            String extraInfo = "";
            order.setExtraInfo(extraInfo);
            if (orderMapper.insertSelective(order) <= 0) {
                CMallException.fail("生成秒杀订单异常");
            }
            return order.getOrderId();
        }, threadPoolExecutor);

        Long orderId = null;
        try {
            orderId = genOrderFuture.get();
        } catch (Exception e) {
            CMallException.fail(e.getMessage());
        }

        // 生成订单地址快照，并保存至数据库
        Long finalOrderId = orderId;
        futureList.add(CompletableFuture.runAsync(() -> {
            // 查找用户地址
            UserAddress address = getUserAddress_RPC(addressId);
            genOrderAddressSnapshot(finalOrderId, address);
        }, threadPoolExecutor));

        // 保存订单商品项
        futureList.add(CompletableFuture.runAsync(() -> {
            GoodsInfo goodsInfo = null;
            try {
                goodsInfo = goodsInfoFuture.get();
            } catch (Exception e) {
                CMallException.fail(e.getMessage());
            }
            OrderItem orderItem = OrderItem.builder()
                    .orderId(finalOrderId)
                    .goodsId(goodsInfo.getGoodsId())
                    .goodsName(goodsInfo.getGoodsName())
                    .goodsCoverImg(goodsInfo.getGoodsCoverImg())
                    .goodsCount(1)
                    .sellingPrice(seckillPrice)
                    .build();
            if (orderItemMapper.insertSelective(orderItem) <= 0) {
                CMallException.fail("生成订单项异常");
            }
        }, threadPoolExecutor));

        for (CompletableFuture<?> future : futureList) {
            try {
                future.join();
            }catch (Exception e) {
                CMallException.fail(e.getMessage());
            }

        }

        // 订单超时未支付
        messageHandler.sendMessage(CMALL_DIRECT, ORDER_SECKILL_UNPAID, orderId);

        // 解锁下单
        redisCache.unlock(lockKey, lockValue);
    }

    /**
     * @Description 查找用户地址
     * @Param [addressId]
     * @Return com.tsong.cmall.entity.UserAddress
     */
    private UserAddress getUserAddress_RPC(Long addressId) {
        Result<UserAddress> addressResult = addressClient.getAddressById(addressId);
        if (addressResult.getResultCode() != 200) {
            CMallException.fail(RPC_ERROR.getResult() + addressResult.getMessage());
        }
        UserAddress address = addressResult.getData();
        return address;
    }

    /**
     * @Description 生成订单地址快照，并保存至数据库
     * @Param [orderId, address]
     * @Return void
     */
    private void genOrderAddressSnapshot(Long orderId, UserAddress address) {
        OrderAddress orderAddress = new OrderAddress();
        // 用户地址->订单地址
        BeanUtil.copyProperties(address, orderAddress);
        // OrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
        orderAddress.setOrderId(orderId);
        // 保存订单地址快照至数据库
        if (orderAddressMapper.insertSelective(orderAddress) <= 0){
            CMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    /**
     * 生成订单细节VO
     * 细节中要展示每个订单项的实际付款
     * */
    private OrderDetailVO genOrderDetailVO(Order order){
        // 获取订单项数据
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getOrderId());
        if (CollectionUtils.isEmpty(orderItems)) {
            CMallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        // 拷贝到订单项VOList
        List<OrderItemVO> orderItemVOList = BeanUtil.copyList(orderItems, OrderItemVO.class);
        OrderAddress orderAddress = orderAddressMapper.selectByPrimaryKey(order.getOrderId());
        if (orderAddress == null){
            CMallException.fail(ServiceResultEnum.ORDER_ADDRESS_NULL_ERROR.getResult());
        }
        // 订单VO
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtil.copyProperties(order, orderDetailVO);
        orderDetailVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(orderDetailVO.getOrderStatus()).getName());
        orderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(orderDetailVO.getPayType()).getName());
        orderDetailVO.setUserAddress(orderAddress.toString());
        // 计算每个订单项的实际付款价格
        calPaidPrice(order, orderItemVOList);
        orderDetailVO.setOrderItemVOList(orderItemVOList);

        // 优惠券信息
        Result<Coupon> couponResult = couponClient.getCouponByOrderId(order.getOrderId());
        if (couponResult.getResultCode() != 200){
            CMallException.fail(RPC_ERROR.getResult() + couponResult.getMessage());
        }
        Coupon coupon = couponResult.getData();
        if (coupon != null) {
            orderDetailVO.setDiscount(new BigDecimal(coupon.getDiscount()));
        }
        return orderDetailVO;
    }


    private void calPaidPrice(Order order, List<OrderItemVO> orderItemVOList){
        BigDecimal paidTotal = order.getTotalPrice();
        BigDecimal totalPrice = orderItemVOList.stream()
                .map(e -> e.getSellingPrice().multiply(new BigDecimal(e.getGoodsCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        for (OrderItemVO orderItemVO : orderItemVOList) {
            BigDecimal sellingPrice = orderItemVO.getSellingPrice().multiply(new BigDecimal(orderItemVO.getGoodsCount()));
            if (paidTotal.compareTo(totalPrice) == 0){
                orderItemVO.setPaidPrice(sellingPrice);
            }else{
                orderItemVO.setPaidPrice(sellingPrice.divide(totalPrice,2,RoundingMode.HALF_UP).multiply(paidTotal));
            }
        }
    }

    /**
     * @Description 订单关闭恢复库存
     * @Param [orderIds]
     * @Return java.lang.Boolean
     */
    private Boolean recoverStock(Long orderId) {
        //查询对应的订单项
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderId(orderId);
        //获取对应的商品id和商品数量并赋值到StockNumDTO对象中
        recoverStock(orderItemList);
        return true;
    }
    private void recoverStock(List<OrderItem> orderItemList) {
        //获取对应的商品id和商品数量并赋值到StockNumDTO对象中
        List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(orderItemList, StockNumDTO.class);
        // 执行恢复库存的操作
        // 消息队列
        messageHandler.sendMessage(CMALL_DIRECT, GOODS_STOCK_RECOVER, stockNumDTOS);
    }

    private void recoverSeckillStock(Long userId, Long seckillId){
        SeckillStockMsg seckillStockMsg = new SeckillStockMsg();
        seckillStockMsg.setUserId(userId);
        seckillStockMsg.setSeckillId(seckillId);
        messageHandler.sendMessage(CMALL_DIRECT, SECKILL_STOCK_RECOVER, seckillStockMsg);
    }

}
