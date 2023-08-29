package com.tsong.cmall.goods.enums;

/**
 * 首页配置项 1-搜索框热搜 2-搜索下拉框热搜 3-(首页)热销商品 4-(首页)新品上线 5-(首页)为你推荐
 * @Author Tsong
 * @Date 2023/3/31 23:04
 */
public enum HomePageConfigTypeEnum {
    DEFAULT(0, "DEFAULT"),
    HOME_PAGE_SEARCH_HOT(1, "HOME_PAGE_SEARCH_HOT"),
    HOME_PAGE_SEARCH_DOWN_HOT(2, "HOME_PAGE_SEARCH_DOWN_HOT"),
    HOME_PAGE_GOODS_HOT(3, "HOME_PAGE_GOODS_HOT"),
    HOME_PAGE_GOODS_NEW(4, "HOME_PAGE_GOODS_NEW"),
    HOME_PAGE_GOODS_RECOMMENDED(5, "HOME_PAGE_GOODS_RECOMMENDED");

    private int type;

    private String name;

    public static HomePageConfigTypeEnum getHomePageConfigTypeEnumByType(int type){
        for (HomePageConfigTypeEnum homePageConfigTypeEnum : HomePageConfigTypeEnum.values()) {
            if (homePageConfigTypeEnum.getType() == type){
                return homePageConfigTypeEnum;
            }
        }
        return DEFAULT;
    }

    HomePageConfigTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
