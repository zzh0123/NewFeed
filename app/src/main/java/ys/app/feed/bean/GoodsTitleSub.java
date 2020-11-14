package ys.app.feed.bean;

import java.io.Serializable;
import java.util.List;

public class GoodsTitleSub implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subCode; //
    private String subName; //

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }
}
