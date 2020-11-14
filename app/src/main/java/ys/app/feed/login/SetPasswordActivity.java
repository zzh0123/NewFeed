package ys.app.feed.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.CountDownTextView;

/**
 * 设置/找回密码
 */
public class SetPasswordActivity extends Activity implements View.OnClickListener{

    private TextView tv_phone_num; // 电话号码
    private CountDownTextView tv_count_down; // 倒计时
    private EditText et_set_new_password, et_verification_code; // 设置新密码, 输入验证码
    private Button bt_submit; // 提交

    private String url_get_verification_code; // 获取验证码接口url
    private String type, phone;  // 类型,0.注册/登录 | 1.忘记密码, 手机号
    private HashMap<String, String> paramsMap_get_verification_code = new HashMap<String, String>();

    private String url_forgetPassword;  // 忘记密码接口url
    private String code, password;  // 验证码, 密码, 手机号 phone
    private HashMap<String, String> paramsMap_forgetPassword = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        phone = getIntent().getExtras().getString("phone_num");
        initView();
        getVerificationCode();
    }

    private void initView(){
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num); // 电话号码
        tv_phone_num.setText(" " + phone);
        tv_count_down = (CountDownTextView) findViewById(R.id.tv_count_down); // 倒计时
        et_set_new_password = (EditText) findViewById(R.id.et_set_new_password); // 设置新密码
        et_verification_code = (EditText) findViewById(R.id.et_verification_code); // 输入验证码
        bt_submit = (Button) findViewById(R.id.bt_submit); // 提交
        bt_submit.setOnClickListener(this);

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
//                        Toast.makeText(SetPasswordActivity.this, "开始计时", Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(SetPasswordActivity.this, "倒计时完毕", Toast.LENGTH_SHORT).show();
                        tv_count_down.setTextColor(getResources().getColor(R.color.text_green));
                    }
                })
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(SetPasswordActivity.this, "短信已发送", Toast.LENGTH_SHORT).show();
                        tv_count_down.startCountDown(30);
                        getVerificationCode();
                    }
                });
        tv_count_down.startCountDown(30);

        et_verification_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_set_new_password.getText().toString().trim().length() < 6 || et_verification_code.getText().toString().trim().length() != 6){
                    bt_submit.setClickable(false);
                    bt_submit.setBackgroundResource(R.drawable.bt_rectangle_shape_grey);
                } else {
                    bt_submit.setClickable(true);
                    bt_submit.setBackgroundResource(R.drawable.bt_rectangle_shape_green);
                }
            }
        });

        et_set_new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_set_new_password.getText().toString().trim().length() < 6 || et_verification_code.getText().toString().trim().length() != 6){
                    bt_submit.setClickable(false);
                    bt_submit.setBackgroundResource(R.drawable.bt_rectangle_shape_grey);
                } else {
                    bt_submit.setClickable(true);
                    bt_submit.setBackgroundResource(R.drawable.bt_rectangle_shape_green);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_submit: // 提交
                setPassword();
                break;
        }
    }

    // 获取短信验证码
    private void getVerificationCode() {
        url_get_verification_code = Constants.baseUrl + Constants.url_get_verification_code;
        type = "1";
        paramsMap_get_verification_code.put("phone", phone);
        paramsMap_get_verification_code.put("type", type);
        Okhttp3Utils.getAsycRequest(url_get_verification_code, paramsMap_get_verification_code, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(SetPasswordActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_getCode = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_getCode, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(SetPasswordActivity.this, "短信发送成功！");
//                        Intent intent = new Intent(SetPasswordActivity.this, VerificationCodeActivity.class);
//                        intent.putExtra("phone_num", et_phone_num.getText().toString().trim());
//                        startActivity(intent);
//                        finish();
                    } else {
                        ToastUtils.showShort(SetPasswordActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(SetPasswordActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    private void setData(){
        code = et_verification_code.getText().toString().trim();
        password = et_set_new_password.getText().toString().trim();
        paramsMap_forgetPassword.put("code", code);
        paramsMap_forgetPassword.put("password", password);
        paramsMap_forgetPassword.put("phone", phone);
    }
    // 设置密码
    private void setPassword(){
        bt_submit.setClickable(false);
        url_forgetPassword = Constants.baseUrl + Constants.url_forgetPassword;
        setData();
        Okhttp3Utils.postAsyRequest_Form(url_forgetPassword, paramsMap_forgetPassword, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(SetPasswordActivity.this, "服务器未响应！");
                bt_submit.setClickable(true);
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_getCode = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_getCode, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(SetPasswordActivity.this, "密码设置成功！");
                        bt_submit.setClickable(false);
                        Intent intent = new Intent(SetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.showShort(SetPasswordActivity.this, resultInfo.getMessage());
                        bt_submit.setClickable(true);
                    }
                } else {
                    ToastUtils.showShort(SetPasswordActivity.this, "服务器返回数据为空！");
                    bt_submit.setClickable(true);
                }
            }
        });
    }
}
