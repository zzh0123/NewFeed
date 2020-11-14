package ys.app.feed.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.about.KnowAboutActivity;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.collect.MyCollectActivity;
import ys.app.feed.utils.AppUtils;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.SPUtils;

public class SettingsActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回
    private String userId; // 用户id

    private TextView tv_version; // 版本号
    private RelativeLayout rl_clear_cache; // 清除缓存
    private RelativeLayout rl_about; // 关于

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView() {
        // 用户id
//        userId = (String) SPUtils.get(SettingsActivity.this, "userId", "");

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        tv_version = (TextView) findViewById(R.id.tv_version);
        String versionName = AppUtils.getVersionName(this);
        LogUtils.i("--versionName--", "--versionName--" + versionName);
        tv_version.setText(versionName);
        rl_clear_cache = (RelativeLayout) findViewById(R.id.rl_clear_cache);
        rl_about = (RelativeLayout) findViewById(R.id.rl_about);
        rl_clear_cache.setOnClickListener(this);
        rl_about.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_clear_cache: // 清除缓存
//                finish();
                break;
            case R.id.rl_about: // 关于
                Intent intent = new Intent(SettingsActivity.this, KnowAboutActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }
}
