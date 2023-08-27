package com.tsong.cmall.common.exception;

/**
 * @Author Tsong
 * @Date 2023/3/22 01:09
 */
public class CMallException extends RuntimeException{
    public CMallException() {
    }

    public CMallException(String message) {
        super(message);
    }

    /**
     * @Description 丢出一个异常
     * @Param [message]
     * @Return void
     */
    public static void fail(String message) {
        throw new CMallException(message);
    }
}
