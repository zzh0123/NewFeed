package ys.app.feed.bean;

import java.io.Serializable;

public class CouponOrderSelectItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer couponId; // 优惠券id
    private String endDate; //  结束时间
    private String name; //  优惠券名
    private Double replaceMoney; // 抵扣金额,只有抵扣券才返回
    private String startDate; //  开始时间

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
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

    public Double getReplaceMoney() {
        return replaceMoney;
    }

    public void setReplaceMoney(Double replaceMoney) {
        this.replaceMoney = replaceMoney;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
