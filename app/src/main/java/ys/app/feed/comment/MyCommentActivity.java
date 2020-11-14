package ys.app.feed.comment;

import android.app.Activity;
import android.os.Bundle;

import ys.app.feed.MyApplication;
import ys.app.feed.R;

/**
 *  评论列表
 */
public class MyCommentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);
    }
}
