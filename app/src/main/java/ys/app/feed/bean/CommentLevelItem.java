package ys.app.feed.bean;

import java.io.Serializable;

public class CommentLevelItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name; //

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code; //

}
