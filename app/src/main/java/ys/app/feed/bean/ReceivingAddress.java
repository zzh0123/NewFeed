package ys.app.feed.bean;

import java.io.Serializable;

public class ReceivingAddress implements Serializable {
    private static final long serialVersionUID = 1L;

    private String area; // 区县
    private String city; // 城市
    private String detailAddress; //  详细地址
    private String address_id ; // id
    private Integer isDefault; //  是否默认地址 number 0.不默认 | 1.默认地址
    private String name; //  名字
    private String phone; // 手机号
    private String province; // 省份

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

}
