package ys.app.feed.discount;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.util.HashMap;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.bargain.ZeroBargainActivity;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.coupon.CouponActivity;
import ys.app.feed.goods.GoodsDetailActivity;
import ys.app.feed.invite.InviteActivity;
import ys.app.feed.order.MyOrderActivity;
import ys.app.feed.score.BuyScoreDiscountActivity;
import ys.app.feed.score.LuckDrawActivity;
import ys.app.feed.score.ScoreActivity;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.vip.VipActivity;
import ys.app.feed.widget.CircleImageView;
import ys.app.feed.widget.x5.X5WebView;

public class DiscountActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_back; // 返回

    private TextView tv_sign_in;

    private CircleImageView iv_head;
    private TextView tv_vip_type;
    private LinearLayout ll_upgrade; // 升级会员
    private TextView tv_vip_date;  // 您的会员--到期

    private LinearLayout ll_score;
    private TextView tv_score;
    private LinearLayout ll_coupon;
    private TextView tv_coupon;

    private TextView tv_to_bargain; // 去砍价
    private TextView tv_score_luck_draw; // 去抽奖

    private TextView tv_show_order; // 点击晒单


    // 获取用户信息
    private String url_getUserInfo; // 获取用户信息接口url
    private String userId; // 用户id
    private HashMap<String, String> paramsMap_get_getUserInfo = new HashMap<String, String>();

    // 签到
    private String url_signIn; // 获取地址接口url
    private HashMap<String, String> paramsMap_signIn = new HashMap<String, String>();

    // x5
    private X5WebView x5_webview;
    private HashMap<String, String> paramsMap_share = new HashMap<String, String>();

    private LinearLayout ll_to_bargain, ll_score_luck_draw;
    private LinearLayout ll_invite, ll_vip_surprise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.TRANSLUCENT); // 加载网页视频避免闪屏和透明
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }

        setContentView(R.layout.activity_discount);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        // 用户id
        userId = (String) SPUtils.get(DiscountActivity.this, "userId", "");
        initView();

        getUserInfo();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        tv_sign_in = (TextView) findViewById(R.id.tv_sign_in);
        ll_to_bargain = (LinearLayout) findViewById(R.id.ll_to_bargain);
        ll_score_luck_draw = (LinearLayout) findViewById(R.id.ll_score_luck_draw);

        tv_sign_in.setOnClickListener(this);
        ll_to_bargain.setOnClickListener(this);
        ll_score_luck_draw.setOnClickListener(this);

        iv_head = (CircleImageView) findViewById(R.id.iv_head);
        tv_vip_type = (TextView) findViewById(R.id.tv_vip_type);
        ll_upgrade = (LinearLayout) findViewById(R.id.ll_upgrade); // 升级会员
        ll_upgrade.setOnClickListener(this);
        tv_vip_date = (TextView) findViewById(R.id.tv_vip_date);

        ll_score = (LinearLayout) findViewById(R.id.ll_score);
        ll_score.setOnClickListener(this);
        tv_score = (TextView) findViewById(R.id.tv_score);
        ll_coupon = (LinearLayout) findViewById(R.id.ll_coupon);
        ll_coupon.setOnClickListener(this);
        tv_coupon = (TextView) findViewById(R.id.tv_coupon);

        tv_to_bargain = (TextView) findViewById(R.id.tv_to_bargain);
        tv_to_bargain.setOnClickListener(this);
        tv_score_luck_draw = (TextView) findViewById(R.id.tv_score_luck_draw);
        tv_score_luck_draw.setOnClickListener(this);

        tv_show_order = (TextView) findViewById(R.id.tv_show_order);
        tv_show_order.setOnClickListener(this);

        // x5
        x5_webview = (X5WebView) findViewById(R.id.x5_webview);

        paramsMap_share.put("userId", userId);
        String url = Okhttp3Utils.build_get_url(Constants.url_discount, paramsMap_share);

        x5_webview.loadUrl(url);

        ll_invite = (LinearLayout) findViewById(R.id.ll_invite);
        ll_invite.setOnClickListener(this);
        ll_vip_surprise = (LinearLayout) findViewById(R.id.ll_vip_surprise);
        ll_vip_surprise.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sign_in: // 签到
                signIn();
                break;
            case R.id.ll_score: // 积分
                Intent intent4 = new Intent(DiscountActivity.this, ScoreActivity.class);
                startActivity(intent4);
                break;
            case R.id.ll_coupon: // 优惠券
                Intent intent5 = new Intent(DiscountActivity.this, CouponActivity.class);
                startActivity(intent5);
                break;
            case R.id.ll_upgrade: // 升级会员
                Intent intent = new Intent(DiscountActivity.this, VipActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_to_bargain: // 去砍价
                Intent intent1 = new Intent(DiscountActivity.this, ZeroBargainActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_score_luck_draw: // 积分抽奖
                Intent intent2 = new Intent(DiscountActivity.this, LuckDrawActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_show_order: // 点击晒单
                Intent intent3 = new Intent(DiscountActivity.this, MyOrderActivity.class);
                startActivity(intent3);
                break;
            case R.id.ll_invite: // 邀请好友
                Intent intent6 = new Intent(DiscountActivity.this, InviteActivity.class);
                startActivity(intent6);
                break;
            case R.id.ll_vip_surprise: // 会员惊喜
                Intent intent7 = new Intent(DiscountActivity.this, GiftPackageActivity.class);
                startActivity(intent7);
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    // 签到
    private void signIn() {
        paramsMap_signIn.put("userId", userId);
        url_signIn = Constants.baseUrl + Constants.url_signIn;
        Okhttp3Utils.getAsycRequest(url_signIn, paramsMap_signIn, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(DiscountActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(DiscountActivity.this, "签到成功！");
                    } else {
                        //                        ToastUtils.showShort(getActivity(), resultInfo.getMessage());
                        LogUtils.i("signIn", "--signIn--" + resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(DiscountActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 获取用户首页信息
    private void getUserInfo() {
        paramsMap_get_getUserInfo.put("userId", userId);
        url_getUserInfo = Constants.baseUrl + Constants.url_getUserInfo;
        Okhttp3Utils.getAsycRequest(url_getUserInfo, paramsMap_get_getUserInfo, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(DiscountActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        String score = data_jsonObject.getString("score");
                        String couponCount = data_jsonObject.getString("couponCount");

                        String name = data_jsonObject.getString("name");
                        String userType = data_jsonObject.getString("userType");
                        String headPortrait = data_jsonObject.getString("headPortrait");
                        String vipEndDate = data_jsonObject.getString("vipEndDate");
                        String userId = data_jsonObject.getString("userId");
                        String userTypeStr = data_jsonObject.getString("userTypeStr");

                        Glide.with(DiscountActivity.this).load(headPortrait).error(R.mipmap.load_fail).into(iv_head);
                        if ("012001".equals(userType)) { // 非会员
                            tv_vip_date.setVisibility(View.GONE);
                        } else {
                            tv_vip_date.setText("您的会员 " + vipEndDate + " 到期");
                        }
                        tv_vip_type.setText(userTypeStr);
//                        tv_user_name.setText(name);
                        tv_score.setText(score);
                        tv_coupon.setText(couponCount);

                    } else {
                        ToastUtils.showShort(DiscountActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(DiscountActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (x5_webview != null && x5_webview.canGoBack()) {
                x5_webview.goBack();
                return true;
            } else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || x5_webview == null || intent.getData() == null)
            return;
        x5_webview.loadUrl(intent.getData().toString());
    }

    /**
     * Native JS 接口
     */

    @SuppressLint("JavascriptInterface")
    public class JavascriptInterface {

        /**
         * 构造方法
         */

        private Context context;
        public JavascriptInterface(Context context) {
            this.context = context;
        }

        /**
         * JS 调用 Android
         */

        @android.webkit.JavascriptInterface
        public String getUserId() {
            Toast.makeText(context, "userId为：" + userId, Toast.LENGTH_LONG).show();
           return  userId;
        }

    }

    @Override
    protected void onDestroy() {
        if (x5_webview != null)
            x5_webview.destroy();
        super.onDestroy();
    }
}
