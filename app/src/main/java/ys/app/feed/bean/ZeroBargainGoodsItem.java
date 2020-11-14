package ys.app.feed.bean;

import java.io.Serializable;

public class ZeroBargainGoodsItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double price; // 130.0
    private String name; //
    private String commodityId; //

    private String listImg; //
    private String spec; //
    private Double bargainPrice; // 0.0

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

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Double getBargainPrice() {
        return bargainPrice;
    }

    public void setBargainPrice(Double bargainPrice) {
        this.bargainPrice = bargainPrice;
    }
}
