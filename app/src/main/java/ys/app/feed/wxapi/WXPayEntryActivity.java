package ys.app.feed.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.concurrent.TimeUnit;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.widget.CountDownTextView;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String TAG = "WXPayEntryActivity";
	
    private IWXAPI api;

	private ImageView iv_pay_result; // 微信支付结果
	private TextView tv_pay_result; // 微信支付结果

	private CountDownTextView tv_count_down; // 倒计时

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

		// 添加Activity到堆栈
		MyApplication.getInstance().addActivity(this);

		initView();

    	api = WXAPIFactory.createWXAPI(this, Constants.AppID);
        api.handleIntent(getIntent(), this);
    }

	private void initView() {
		iv_pay_result = (ImageView) findViewById(R.id.iv_pay_result);
		tv_pay_result = (TextView) findViewById(R.id.tv_pay_result);
		tv_count_down = (CountDownTextView) findViewById(R.id.tv_count_down);

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

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		int result_code = resp.errCode;
		Log.d(TAG, "wx--onPayFinish, errCode = " + result_code);
		Log.d(TAG, "wx1--onPayFinish, errCode = " + resp.getType());
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			LogUtils.i("--11", "--11");
			if (result_code == 0) {
				iv_pay_result.setImageResource(R.mipmap.pay_success);
				tv_pay_result.setText("支付成功！");
				LogUtils.i("--00", "--00");
			} else if (result_code == -1){
				iv_pay_result.setImageResource(R.mipmap.pay_fail);
				tv_pay_result.setText("支付失败！");
				LogUtils.i("--1", "--1");
			} else {
				iv_pay_result.setImageResource(R.mipmap.pay_fail);
				tv_pay_result.setText("支付取消！");
				LogUtils.i("--支付取消", "支付取消");
			}
		}
	}
}