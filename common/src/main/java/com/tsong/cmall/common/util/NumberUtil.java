package com.tsong.cmall.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Tsong
 * @Date 2023/3/24 18:22
 */
public class NumberUtil {
    /**
     * @Description 生成指定长度的随机数
     * @Param [length]
     * @Return int
     */
    public static int genRandomNum(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }

    /**
     * @Description 生成订单流水号
     * @Param []
     * @Return java.lang.String
     */
    public static String genOrderNo() {
        StringBuilder buffer = new StringBuilder(String.valueOf(System.currentTimeMillis()));
        int num = genRandomNum(4);
        buffer.append(num);
        return buffer.toString();
    }

    /**
     * @Description 判断是否为11位电话号码
     * @Param [phone]
     * @Return boolean
     */
    public static boolean isPhone(String phone) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[0-8])|(18[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
}
