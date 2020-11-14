package ys.app.feed.about;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import cn.jzvd.JZVideoPlayerStandard;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.constant.Constants;

public class KnowAboutActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_know_about);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        JZVideoPlayerStandard jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
        jzVideoPlayerStandard.setUp(Constants.start_video_url,
                JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                "了解慧河");
        Glide.with(this)
                .load(Constants.start_img_url)
                .into(jzVideoPlayerStandard.thumbImageView);

        JZVideoPlayerStandard jzVideoPlayerStandard1 = (JZVideoPlayerStandard) findViewById(R.id.videoplayer1);
        jzVideoPlayerStandard1.setUp(Constants.start_video_url1,
                JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                "慧河视频");
        Glide.with(this)
                .load(Constants.start_img_url)
                .into(jzVideoPlayerStandard.thumbImageView);

        JZVideoPlayerStandard jzVideoPlayerStandard2 = (JZVideoPlayerStandard) findViewById(R.id.videoplayer2);
        jzVideoPlayerStandard2.setUp(Constants.start_video_url2,
                JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                "慧河视频");
        Glide.with(this)
                .load(Constants.start_img_url)
                .into(jzVideoPlayerStandard.thumbImageView);

        JZVideoPlayerStandard jzVideoPlayerStandard3 = (JZVideoPlayerStandard) findViewById(R.id.videoplayer3);
        jzVideoPlayerStandard3.setUp(Constants.start_video_url3,
                JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                "慧河视频");
        Glide.with(this)
                .load(Constants.start_img_url)
                .into(jzVideoPlayerStandard.thumbImageView);


        JZVideoPlayerStandard jzVideoPlayerStandard4 = (JZVideoPlayerStandard) findViewById(R.id.videoplayer4);
        jzVideoPlayerStandard4.setUp(Constants.start_video_url4,
                JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                "慧河视频");
        Glide.with(this)
                .load(Constants.start_img_url)
                .into(jzVideoPlayerStandard.thumbImageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }
}
