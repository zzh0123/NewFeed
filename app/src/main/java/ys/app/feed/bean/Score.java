package ys.app.feed.bean;

import java.io.Serializable;

public class Score implements Serializable {
    private static final long serialVersionUID = 1L;

    private String createDate; //  创建时间
    private String message; //  积分说明
    private Double score; //  积分数量

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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
