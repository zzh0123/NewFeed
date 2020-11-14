package ys.app.feed.bean;

public class LoginResultInfo extends ResultInfo {

    private Integer isGiftPackage; // 是否领取注册红包
    private Integer intention; // 是否填写意向产品
    private String userId; // 用户Id

    public Integer getIsGiftPackage() {
        return isGiftPackage;
    }

    public void setIsGiftPackage(Integer isGiftPackage) {
        this.isGiftPackage = isGiftPackage;
    }

    public Integer getIntention() {
        return intention;
    }

    public void setIntention(Integer intention) {
        this.intention = intention;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
