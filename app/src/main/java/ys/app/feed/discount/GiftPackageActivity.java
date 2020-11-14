package ys.app.feed.discount;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.OrderAdapter;
import ys.app.feed.R;
import ys.app.feed.adapter.BuyScoreAdapter;
import ys.app.feed.adapter.ReceivingAddressAdapter;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.BuyScoreItem;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.order.OrderPaymentActivity;
import ys.app.feed.orderdetail.ScoreExchangeOrderDetailActivity;
import ys.app.feed.score.BuyScoreDiscountActivity;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.vip.VipActivity;

/**
 * 充值增值/会员惊喜
 */
public class GiftPackageActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_back; // 返回

    private String userId; // 用户id

    // 购买积分列表
    private String url_getBuyScoreData; // url
    private HashMap<String, String> paramsMap_getBuyScoreData = new HashMap<String, String>();
    private ArrayList<BuyScoreItem> list_buyScoreItem = new ArrayList<BuyScoreItem>();
    private XRecyclerView mRecyclerView;
    private BuyScoreAdapter adapter_buyScoreItem;

    // 创建购买积分订单
    private String url_createBuyScoreOrder; // url
    private HashMap<String, String> paramsMap_createBuyScoreOrder = new HashMap<String, String>();
    private String orderNo;
    private String orderType = "购买积分";

    private Double buyScore; // 购买积分
    private Double money; // 支付金额

    private String giftCoupon; // 获取列表接口的coupon字段,返回什么传什么

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_package);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        // 用户id
        userId = (String) SPUtils.get(GiftPackageActivity.this, "userId", "");

        initView();
        getBuyScoreData();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        // 购买积分列表
        mRecyclerView = (XRecyclerView) this.findViewById(R.id.recyclerview_buy_score);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnabled(false);

        adapter_buyScoreItem = new BuyScoreAdapter(this, list_buyScoreItem);
        adapter_buyScoreItem.setClickCallBack(new BuyScoreAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                buyScore = list_buyScoreItem.get(pos).getScore();
                money = list_buyScoreItem.get(pos).getMoney();
                giftCoupon = list_buyScoreItem.get(pos).getCoupon();
                createBuyScoreOrder();
            }
        });
        mRecyclerView.setAdapter(adapter_buyScoreItem);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
            default:
                break;
        }
    }

    // 购买积分列表
    private void getBuyScoreData() {
        url_getBuyScoreData = Constants.baseUrl + Constants.url_getBuyScoreData;
        Okhttp3Utils.getAsycRequest(url_getBuyScoreData, paramsMap_getBuyScoreData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(GiftPackageActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONArray data_jsonArray = (JSONArray) response_jsonObject.getJSONArray("data");
                        String data_json_str = JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                        List<BuyScoreItem> list_data = JSONObject.parseArray(data_json_str, BuyScoreItem.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0) {
                            list_buyScoreItem.addAll(list_data);
                            adapter_buyScoreItem.notifyDataSetChanged();
                        }
                    } else {
                        ToastUtils.showShort(GiftPackageActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(GiftPackageActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 创建购买积分订单
    private void createBuyScoreOrder() {
        paramsMap_createBuyScoreOrder.put("buyScore", buyScore + "");
        paramsMap_createBuyScoreOrder.put("giftCoupon", giftCoupon);
        paramsMap_createBuyScoreOrder.put("money", money + "");
        paramsMap_createBuyScoreOrder.put("userId", userId);

        url_createBuyScoreOrder = Constants.baseUrl + Constants.url_createBuyScoreOrder;
        Okhttp3Utils.getAsycRequest(url_createBuyScoreOrder, paramsMap_createBuyScoreOrder, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(GiftPackageActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    orderNo = str_response;
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        Double money = data_jsonObject.getDouble("money");
                        orderNo = data_jsonObject.getString("orderNo");
                        LogUtils.i("--money--", "--money--" + money);
                        Intent intent = new Intent(GiftPackageActivity.this, OrderPaymentActivity.class);
                        intent.putExtra("money", money);
                        intent.putExtra("orderNo", orderNo);
                        intent.putExtra("orderType", orderType);
                        startActivity(intent);
//                        alipay();
//                        wxpay();
                    } else {
                        ToastUtils.showShort(GiftPackageActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(GiftPackageActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }
}
