package ys.app.feed.order;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shizhefei.fragment.LazyFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.OrderAdapter;
import ys.app.feed.R;
import ys.app.feed.adapter.NormalOrderStatusAdapter;
import ys.app.feed.bean.NormalOrderStatusItem;
import ys.app.feed.bean.Order;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.orderdetail.GroupBuyOrderDetailActivity;
import ys.app.feed.orderdetail.NormalOrderDetailActivity;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

/**
 * 团购订单fragment
 */

public class GroupBuyOrderFragment extends LazyFragment {

    // 订单状态标题（左侧）
    private RecyclerView recyclerView_order_status;
    private ArrayList<NormalOrderStatusItem> list_orderStatusTitle = new ArrayList<NormalOrderStatusItem>();
    private NormalOrderStatusAdapter adapter_normalOrderStatus;
    private String status;
    private String type = "2";

    // 获取团购订单列表
    private String url_getOrderData; // url
    private String userId; // 用户名
    private HashMap<String, String> paramsMap_getOrderData = new HashMap<String, String>();
    private XRecyclerView mRecyclerView;
    private OrderAdapter adapter_order;
    private ArrayList<Order> list_order = new ArrayList<Order>();

    private Handler handler;

    public GroupBuyOrderFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(android.os.Message msg) {
//                initView();
            }
        };

        setContentView(R.layout.fragment_order_group_buy);

        // 用户id
        userId = (String) SPUtils.get(getActivity(), "userId", "");
        initTitleView();
        initView();
        getOrderData();
    }

    private void initTitleView() {
        // 订单状态标题（左侧）
        recyclerView_order_status = (RecyclerView) findViewById(R.id.recyclerView_order_status);
        recyclerView_order_status.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (list_orderStatusTitle.size() == 0){
            setTitleList();
        }
        status = list_orderStatusTitle.get(0).getStatus_code();
        list_orderStatusTitle.get(0).setSelected(true);

        adapter_normalOrderStatus = new NormalOrderStatusAdapter(getActivity(), list_orderStatusTitle);
        adapter_normalOrderStatus.setClickCallBack(new NormalOrderStatusAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                status = list_orderStatusTitle.get(pos).getStatus_code();
                for (int i=0; i<list_orderStatusTitle.size(); i++){
                    list_orderStatusTitle.get(i).setSelected(false);
                }
                list_orderStatusTitle.get(pos).setSelected(true);
                adapter_normalOrderStatus.notifyDataSetChanged();
                // 获取普通订单列表
                getOrderData();
            }
        });
        recyclerView_order_status.setAdapter(adapter_normalOrderStatus);
    }

    private void initView() {
        // 团购订单列表
        mRecyclerView = (XRecyclerView) this.findViewById(R.id.recyclerview_order_group_buy);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnabled(false);

        adapter_order = new OrderAdapter(getActivity(), list_order);
        adapter_order.setClickCallBack(new OrderAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                Integer orderId = list_order.get(pos).getOrderId();
                Intent intent = new Intent(getActivity(), GroupBuyOrderDetailActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("status", status);
                startActivityForResult(intent, 1);
            }
        });
        mRecyclerView.setAdapter(adapter_order);
    }

    private void setTitleList() {
        // 团购订单表
       // 订单状态 0.待支付 | 1.拼团中 | 2.配货中 | 3.待签收 | 4.已完成  | 5.待评价| 6.已关闭 | 7.已删除
        NormalOrderStatusItem normalOrderStatusItem = new NormalOrderStatusItem();
        normalOrderStatusItem.setStatus_code("0");
        normalOrderStatusItem.setStatus_name("待支付");
        normalOrderStatusItem.setSelected(false);
        list_orderStatusTitle.add(normalOrderStatusItem);

        NormalOrderStatusItem normalOrderStatusItem1 = new NormalOrderStatusItem();
        normalOrderStatusItem1.setStatus_code("1");
        normalOrderStatusItem1.setStatus_name("拼团中");
        normalOrderStatusItem1.setSelected(false);
        list_orderStatusTitle.add(normalOrderStatusItem1);

        NormalOrderStatusItem normalOrderStatusItem2 = new NormalOrderStatusItem();
        normalOrderStatusItem2.setStatus_code("2");
        normalOrderStatusItem2.setStatus_name("配货中");
        normalOrderStatusItem2.setSelected(false);
        list_orderStatusTitle.add(normalOrderStatusItem2);

        NormalOrderStatusItem normalOrderStatusItem3 = new NormalOrderStatusItem();
        normalOrderStatusItem3.setStatus_code("3");
        normalOrderStatusItem3.setStatus_name("待签收");
        normalOrderStatusItem3.setSelected(false);
        list_orderStatusTitle.add(normalOrderStatusItem3);

        NormalOrderStatusItem normalOrderStatusItem4 = new NormalOrderStatusItem();
        normalOrderStatusItem4.setStatus_code("4");
        normalOrderStatusItem4.setStatus_name("已完成");
        normalOrderStatusItem4.setSelected(false);
        list_orderStatusTitle.add(normalOrderStatusItem4);

//        NormalOrderStatusItem normalOrderStatusItem5 = new NormalOrderStatusItem();
//        normalOrderStatusItem5.setStatus_code("5");
//        normalOrderStatusItem5.setStatus_name("待评价");
//        normalOrderStatusItem5.setSelected(false);
//        list_orderStatusTitle.add(normalOrderStatusItem5);

//        NormalOrderStatusItem normalOrderStatusItem6 = new NormalOrderStatusItem();
//        normalOrderStatusItem6.setStatus_code("6");
//        normalOrderStatusItem6.setStatus_name("已关闭");
//        normalOrderStatusItem6.setSelected(false);
//        list_orderStatusTitle.add(normalOrderStatusItem6);
//
//        NormalOrderStatusItem normalOrderStatusItem7 = new NormalOrderStatusItem();
//        normalOrderStatusItem7.setStatus_code("7");
//        normalOrderStatusItem7.setStatus_name("已删除");
//        normalOrderStatusItem7.setSelected(false);
//        list_orderStatusTitle.add(normalOrderStatusItem7);
    }

    // 获取普通订单列表
    private void getOrderData(){
        list_order.clear();
        paramsMap_getOrderData.put("status", status);
        paramsMap_getOrderData.put("type", type);
        paramsMap_getOrderData.put("userId", userId);
        url_getOrderData = Constants.baseUrl + Constants.url_getOrderData;
        Okhttp3Utils.getAsycRequest(url_getOrderData, paramsMap_getOrderData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(getActivity(), "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONArray data_jsonArray = (JSONArray)response_jsonObject.getJSONArray("data");
                        String data_json_str =JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                        List<Order> list_data = JSONObject.parseArray(data_json_str, Order.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0){
                            list_order.addAll(list_data);

                        }
                        if (adapter_order == null){
                            adapter_order = new OrderAdapter(getActivity(), list_order);
                        }
                        adapter_order.notifyDataSetChanged();
                        //                            handler.sendEmptyMessageDelayed(1, 0000);
                    } else {
                        ToastUtils.showShort(getActivity(), resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "服务器返回数据为空！");
                }
            }
        });
    }

    @Override
    public void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == 1 && resultCode == 1) {
                //返回的数据
                getOrderData();
//                LogUtils.i("--走了--", "--走了--");
            }
        }
    }

}
