package com.tsong.cmall.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;

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
            if (result.length() == 31) {
                result = result + "-";
            }
            System.out.println(result);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
