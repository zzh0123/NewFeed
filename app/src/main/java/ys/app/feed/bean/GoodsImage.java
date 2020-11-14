package ys.app.feed.bean;

import java.io.Serializable;
import java.util.List;

public class GoodsImage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String imgUrl; //
    private Integer type; //

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
