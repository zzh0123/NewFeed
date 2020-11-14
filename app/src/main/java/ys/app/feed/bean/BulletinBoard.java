package ys.app.feed.bean;

import java.io.Serializable;

public class BulletinBoard implements Serializable {
    private static final long serialVersionUID = 1L;

    private String createDate; //  创建时间
    private String message; //  积分说明
    private Integer score; //  积分数量

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
