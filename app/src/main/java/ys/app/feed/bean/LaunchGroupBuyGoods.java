package ys.app.feed.bean;

import java.io.Serializable;

public class LaunchGroupBuyGoods implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double groupPrice; // 130.0,
    private String name; // "慧合30%肉牛育肥期浓缩饲料3028"
    private String commodityId; // "C015"
    private String listImg; // "http://www.huihemuchang.com/nfs/list_commodity/list_C015.png"
    private String spec; // "40kg/包"
    private Double groupMemberPrice; // 118.0

    public Double getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(Double groupPrice) {
        this.groupPrice = groupPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getListImg() {
        return listImg;
    }

    public void setListImg(String listImg) {
        this.listImg = listImg;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Double getGroupMemberPrice() {
        return groupMemberPrice;
    }

    public void setGroupMemberPrice(Double groupMemberPrice) {
        this.groupMemberPrice = groupMemberPrice;
    }
}
