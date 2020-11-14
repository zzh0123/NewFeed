package ys.app.feed.bean;

import java.io.Serializable;

/**
 *  会员类型item
 */
public class VipTypeItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name; // 体验会员
    private String code; //
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

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
}
