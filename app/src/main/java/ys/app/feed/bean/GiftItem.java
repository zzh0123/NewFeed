package ys.app.feed.bean;

import java.io.Serializable;

/**
 *  抽奖item
 */
public class GiftItem implements Serializable {
    private static final long serialVersionUID = 1L;


    private String configCode; // 物品code
    private String configName; // 物品名
    private String configRemark; // 概率

    private String configValue; //
    private Integer orderBy; //
    private String paraCode; //

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigRemark() {
        return configRemark;
    }

    public void setConfigRemark(String configRemark) {
        this.configRemark = configRemark;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public String getParaCode() {
        return paraCode;
    }

    public void setParaCode(String paraCode) {
        this.paraCode = paraCode;
    }
}
