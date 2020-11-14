package ys.app.feed.bean;

import java.io.Serializable;
import java.util.List;

public class PromotionItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String content; //

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
