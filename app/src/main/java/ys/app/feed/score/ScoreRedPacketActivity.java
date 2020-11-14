package ys.app.feed.score;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.CouponAdapter;
import ys.app.feed.adapter.ReceivingAddressAdapter;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.Coupon;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.dialog.ScoreRedPacketDialog;
import ys.app.feed.widget.redpacket.CustomDialog;
import ys.app.feed.widget.redpacket.OnRedPacketDialogClickListener;
import ys.app.feed.widget.redpacket.RedPacketEntity;
import ys.app.feed.widget.redpacket.RedPacketViewHolder;

/**
 *  积分红包
 */

public class ScoreRedPacketActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    private String userId; // 用户id

    private View mRedPacketDialogView;
    private RedPacketViewHolder mRedPacketViewHolder;
    private CustomDialog mRedPacketDialog;

    private ScoreRedPacketDialog scoreRedPacketDialog;

    // 积分红包
    private String url_luckDraw; // url
    private HashMap<String, String> paramsMap_luckDraw = new HashMap<String, String>();
    private Double score_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_red_packet);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        // 用户id
        userId = (String) SPUtils.get(ScoreRedPacketActivity.this, "userId", "");

        initView();

    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        RedPacketEntity entity = new RedPacketEntity("", "", "积分红包");
        showRedPacketDialog(entity);
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

    public void showRedPacketDialog(RedPacketEntity entity) {
        if (mRedPacketDialogView == null) {
            mRedPacketDialogView = View.inflate(this, R.layout.dialog_red_packet, null);
            mRedPacketViewHolder = new RedPacketViewHolder(this, mRedPacketDialogView);
            mRedPacketDialog = new CustomDialog(this, mRedPacketDialogView, R.style.custom_dialog);
            mRedPacketDialog.setCancelable(false);
        }

        mRedPacketViewHolder.setData(entity);
        mRedPacketViewHolder.setOnRedPacketDialogClickListener(new OnRedPacketDialogClickListener() {
            @Override
            public void onCloseClick() {
                mRedPacketDialog.dismiss();
                finish();
            }

            @Override
            public void onOpenClick() {
                luckDraw();
            }
        });

        mRedPacketDialog.show();
    }

    // 积分红包
    private void luckDraw(){
        paramsMap_luckDraw.put("userId", userId);
        url_luckDraw = Constants.baseUrl + Constants.url_luckDraw;
        Okhttp3Utils.getAsycRequest(url_luckDraw, paramsMap_luckDraw, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                mRedPacketViewHolder.stopAnim();
                ToastUtils.showShort(ScoreRedPacketActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        score_result = response_jsonObject.getDouble("data"); // 抽取的积分
                        mRedPacketViewHolder.stopAnim();
                        showScoreRedPacketDialog();
//                        Intent intent = new Intent(ScoreRedPacketActivity.this, ScoreRedPacketResultActivity.class);
//                        intent.putExtra("score_result", score_result);
//                        startActivity(intent);
//                        finish();
                    } else {
                        mRedPacketViewHolder.stopAnim();
                        ToastUtils.showShort(ScoreRedPacketActivity.this, resultInfo.getMessage());
                    }
                } else {
                    mRedPacketViewHolder.stopAnim();
                    ToastUtils.showShort(ScoreRedPacketActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 积分红包
    private void showScoreRedPacketDialog() {
        scoreRedPacketDialog = new ScoreRedPacketDialog(this);
        scoreRedPacketDialog.setScore("+ " + score_result + "");
        scoreRedPacketDialog.setNoOnclickListener("取消", new ScoreRedPacketDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
//                    Toast.makeText(getActivity(), "点击了--取消--按钮", Toast.LENGTH_LONG).show();
                scoreRedPacketDialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = scoreRedPacketDialog.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        scoreRedPacketDialog.getWindow().setAttributes(lp);
        scoreRedPacketDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        scoreRedPacketDialog.show();

    }

}

