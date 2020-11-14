package ys.app.feed.alipay;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.widget.CountDownTextView;

public class AlipayResultInfoActivity extends Activity {

    private ImageView iv_pay_result; // 微信支付结果
    private TextView tv_pay_result; // 微信支付结果

    private CountDownTextView tv_count_down; // 倒计时

    private String resultStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay_result);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        resultStatus = getIntent().getStringExtra("resultStatus");

        initView();
    }

    private void initView() {
        iv_pay_result = (ImageView) findViewById(R.id.iv_pay_result);
        tv_pay_result = (TextView) findViewById(R.id.tv_pay_result);
        tv_count_down = (CountDownTextView) findViewById(R.id.tv_count_down);

        // 判断resultStatus 为9000则代表支付成功
        if (TextUtils.equals(resultStatus, "9000")) { // 订单支付成功
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            iv_pay_result.setImageResource(R.mipmap.pay_success);
            tv_pay_result.setText("支付成功！");
        } else if (TextUtils.equals(resultStatus, "4000")){ // 		订单支付失败
            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            iv_pay_result.setImageResource(R.mipmap.pay_fail);
            tv_pay_result.setText("支付失败！");
        } else if (TextUtils.equals(resultStatus, "6001")){ // 	用户中途取消
            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            iv_pay_result.setImageResource(R.mipmap.pay_fail);
            tv_pay_result.setText("支付取消！");
        } else { //  支付异常
            iv_pay_result.setImageResource(R.mipmap.pay_fail);
            tv_pay_result.setText("支付异常！");
        }

        tv_count_down
                .setNormalText("")
                .setCountDownText("自动跳转", "")
                .setCloseKeepCountDown(false)//关闭页面保持倒计时开关
                .setCountDownClickable(false)//倒计时期间点击事件是否生效开关
                .setShowFormatTime(false)//是否格式化时间
                .setIntervalUnit(TimeUnit.SECONDS)
                .setOnCountDownStartListener(new CountDownTextView.OnCountDownStartListener() {
                    @Override
                    public void onStart() {
//                        Toast.makeText(VerificationCodeActivity.this, "开始计时", Toast.LENGTH_SHORT).show();
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
                        if (TextUtils.equals(resultStatus, "9000")) { // 订单支付成功

                        }
                        finish();
                    }
                })
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(VerificationCodeActivity.this, "短信已发送", Toast.LENGTH_SHORT).show();
                    }
                });
        tv_count_down.startCountDown(3);

    }
}
