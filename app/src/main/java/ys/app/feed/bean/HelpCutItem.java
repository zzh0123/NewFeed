package ys.app.feed.bean;

import java.io.Serializable;

/**
 *  砍价帮item
 */
public class HelpCutItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long cutDate; // 1561796042000
    private String name; // "183****4536"
    private String headPortrait; // "http://www.huihemuchang.com/nfs/head_portrait/default.png"
    private Double cutMoney; // 19.78
    private String userTypeStr; //

    public String getUserTypeStr() {
        return userTypeStr;
    }

    public void setUserTypeStr(String userTypeStr) {
        this.userTypeStr = userTypeStr;
    }

    public Long getCutDate() {
        return cutDate;
    }

    public void setCutDate(Long cutDate) {
        this.cutDate = cutDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public Double getCutMoney() {
        return cutMoney;
    }

    public void setCutMoney(Double cutMoney) {
        this.cutMoney = cutMoney;
    }
}
