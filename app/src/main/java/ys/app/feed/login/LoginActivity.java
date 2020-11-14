package ys.app.feed.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

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
import ys.app.feed.utils.WRKShareUtil;
import ys.app.feed.widget.ClearEditText;

/**
 * 登录
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_verification_code_login, ll_password_login, ll_wexin_login; // 验证码登录， 密码登录, 微信登录
    private ClearEditText et_phone_num, et_password; // 输入手机号, 输入密码
    private Button bt_get_verification_code, bt_login; // 获取验证码， 登录
    private TextView tv_password_login, tv_verification_code_login, tv_forget_set_password; // 密码登录, 验证码登录, 忘记/设置密码
    private String url_get_verification_code, type, phone; // 获取验证码接口url, 0.注册/登录 | 1.忘记密码类型, 手机号
    private HashMap<String, String> paramsMap_get_verification_code = new HashMap<String, String>();

    private String userId = ""; // 用户Id

    private String url_login, password; // 密码登录接口url, 密码, 手机号phone
    private HashMap<String, String> paramsMap_login = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView() {
        ll_verification_code_login = (LinearLayout) findViewById(R.id.ll_verification_code_login); // 验证码登录
        ll_password_login = (LinearLayout) findViewById(R.id.ll_password_login); // 密码登录
        ll_wexin_login = (LinearLayout) findViewById(R.id.ll_wexin_login); // 微信登录
        et_phone_num = (ClearEditText) findViewById(R.id.et_phone_num); // 输入手机号
        et_password = (ClearEditText) findViewById(R.id.et_password); // 输入密码
        bt_get_verification_code = (Button) findViewById(R.id.bt_get_verification_code); // 获取验证码
        bt_login = (Button) findViewById(R.id.bt_login); // bt_login
        tv_password_login = (TextView) findViewById(R.id.tv_password_login); // 密码登录
        tv_verification_code_login = (TextView) findViewById(R.id.tv_verification_code_login); // 验证码登录
        tv_forget_set_password = (TextView) findViewById(R.id.tv_forget_set_password); // 忘记/设置密码

        bt_get_verification_code.setClickable(false);
        bt_get_verification_code.setOnClickListener(this);
        tv_password_login.setOnClickListener(this);
        tv_verification_code_login.setOnClickListener(this);
        ll_wexin_login.setOnClickListener(this);
        tv_forget_set_password.setOnClickListener(this);
        bt_login.setOnClickListener(this);

        et_phone_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_phone_num.getText().toString().trim().length() == 11) {
                    bt_get_verification_code.setClickable(true);
                    bt_get_verification_code.setBackgroundResource(R.drawable.bt_rectangle_shape_green);
                    bt_login.setClickable(true);
                    bt_login.setBackgroundResource(R.drawable.bt_rectangle_shape_green);
                } else {
                    bt_get_verification_code.setClickable(false);
                    bt_get_verification_code.setBackgroundResource(R.drawable.bt_rectangle_shape_grey);
                    bt_login.setClickable(false);
                    bt_login.setBackgroundResource(R.drawable.bt_rectangle_shape_grey);
                }
            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_phone_num.getText().toString().trim().length() != 11 || et_password.getText().toString().trim().length() == 0) {
                    bt_login.setClickable(false);
                    bt_login.setBackgroundResource(R.drawable.bt_rectangle_shape_grey);
                } else {
                    bt_login.setClickable(true);
                    bt_login.setBackgroundResource(R.drawable.bt_rectangle_shape_green);
                }
            }
        });

        // 用户id
        userId = (String) SPUtils.get(LoginActivity.this, "userId", "");
        if (!TextUtils.isEmpty(userId)){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_get_verification_code: // 获取验证码(注册/登录)
                phone = et_phone_num.getText().toString().trim();
                type = "0";
                paramsMap_get_verification_code.put("phone", phone);
                paramsMap_get_verification_code.put("type", type);
                getVerificationCode();
                break;

            case R.id.tv_password_login: // 密码登录
                ll_password_login.setVisibility(View.VISIBLE);
                ll_verification_code_login.setVisibility(View.GONE);
                break;
            case R.id.tv_verification_code_login: // 验证码登录
                et_password.setText("");
                ll_password_login.setVisibility(View.GONE);
                ll_verification_code_login.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_wexin_login: // 微信登录
                WRKShareUtil.getInstance(this).wxLogin();
                break;
            case R.id.bt_login: // 登录
                login();
                break;
            case R.id.tv_forget_set_password: // 忘记/设置密码
                Intent intent = new Intent(LoginActivity.this, GetbackPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

    // 获取短信验证码
    private void getVerificationCode() {
        url_get_verification_code = Constants.baseUrl + Constants.url_get_verification_code;
        Okhttp3Utils.getAsycRequest(url_get_verification_code, paramsMap_get_verification_code, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(LoginActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_getCode = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_getCode, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(LoginActivity.this, "短信发送成功！");
                        Intent intent = new Intent(LoginActivity.this, VerificationCodeActivity.class);
                        intent.putExtra("phone_num", et_phone_num.getText().toString().trim());
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.showShort(LoginActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(LoginActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    private void setData(){
        phone = et_phone_num.getText().toString().trim();
        type = "1";
        password = et_password.getText().toString().trim();
        paramsMap_login.put("password", password);
        paramsMap_login.put("phone", phone);
    }
    // 密码登录code, password, phone
    private void login(){
        bt_login.setClickable(false);
        url_login = Constants.baseUrl + Constants.url_login;
        setData();
        Okhttp3Utils.postAsyRequest_Form(url_login, paramsMap_login, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(LoginActivity.this, "服务器未响应！");
                bt_login.setClickable(true);
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_getCode = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_getCode, ResultInfo.class);
//                    JSONObject jsonObject = JSON.parseObject(str_getCode);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(LoginActivity.this, "登录成功！");
                        bt_login.setClickable(false);
                        JSONObject data = JSONObject.parseObject(resultInfo.getData().toString());
                        String userId = data.getString("userId");
                        Integer isGiftPackage = data.getInteger("isGiftPackage");
                        Integer intention = data.getInteger("intention"); // 是否填写意向产品 0.已选择意向产品 | 1.未选择意向产品
                        Log.i("---userId", "---userId" + userId);
                        SPUtils.put(LoginActivity.this, "userId", userId);
                        SPUtils.put(LoginActivity.this, "isGiftPackage", isGiftPackage);
                        if (intention == 0){ // 0.已选择意向产品
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, IntentionActivity.class);
                            startActivity(intent);
                        }

                        finish();
                    } else {
                        ToastUtils.showShort(LoginActivity.this, resultInfo.getMessage());
                        bt_login.setClickable(true);
                    }
                } else {
                    ToastUtils.showShort(LoginActivity.this, "服务器返回数据为空！");
                    bt_login.setClickable(true);
                }
            }
        });
    }

    // 微信登录
    private void weixinLogin() {
    }

}
