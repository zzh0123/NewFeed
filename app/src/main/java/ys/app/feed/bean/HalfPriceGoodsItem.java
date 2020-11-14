package ys.app.feed.bean;

import java.io.Serializable;

public class HalfPriceGoodsItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double price; // 176.0
    private String name; //
    private String commodityId; //
    private String listImg; //
    private Double halfPrice; // 88.0
    private String spec; //

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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

    public Double getHalfPrice() {
        return halfPrice;
    }

    public void setHalfPrice(Double halfPrice) {
        this.halfPrice = halfPrice;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }
}
