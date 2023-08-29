package com.tsong.cmall.common.constants;

/**
 * @Author Tsong
 * @Date 2023/3/21 17:23
 */
public class Constants {
    public final static String FILE_UPLOAD_DIC = (System.getProperty("user.dir").equals("/") ? "" : System.getProperty("user.dir"))
            +"/upload/"; // 上传文件的默认url前缀，根据部署设置自行修改
    public final static int HOME_PAGE_CAROUSEL_NUMBER = 5;// 首页轮播图数量(可根据自身需求修改)

    public final static int HOME_PAGE_CATEGORY_NUMBER = 10;// 首页一级分类的最大数量

    public final static int SEARCH_PAGE_CATEGORY_NUMBER = 8;// 搜索页一级分类的最大数量

    public final static int HOME_PAGE_GOODS_HOT_NUMBER = 4;// 首页热卖商品数量
    public final static int HOME_PAGE_GOODS_NEW_NUMBER = 5;// 首页新品数量
    public final static int HOME_PAGE_GOODS_RECOMMENDED_NUMBER = 10;// 首页推荐商品数量

    public final static int SHOPPING_CART_ITEM_TOTAL_NUMBER = 20;// 购物车中商品的最大数量(可根据自身需求修改)

    public final static int SHOPPING_CART_ITEM_LIMIT_NUMBER = 10;// 购物车中单个商品的最大购买数量(可根据自身需求修改)

    public final static int REPEAT_INTERVAL_TIME = 5; // 重复提交的间隔时间

    public final static String MALL_VERIFY_CODE_KEY = "mallVerifyCode";// 验证码key

    public final static int GOODS_SEARCH_PAGE_LIMIT = 10;// 搜索分页的默认条数(每页10条)

    public final static int SHOPPING_CART_PAGE_LIMIT = 10;//购物车分页的默认条数(每页10条)
    public final static int MY_ORDERS_PAGE_LIMIT = 10;// 我的订单列表分页的默认条数(每页10条)
    public final static int MY_COUPONS_LIMIT = 10;// 我的优惠卷列表分页的默认条数(每页10条)

    public final static String COUPON_CODE = "couponCode:";// 优惠卷加密前缀

    public final static int SALE_STATUS_UP = 1;// 商品上架状态
    public final static int SALE_STATUS_DOWN = 0;// 商品下架状态

    public final static int TOKEN_LENGTH = 32;//token字段长度

    public final static long TOKEN_EXPIRED_TIME = 2 * 24 * 3600 * 1000; // 48小时过期

    /**
     * 字符编码
     */
    public static final String UTF_ENCODING = "UTF-8";

    /**
     * 秒杀下单盐值
     */
    public static final String SECKILL_ORDER_SALT = "asd";

    public static final String REDIS_KEY_PREFIX = "c-mall:";

    /**
     * 用户token缓存
     */
    public static final String MALL_USER_TOKEN_KEY = REDIS_KEY_PREFIX + "mall_user_token:";

    /**
     * 用户info缓存
     */
    public static final String MALL_USER_INFO_KEY = REDIS_KEY_PREFIX + "mall_user_info:";

    /**
     * 用户请求token缓存
     */
    public static final String REQUEST_KEY = REDIS_KEY_PREFIX + "request:";

    /**
     * 秒杀商品库存缓存
     */
    public static final String SECKILL_GOODS_STOCK_KEY = REDIS_KEY_PREFIX + "seckill_goods_stock:";

    /**
     * 秒杀商品缓存
     */
    public static final String SECKILL_KEY = REDIS_KEY_PREFIX + "seckill:";
    /**
     * 秒杀商品详情页面缓存
     */
    public static final String SECKILL_GOODS_DETAIL = REDIS_KEY_PREFIX + "seckill_goods_detail:";
    /**
     * 秒杀商品列表页面缓存
     */
    public static final String SECKILL_GOODS_LIST = REDIS_KEY_PREFIX + "seckill_goods_list";

    /**
     * 秒杀成功的用户set缓存
     */
    public static final String SECKILL_SUCCESS_USER_ID = REDIS_KEY_PREFIX + "seckill_success_user_id:";
}
