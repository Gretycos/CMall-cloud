package com.tsong.cmall.order.enums;

/**
 * @Author Tsong
 * @Date 2023/3/24 22:10
 */
public enum PayStatusEnum {
    DEFAULT(-1, "支付失败"),
    PAY_PAYING(0, "支付中"),
    PAY_SUCCESSFUL(1, "支付成功");

    private int payStatus;

    private String name;

    PayStatusEnum(int payStatus, String name) {
        this.payStatus = payStatus;
        this.name = name;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
