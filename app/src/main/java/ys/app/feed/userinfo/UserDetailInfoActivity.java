package ys.app.feed.userinfo;

import android.app.Activity;
import android.os.Bundle;

import ys.app.feed.MyApplication;
import ys.app.feed.R;

public class UserDetailInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_info);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);
    }
}
