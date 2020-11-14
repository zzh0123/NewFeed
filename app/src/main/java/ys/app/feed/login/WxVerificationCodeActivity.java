package ys.app.feed.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import ys.app.feed.MainActivity;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.intention.IntentionActivity;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.CountDownTextView;
import ys.app.feed.widget.VerificationCodeEditText;

/**
 *  微信登录验证码
 */
public class WxVerificationCodeActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    private CountDownTextView tv_count_down; // 倒计时
    private VerificationCodeEditText et_verification_code; // 验证码输入框

    private String url_wxLogin; // url
    private HashMap<String, String> paramsMap_wxLogin = new HashMap<String, String>();
    private String code; // 验证码
    private String headPortrait; // 头像
    private String name; // 微信名
    private String openId; //
    private String phone; //手机号


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_verification_code);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        headPortrait = getIntent().getExtras().getString("headPortrait");
        name = getIntent().getExtras().getString("name");
        openId = getIntent().getExtras().getString("openId");
        phone = getIntent().getExtras().getString("phone_num");
        initView();
    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        tv_count_down = (CountDownTextView) findViewById(R.id.tv_count_down); // 倒计时
        et_verification_code = (VerificationCodeEditText) findViewById(R.id.et_verification_code); // 验证码输入框

        tv_count_down
                .setNormalText("获取验证码")
                .setCountDownText("", "秒后可重新获取验证码")
                .setCloseKeepCountDown(false)//关闭页面保持倒计时开关
                .setCountDownClickable(false)//倒计时期间点击事件是否生效开关
                .setShowFormatTime(false)//是否格式化时间
                .setIntervalUnit(TimeUnit.SECONDS)
                .setOnCountDownStartListener(new CountDownTextView.OnCountDownStartListener() {
                    @Override
                    public void onStart() {
//                        Toast.makeText(VerificationCodeActivity.this, "开始计时", Toast.LENGTH_SHORT).show();
                        tv_count_down.setTextColor(getResources().getColor(R.color.text_grey));
                    }
                })
                .setOnCountDownTickListener(new CountDownTextView.OnCountDownTickListener() {
                    @Override
                    public void onTick(long untilFinished) {
                        Log.e("------", "onTick: " + untilFinished);
                    }
                })
                .setOnCountDownFinishListener(new CountDownTextView.OnCountDownFinishListener() {
                    @Override
                    public void onFinish() {
//                        Toast.makeText(VerificationCodeActivity.this, "倒计时完毕", Toast.LENGTH_SHORT).show();
                        tv_count_down.setTextColor(getResources().getColor(R.color.text_green));
                    }
                })
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(VerificationCodeActivity.this, "短信已发送", Toast.LENGTH_SHORT).show();
                        tv_count_down.startCountDown(30);
                    }
                });
        tv_count_down.startCountDown(30);

        et_verification_code.setOnVerificationCodeChangedListener(new VerificationCodeEditText
                .OnVerificationCodeChangedListener() {

            @Override
            public void onVerCodeChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onInputCompleted(CharSequence s) {
//                ToastUtils.showShort(VerificationCodeActivity.this, "输入完了");
                login();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    private void setData(){
        code = et_verification_code.getText().toString().trim();
        paramsMap_wxLogin.put("code", code);
        paramsMap_wxLogin.put("headPortrait", headPortrait); // 头像
        paramsMap_wxLogin.put("name", name); // 微信名
        paramsMap_wxLogin.put("openId", openId);
        paramsMap_wxLogin.put("phone", phone);//手机号
    }

    // 验证码登录code, password, phone
    private void login(){
        url_wxLogin = Constants.baseUrl + Constants.url_wxLogin;
        setData();
        Okhttp3Utils.postAsyRequest_Form(url_wxLogin, paramsMap_wxLogin, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(WxVerificationCodeActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_getCode = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_getCode, ResultInfo.class);
//                    JSONObject jsonObject = JSON.parseObject(str_getCode);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(WxVerificationCodeActivity.this, "登录成功！");
                        JSONObject data = JSONObject.parseObject(resultInfo.getData().toString());
                        String userId = data.getString("userId");
                        Integer isGiftPackage = data.getInteger("isGiftPackage");
                        Integer intention = data.getInteger("intention"); // 是否填写意向产品 0.已选择意向产品 | 1.未选择意向产品
                        Log.i("---userId", "---userId" + userId);
                        SPUtils.put(WxVerificationCodeActivity.this, "userId", userId);
                        SPUtils.put(WxVerificationCodeActivity.this, "isGiftPackage", isGiftPackage);
                        if (intention == 0){ // 0.已选择意向产品
                            Intent intent = new Intent(WxVerificationCodeActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(WxVerificationCodeActivity.this, IntentionActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        ToastUtils.showShort(WxVerificationCodeActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(WxVerificationCodeActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }
}
