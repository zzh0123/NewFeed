package ys.app.feed.bean;

import java.io.Serializable;
import java.util.List;

public class MyCollectionItem implements Serializable {
    private static final long serialVersionUID = 1L;


    private String commodityId; // 商品id
    private String imgUrl; // 商品图片url
    private String name; // 商品名

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

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
}
