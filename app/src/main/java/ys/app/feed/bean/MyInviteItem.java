package ys.app.feed.bean;

import java.io.Serializable;

public class MyInviteItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String beInvitedPhone; // 被邀请手机号
    private Long createDate; // 邀请时间
    private Integer id; //
    private String inviteUserId; //
    private Integer state; // 0.未注册|1.已注册
    private Long updateDate; //

    public String getBeInvitedPhone() {
        return beInvitedPhone;
    }

    public void setBeInvitedPhone(String beInvitedPhone) {
        this.beInvitedPhone = beInvitedPhone;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInviteUserId() {
        return inviteUserId;
    }

    public void setInviteUserId(String inviteUserId) {
        this.inviteUserId = inviteUserId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }
}
