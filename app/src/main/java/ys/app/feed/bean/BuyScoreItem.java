package ys.app.feed.bean;

import java.io.Serializable;

/**
 *  冲积分
 */
public class BuyScoreItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String coupon; // 券编码
    private String couponStr; // 券名称

    private Integer id; // 130.0,
    private Double money; // "所需金额%肉牛育肥期浓缩饲料3028"

    private Double score; // "获得积分"

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getCouponStr() {
        return couponStr;
    }

    public void setCouponStr(String couponStr) {
        this.couponStr = couponStr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
