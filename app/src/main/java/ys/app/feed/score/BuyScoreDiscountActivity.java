package ys.app.feed.score;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.utils.SPUtils;

public class BuyScoreDiscountActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    private String userId; // 用户id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_score_discount);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        // 用户id
        userId = (String) SPUtils.get(BuyScoreDiscountActivity.this, "userId", "");

        initView();
    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

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
