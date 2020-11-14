package ys.app.feed.bean;

import java.io.Serializable;

public class RecommendGoodsItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String imgUrl; //
    private String name; //
    private String commodityId; //

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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
}
