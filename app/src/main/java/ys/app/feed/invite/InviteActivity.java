package ys.app.feed.invite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.shaohui.bottomdialog.BottomDialog;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.PromotionAdapter;
import ys.app.feed.adapter.ReceivingAddressAdapter;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.login.LoginActivity;
import ys.app.feed.my.EditMyInfoActivity;
import ys.app.feed.my.EditUserNameActivity;
import ys.app.feed.orderdetail.HalfPriceOrderDetailActivity;
import ys.app.feed.start.SplashActivity;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.utils.WRKShareUtil;

public class InviteActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回
    private TextView tv_share;
    private ImageView iv_QR_code;

    // 获取邀请方式
    private String url_getInviteMode; // url
    private String userId; // 用户名
    private HashMap<String, String> paramsMap_getInviteMode = new HashMap<String, String>();
    private String qrCode_url; // 二维码图片url
    private String url; // 分享的url

    private String userName; // 用户名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        // 用户名
        userName = (String) SPUtils.get(InviteActivity.this, "userName", "");
        initView();
        getInviteMode();
    }

    private void initView() {
        // 用户id
        userId = (String) SPUtils.get(InviteActivity.this, "userId", "");

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        tv_share = (TextView) findViewById(R.id.tv_share);
        tv_share.setOnClickListener(this);
        iv_QR_code = (ImageView) findViewById(R.id.iv_QR_code);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share: // 点击分享按钮邀请
                if (TextUtils.isEmpty(url)){
                    ToastUtils.showShort(InviteActivity.this, "分享链接为空！");
                    return;
                }
                // 分享
                BottomDialog.create(getSupportFragmentManager())
                        .setViewListener(new BottomDialog.ViewListener() {
                            @Override
                            public void bindView(View v) {
                                initViewBottom(v);
                            }
                        })
                        .setLayoutRes(R.layout.layout_share)
                        .setDimAmount(0.9f)
                        .setTag("BottomDialog")
                        .show();
                break;
            case R.id.rl_wx_chat: // 微信聊天
                String title = userName + "邀请您下载使用慧合app,慧合配料购料平台，高档饲料最低价";
                String title_sub = "";
                WRKShareUtil.getInstance(this).shareUrlToWx(url, title,
                        title_sub, "",
                        SendMessageToWX.Req.WXSceneSession);
                break;
            case R.id.rl_wx_friend_circle: // 微信朋友圈
                String title1 = userName + "邀请您下载使用慧合app,慧合配料购料平台，高档饲料最低价";
                String title_sub1 = "";
                WRKShareUtil.getInstance(this).shareUrlToWx(url, title1,
                        title_sub1, "",
                        SendMessageToWX.Req.WXSceneTimeline);
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    private void initViewBottom(final View view) {
        RelativeLayout rl_wx_chat = (RelativeLayout) view.findViewById(R.id.rl_wx_chat); // 微信聊天
        RelativeLayout rl_wx_friend_circle = (RelativeLayout) view.findViewById(R.id.rl_wx_friend_circle); // 微信朋友圈
        rl_wx_chat.setOnClickListener(this);
        rl_wx_friend_circle.setOnClickListener(this);
    }

    // 获取邀请方式
    private void getInviteMode(){
        paramsMap_getInviteMode.put("userId", userId);
        url_getInviteMode = Constants.baseUrl + Constants.url_getInviteMode;
        Okhttp3Utils.getAsycRequest(url_getInviteMode, paramsMap_getInviteMode, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(InviteActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        qrCode_url = data_jsonObject.getString("qrCode");
                        url = data_jsonObject.getString("url");
                        Glide.with(InviteActivity.this).load(qrCode_url).error(R.mipmap.load_fail).into(iv_QR_code);
                    } else {
                        ToastUtils.showShort(InviteActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(InviteActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

}
