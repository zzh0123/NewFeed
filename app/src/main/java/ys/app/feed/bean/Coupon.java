package ys.app.feed.bean;

import java.io.Serializable;

public class Coupon implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer couponId; //
    private String couponType; // 类型

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    private String endDate; //  结束时间
    private String name; //  优惠券名
    private String startDate; //  开始时间
    private String replaceMoney; // 代金券金额
    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getReplaceMoney() {
        return replaceMoney;
    }

    public void setReplaceMoney(String replaceMoney) {
        this.replaceMoney = replaceMoney;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
