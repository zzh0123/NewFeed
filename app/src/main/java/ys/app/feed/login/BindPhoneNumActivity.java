package ys.app.feed.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.address.AddAddressActivity;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.ClearEditText;

/**
 *  微信绑定手机号
 */
public class BindPhoneNumActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_back; // 返回

    private ClearEditText et_phone_num; // 输入手机号
    private Button bt_get_verification_code; // 获取验证码

    private String headPortrait; // 头像
    private String name; // 微信名
    private String openId; //
    private String phone; //手机号

    private String url_get_verification_code, type; // 获取验证码接口url, 0.注册/登录 | 1.忘记密码类型, 手机号
    private HashMap<String, String> paramsMap_get_verification_code = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone_num);

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

        et_phone_num = (ClearEditText) findViewById(R.id.et_phone_num); // 输入手机号
        bt_get_verification_code = (Button) findViewById(R.id.bt_get_verification_code); // 获取验证码

        bt_get_verification_code.setOnClickListener(this);

        et_phone_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_phone_num.getText().toString().trim().length() == 11){
                    bt_get_verification_code.setClickable(true);
                    bt_get_verification_code.setBackgroundResource(R.drawable.bt_rectangle_shape_green);
                } else {
                    bt_get_verification_code.setClickable(false);
                    bt_get_verification_code.setBackgroundResource(R.drawable.bt_rectangle_shape_grey);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_get_verification_code: // 获取验证码
                phone = et_phone_num.getText().toString().trim();
                type = "0";
                paramsMap_get_verification_code.put("phone", phone);
                paramsMap_get_verification_code.put("type", type);
                getVerificationCode();
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    // 获取短信验证码
    private void getVerificationCode() {
        url_get_verification_code = Constants.baseUrl + Constants.url_get_verification_code;
        Okhttp3Utils.getAsycRequest(url_get_verification_code, paramsMap_get_verification_code, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(BindPhoneNumActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_getCode = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_getCode, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(BindPhoneNumActivity.this, "短信发送成功！");
                        Intent intent = new Intent(BindPhoneNumActivity.this, WxVerificationCodeActivity.class);
                        intent.putExtra("headPortrait", headPortrait);
                        intent.putExtra("name", name);
                        intent.putExtra("openId", openId);
                        intent.putExtra("phone_num", et_phone_num.getText().toString().trim());
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.showShort(BindPhoneNumActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(BindPhoneNumActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

}
