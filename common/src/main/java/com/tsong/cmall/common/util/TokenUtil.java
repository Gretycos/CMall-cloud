package com.tsong.cmall.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

/**
 * @Author Tsong
 * @Date 2023/3/27 19:30
 */
public class TokenUtil {
    public static String genToken(String src) {
        if (null == src || "".equals(src)) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(src.getBytes());
            String result = new BigInteger(1, md.digest()).toString(16);
            Random random = new Random();
            while (result.length() < 32) {
                result = result + random.nextInt(10);
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
