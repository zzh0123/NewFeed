package ys.app.feed.coupon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.CouponAdapter;
import ys.app.feed.adapter.CouponOrderSelectAdapter;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.Coupon;
import ys.app.feed.bean.CouponOrderSelectItem;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

public class OrderSelectCouponActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_back; // 返回

    private XRecyclerView recyclerview_coupon;

    // 按照类型获取用户未使用的优惠券
    private String url_getCouponByType; // 获取地址接口url
    private String userId; // 用户名,用户id
    private HashMap<String, String> paramsMap_getCouponByType = new HashMap<String, String>();
    private ArrayList<CouponOrderSelectItem> list_coupon = new ArrayList<CouponOrderSelectItem>();

    private CouponOrderSelectAdapter adapter_coupon;

    private Intent intent;
    private Integer cashCouponId; // 抵扣券id
    private Integer noSpellId; // 免拼券id
    private Integer halfCouponId; // 半价券id
    private Integer cutCouponId; // 砍价券id


    private String type; // 优惠券类型 半价券:018001, 砍价券:018002, 免拼券:018003, 代金券:018004

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_order_select);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        intent = getIntent();
        type = intent.getStringExtra("type");
        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        recyclerview_coupon = (XRecyclerView) findViewById(R.id.recyclerview_coupon);
        recyclerview_coupon.setPullRefreshEnabled(false);
        recyclerview_coupon.setLoadingMoreEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_coupon.setLayoutManager(layoutManager);

        adapter_coupon = new CouponOrderSelectAdapter(this, list_coupon, type);
        adapter_coupon.setClickCallBack(new CouponOrderSelectAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
//                ToastUtils.showShort(CouponActivity.this, "--" + pos);
                if (TextUtils.equals("018001", type)) { // 半价券
                    halfCouponId = list_coupon.get(pos).getCouponId();
                    intent.putExtra("halfCouponId", halfCouponId);
                } else if (TextUtils.equals("018002", type)) { // 砍价券
                    cutCouponId = list_coupon.get(pos).getCouponId();
                    intent.putExtra("cutCouponId", cutCouponId);
                } else if (TextUtils.equals("018003", type)) { // 免拼券
                    noSpellId = list_coupon.get(pos).getCouponId();
                    intent.putExtra("noSpellId", noSpellId);
                } else if (TextUtils.equals("018004", type)) { // 代金券
                    cashCouponId = list_coupon.get(pos).getCouponId();
                    Double replaceMoney = list_coupon.get(pos).getReplaceMoney();
                    intent.putExtra("cashCouponId", cashCouponId);
                    intent.putExtra("replaceMoney", replaceMoney);
                }
                setResult(1, intent);
                finish();
            }
        });

        recyclerview_coupon.setAdapter(adapter_coupon);

        // 用户id
        userId = (String) SPUtils.get(OrderSelectCouponActivity.this, "userId", "");
        getCouponByType();
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

    // 按照类型获取用户未使用的优惠券
    private void getCouponByType() {
        paramsMap_getCouponByType.put("type", type + "");
        paramsMap_getCouponByType.put("userId", userId);
        url_getCouponByType = Constants.baseUrl + Constants.url_getCouponByType;
        Okhttp3Utils.getAsycRequest(url_getCouponByType, paramsMap_getCouponByType, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(OrderSelectCouponActivity.this, "服务器未响应！");
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
                        List<CouponOrderSelectItem> list_data = JSONObject.parseArray(data_json_str, CouponOrderSelectItem.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0) {
                            list_coupon.addAll(list_data);
                            if (adapter_coupon == null) {
                                adapter_coupon = new CouponOrderSelectAdapter(OrderSelectCouponActivity.this, list_coupon, type);
                                adapter_coupon.setType(type);
                                recyclerview_coupon.setAdapter(adapter_coupon);
                            }
                            adapter_coupon.setType(type);
                            adapter_coupon.notifyDataSetChanged();
                        }
                    } else {
                        ToastUtils.showShort(OrderSelectCouponActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(OrderSelectCouponActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }
}
