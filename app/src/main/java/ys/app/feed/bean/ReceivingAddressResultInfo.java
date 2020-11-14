package ys.app.feed.bean;

import java.util.List;

public class ReceivingAddressResultInfo extends ResultInfo {

    private List<ReceivingAddress> receivingAddress_list; // 地址列表

    public List<ReceivingAddress> getReceivingAddress_list() {
        return receivingAddress_list;
    }

    public void setReceivingAddress_list(List<ReceivingAddress> receivingAddress_list) {
        this.receivingAddress_list = receivingAddress_list;
    }
}
