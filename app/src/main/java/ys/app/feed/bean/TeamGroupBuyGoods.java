package ys.app.feed.bean;

import java.io.Serializable;

public class TeamGroupBuyGoods implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer surplusCount; // 5.0
    private Double groupPrice; // 130.0
    private String name; // "慧合30%肉牛育肥期浓缩饲料3028"

    private Integer id; // 72
    private String commodityId; // "C015"
    private String groupScale; // "5人团"
    private String totalCount; // "5"

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    private String groupNo; // "327731981313900544"
    private String listImg; // "http://www.huihemuchang.com/nfs/list_commodity/list_C015.png"
    private Double groupMemberPrice; // 118.0
    private Integer second; // 62706

    public Integer getSurplusCount() {
        return surplusCount;
    }

    public void setSurplusCount(Integer surplusCount) {
        this.surplusCount = surplusCount;
    }

    public Double getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(Double groupPrice) {
        this.groupPrice = groupPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getGroupScale() {
        return groupScale;
    }

    public void setGroupScale(String groupScale) {
        this.groupScale = groupScale;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getListImg() {
        return listImg;
    }

    public void setListImg(String listImg) {
        this.listImg = listImg;
    }

    public Double getGroupMemberPrice() {
        return groupMemberPrice;
    }

    public void setGroupMemberPrice(Double groupMemberPrice) {
        this.groupMemberPrice = groupMemberPrice;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }
}
