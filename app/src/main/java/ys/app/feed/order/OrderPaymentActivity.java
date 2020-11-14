package ys.app.feed.order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;

import java.util.HashMap;
import java.util.Map;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.alipay.AlipayResultInfoActivity;
import ys.app.feed.alipay.AuthResult;
import ys.app.feed.alipay.PayResult;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.wxapi.WXPayUtils;

public class OrderPaymentActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_back; // 返回

    private LinearLayout ll_wx_pay; // 微信支付
    private LinearLayout ll_ali_pay; // 支付宝支付

    private ImageView iv_wxpay_check; // 微信支付
    private ImageView iv_alipay_check; // 支付宝支付

    private TextView tv_total_money; // 总价

    private LinearLayout ll_immediate_payment; // 立即支付

    private boolean is_wx_pay;

    private Double money;
    private String orderNo, orderType;

    // 支付宝支付
    private String url_alipay; // url
    private HashMap<String, String> paramsMap_alipay = new HashMap<String, String>();
    private JSONObject paramsJsonObject_alipay;

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) { // 订单支付成功
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Intent intent = new Intent(OrderPaymentActivity.this, AlipayResultInfoActivity.class);
                        intent.putExtra("resultStatus", resultStatus);
                        startActivity(intent);
                        finish();
                    } else if (TextUtils.equals(resultStatus, "4000")){ // 		订单支付失败
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Intent intent = new Intent(OrderPaymentActivity.this, AlipayResultInfoActivity.class);
                        intent.putExtra("resultStatus", resultStatus);
                        startActivity(intent);
                        finish();
                    } else if (TextUtils.equals(resultStatus, "6001")){ // 	用户中途取消
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Intent intent = new Intent(OrderPaymentActivity.this, AlipayResultInfoActivity.class);
                        intent.putExtra("resultStatus", resultStatus);
                        startActivity(intent);
                        finish();
                    } else { //  支付异常
                        Intent intent = new Intent(OrderPaymentActivity.this, AlipayResultInfoActivity.class);
                        intent.putExtra("resultStatus", resultStatus);
                        startActivity(intent);
                        finish();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        showAlert(OrderPaymentActivity.this, getString(R.string.auth_success) + authResult);
                    } else {
                        // 其他状态值则为授权失败
                        showAlert(OrderPaymentActivity.this, getString(R.string.auth_failed) + authResult);
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };


    // 微信支付
    private String url_wxpay; // url
    private HashMap<String, String> paramsMap_wxpay = new HashMap<String, String>();
    private JSONObject paramsJsonObject_wxpay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        money = getIntent().getDoubleExtra("money", 0);
//        money = 0.01;
        orderNo = getIntent().getStringExtra("orderNo");
        orderType = getIntent().getStringExtra("orderType");

        initView();
        setChioceItem(0);
        is_wx_pay = true;
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);

        ll_wx_pay = (LinearLayout) findViewById(R.id.ll_wx_pay);
        ll_ali_pay = (LinearLayout) findViewById(R.id.ll_ali_pay);
        tv_total_money = (TextView) findViewById(R.id.tv_total_money);
        ll_immediate_payment = (LinearLayout) findViewById(R.id.ll_immediate_payment);

        tv_total_money.setText("¥" + money);
        iv_wxpay_check = (ImageView) findViewById(R.id.iv_wxpay_check);
        iv_alipay_check = (ImageView) findViewById(R.id.iv_alipay_check);

        ll_back.setOnClickListener(this);
        ll_wx_pay.setOnClickListener(this);
        ll_ali_pay.setOnClickListener(this);
        ll_immediate_payment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_wx_pay:  // 微信支付
                setChioceItem(0);
                is_wx_pay = true;
                break;
            case R.id.ll_ali_pay:  // 支付宝支付
                setChioceItem(1);
                is_wx_pay = false;
                break;
            case R.id.ll_immediate_payment:  // 立即支付
                if (is_wx_pay){
                    wxpay();
                } else {
                    alipay();
                }
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 设置点击选项卡的事件处理
     *
     * @param index 选项卡的标号：0, 1, 2, 3
     */
    private void setChioceItem(int index) {
        clearChioce(); // 清空, 重置选项, 隐藏所有Fragment
        switch (index) { // 0为已领取未使用,1为已使用,2为已过期
            case 0:
                iv_wxpay_check.setImageResource(R.mipmap.checked);
                break;
            case 1:
                iv_alipay_check.setImageResource(R.mipmap.checked);
                break;
        }
    }

    /**
     * 当选中其中一个选项卡时，其他选项卡重置为默认
     */
    private void clearChioce() {
        iv_wxpay_check.setImageResource(R.mipmap.unchecked);
        iv_alipay_check.setImageResource(R.mipmap.unchecked);
    }

    // 支付宝支付订单
    private void alipay() {
        String str_money = String.format("%.2f", money);
        paramsMap_alipay.put("money",str_money);
        paramsMap_alipay.put("orderNo", orderNo);
        paramsMap_alipay.put("orderType", orderType);
        String json = JSON.toJSONString(paramsMap_alipay);
        paramsJsonObject_alipay = JSONObject.parseObject(json);
        url_alipay = Constants.baseUrl + Constants.url_alipay;
        Okhttp3Utils.postAsycRequest_Json(url_alipay, paramsJsonObject_alipay, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(OrderPaymentActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    payV2(str_response);
                } else {
                    ToastUtils.showShort(OrderPaymentActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 微信支付订单
    private void wxpay() {
        paramsMap_wxpay.put("money", ((int) (money * 100)) + "");
        paramsMap_wxpay.put("orderNo", orderNo);
        paramsMap_wxpay.put("orderType", orderType);
        String json = JSON.toJSONString(paramsMap_wxpay);
        LogUtils.i("--json--", "--json--" + json);
        paramsJsonObject_wxpay = JSONObject.parseObject(json);
        url_wxpay = Constants.baseUrl + Constants.url_wxpay;
        Okhttp3Utils.postAsycRequest_Json(url_wxpay, paramsJsonObject_wxpay, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(OrderPaymentActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    JSONObject response_jsonObject = JSONObject.parseObject(str_response);

                    String appId = response_jsonObject.getString("appid");
                    String nonceStr = response_jsonObject.getString("nonce_str");
                    String packageValue = "Sign=WXPay";
                    String partnerId = response_jsonObject.getString("mch_id");
                    String paySign = response_jsonObject.getString("sign2");
                    String prepayId = response_jsonObject.getString("prepay_id");
                    String timeStamp = response_jsonObject.getString("timestamp");
                    LogUtils.i("--appId--", appId + "--"
                            + nonceStr + "--" + packageValue + "--"
                            + partnerId + "--" + paySign + "--"
                            + prepayId + "--" + timeStamp);

                    WXPayUtils.WXPayBuilder builder = new WXPayUtils.WXPayBuilder();
                    builder.setAppId(appId)
                            .setPartnerId(partnerId)
                            .setPrepayId(prepayId)
                            .setPackageValue(packageValue)
                            .setNonceStr(nonceStr)
                            .setTimeStamp(timeStamp)
                            .setSign(paySign)
                            .build().toWXPayNotSign(OrderPaymentActivity.this);
                } else {
                    ToastUtils.showShort(OrderPaymentActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    /**
     * 支付宝支付业务示例
     */
    public void payV2(final String orderNo) {
        if (TextUtils.isEmpty(orderNo)) {
            showAlert(this, "订单号为空！");
            return;
        }

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(OrderPaymentActivity.this);
                Map<String, String> result = alipay.payV2(orderNo, true);
                Log.i("--alipay-result--", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 获取支付宝 SDK 版本号。
     */
    public void showSdkVersion(View v) {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        showAlert(this, getString(R.string.alipay_sdk_version_is) + version);
    }

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show();
    }

    private static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}
