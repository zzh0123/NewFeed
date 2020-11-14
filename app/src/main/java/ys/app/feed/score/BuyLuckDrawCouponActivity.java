package ys.app.feed.score;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

public class BuyLuckDrawCouponActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    private String userId; // 用户id

    private TextView tv_buy_one; // 购买抽奖券一张
    private TextView tv_buy_ten; // 购买抽奖券十张

    // 购买抽奖券
    private Integer num = 1;
    private String url_buyLuckCoupon; // url
    private HashMap<String, String> paramsMap_buyLuckCoupon = new HashMap<String, String>();

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_luck_draw_coupon);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        intent = getIntent();
        // 用户id
        userId = (String) SPUtils.get(BuyLuckDrawCouponActivity.this, "userId", "");

        initView();
    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        tv_buy_one = (TextView) findViewById(R.id.tv_buy_one);
        tv_buy_ten = (TextView) findViewById(R.id.tv_buy_ten);
        tv_buy_one.setOnClickListener(this);
        tv_buy_ten.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_buy_one: // 购买抽奖券一张
                num = 1;
                buyLuckCoupon();
                break;
            case R.id.tv_buy_ten: // 购买抽奖券十张
                num = 10;
                buyLuckCoupon();
                break;
            case R.id.ll_back: // 返回
                setResult(1, intent);
                finish();
                break;
            default:
                break;
        }
    }

    // 购买抽奖券
    private void buyLuckCoupon(){
        paramsMap_buyLuckCoupon.put("num", num + "");
        paramsMap_buyLuckCoupon.put("userId", userId);
        url_buyLuckCoupon = Constants.baseUrl + Constants.url_buyLuckCoupon;
        Okhttp3Utils.getAsycRequest(url_buyLuckCoupon, paramsMap_buyLuckCoupon, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(BuyLuckDrawCouponActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(BuyLuckDrawCouponActivity.this, "购买成功！");
                        setResult(1, intent);
                        finish();
                    } else {
                        ToastUtils.showShort(BuyLuckDrawCouponActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(BuyLuckDrawCouponActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(1, intent);
        finish();
    }
}
