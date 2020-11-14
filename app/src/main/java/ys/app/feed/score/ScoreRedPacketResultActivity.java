package ys.app.feed.score;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ys.app.feed.MyApplication;
import ys.app.feed.R;

/**
 *  积分红包详情
 */
public class ScoreRedPacketResultActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回
    private TextView tv_score_red_packet_result;
    private Double score_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_red_packet_result);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        score_result = getIntent().getDoubleExtra("score_result", 0);
        initView();
    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        tv_score_red_packet_result = (TextView) findViewById(R.id.tv_score_red_packet_result);
        tv_score_red_packet_result.setText("+" + score_result + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
            default:
                break;
        }
    }
}
