package ys.app.feed.bean;

import java.util.List;

public class ScoreResultInfo extends ResultInfo {

    private List<Score> list_score; // 积分列表
    private Integer totalScore; //  总积分

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public List<Score> getList_score() {
        return list_score;
    }

    public void setList_score(List<Score> list_score) {
        this.list_score = list_score;
    }
}
