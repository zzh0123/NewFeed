package ys.app.feed.bean;

import java.io.Serializable;

public class SearchGoodsItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String groupPrice; //
    private String name; //
    private String commodityId; //
    private String listImg; //
    private Double groupMemberPrice; //

    public String getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(String groupPrice) {
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

    public Double getGroupMemberPrice() {
        return groupMemberPrice;
    }

    public void setGroupMemberPrice(Double groupMemberPrice) {
        this.groupMemberPrice = groupMemberPrice;
    }
}
