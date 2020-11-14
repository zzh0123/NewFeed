package ys.app.feed.bean;

import java.io.Serializable;

/**
 *  拼团头像item
 */
public class GroupHeadItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name; // "慧合30%肉牛育肥期浓缩饲料3028"
    private String headImageUrl; // "C015"

    public GroupHeadItem(String name, String headImageUrl){
        this.name = name;
        this.headImageUrl = headImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }
}
