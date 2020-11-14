package ys.app.feed.bean;

import java.io.Serializable;

/**
 *  普通订单状态item
 */
public class NormalOrderStatusItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String status_name; //
    private String status_code; //
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }
}
