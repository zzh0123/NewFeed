package ys.app.feed.batching;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.constant.Constants;
import ys.app.feed.widget.x5.X5WebView;

public class BatchingActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_back; // 返回
    // x5
    private X5WebView x5_webview_batching;

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
        setContentView(R.layout.activity_batching);
        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        // x5
        x5_webview_batching = (X5WebView) findViewById(R.id.x5_webview_batching);
        x5_webview_batching.loadUrl(Constants.url_accurate_batching);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (x5_webview_batching != null && x5_webview_batching.canGoBack()) {
                x5_webview_batching.goBack();
                return true;
            } else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || x5_webview_batching == null || intent.getData() == null)
            return;
        x5_webview_batching.loadUrl(intent.getData().toString());
    }

    @Override
    protected void onDestroy() {
        if (x5_webview_batching != null)
            x5_webview_batching.destroy();
        super.onDestroy();
    }
}
