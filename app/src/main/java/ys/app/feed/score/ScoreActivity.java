package ys.app.feed.score;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import ys.app.feed.adapter.ScoreAdapter;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.bean.Score;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

/**
 *  积分列表
 */
public class ScoreActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_back; // 返回

    private static final String TAG = "ScoreActivity";

    private TextView tv_score_income, tv_score_outcome; // 积分收入,积分支出

    private XRecyclerView mRecyclerView;
    private ScoreAdapter adapter_score;

    private TextView tv_total_score;

    // 获取用户积分
    private String url_getUserScoreData; // 获取地址接口url
    private Integer pageNum = 1; // 页
    private Integer pageSize = 10; // 行
    private Integer type = 0; // 积分类型: 0:加积分 | 1.减积分
    private String userId; // 用户id
    private HashMap<String, String> paramsMap_getUserScoreData = new HashMap<String, String>();
    private ArrayList<Score> list_score = new ArrayList<Score>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
        type = 0;
        getScore();
    }

    private void initView() {
        mRecyclerView = (XRecyclerView) this.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.iconfont_downgrey);

        mRecyclerView
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
        View header = LayoutInflater.from(this).inflate(R.layout.activity_score_header, (ViewGroup) findViewById(android.R.id.content), false);
//        header.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "这是头部", Toast.LENGTH_LONG).show();
//            }
//        });
        mRecyclerView.addHeaderView(header);
        mRecyclerView.getDefaultFootView().setLoadingHint("加载中...");
        mRecyclerView.getDefaultFootView().setNoMoreHint("没有更多数据了");
        mRecyclerView.getDefaultFootView().setMinimumHeight(100);

        ll_back = (LinearLayout) header.findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        tv_total_score = (TextView) header.findViewById(R.id.tv_total_score);
        tv_score_income = (TextView) header.findViewById(R.id.tv_score_income);
        tv_score_outcome = (TextView) header.findViewById(R.id.tv_score_outcome);

        tv_score_income.setOnClickListener(this);
        tv_score_outcome.setOnClickListener(this);

        // When the item number of the screen number is list.size-2,we call the onLoadMore
//        mRecyclerView.setLimitNumberToCallLoadMore(2);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum++;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mRecyclerView.refreshComplete();
                    }

                }, 0);            //refresh data here
            }

            @Override
            public void onLoadMore() {
                Log.e("aaaaa", "call onLoadMore");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        pageNum++;
                        getScore();
                    }
                }, 1000);
            }
        });

        adapter_score = new ScoreAdapter(list_score, type);
        adapter_score.setClickCallBack(
                new ScoreAdapter.ItemClickCallBack() {
                    @Override
                    public void onItemClick(int pos) {
                        // a demo for notifyItemRemoved
                        LogUtils.i("--pos--", "--" + pos);
                        ToastUtils.showShort(ScoreActivity.this, "--" + pos);

                    }
                }
        );
        mRecyclerView.setAdapter(adapter_score);

        // 用户id
        userId = (String) SPUtils.get(ScoreActivity.this, "userId", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_score_income: // 积分收入
                tv_score_income.setBackgroundResource(R.drawable.bt_rectangle_shape_half_white);
                tv_score_outcome.setBackground(null);
                list_score.clear();
                adapter_score.notifyDataSetChanged();
                type = 0;
                pageNum = 1;
                getScore();
                break;
            case R.id.tv_score_outcome: // 积分支出
                tv_score_outcome.setBackgroundResource(R.drawable.bt_rectangle_shape_half_white);
                tv_score_income.setBackground(null);
                list_score.clear();
                adapter_score.notifyDataSetChanged();
                type = 1;
                pageNum = 1;
                getScore();
                break;
            case R.id.ll_score: // 积分
//                Intent intent1 = new Intent(getActivity(), ScoreActivity.class);
//                startActivity(intent1);
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    // 获取用户收入积分
    private void getScore() {
        paramsMap_getUserScoreData.put("pageNum", pageNum + "");
        paramsMap_getUserScoreData.put("pageSize", pageSize + "");
        paramsMap_getUserScoreData.put("type", type + "");
        paramsMap_getUserScoreData.put("userId", userId);
        url_getUserScoreData = Constants.baseUrl + Constants.url_getUserScoreData;
        Okhttp3Utils.getAsycRequest(url_getUserScoreData, paramsMap_getUserScoreData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(ScoreActivity.this, "服务器未响应！");
                mRecyclerView.refreshComplete();
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = (JSONObject) response_jsonObject.get("data");
                        JSONArray data_jsonArray = (JSONArray) data_jsonObject.getJSONArray("resultData");
                        String data_json_str = JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                        List<Score> list_data = JSONObject.parseArray(data_json_str, Score.class);//把字符串转换成集合
                        String totalScore = data_jsonObject.getString("totalScore");
                        tv_total_score.setText(totalScore);

                        if (list_data != null && list_data.size() > 0) {
                            if (type == 0) { // 积分收入
                                list_score.addAll(list_data);
                                if (adapter_score == null) {
                                    adapter_score = new ScoreAdapter(list_score, type);
                                    adapter_score.setType(0);
                                    mRecyclerView.setAdapter(adapter_score);
                                }
                                adapter_score.setType(0);
                                mRecyclerView.refreshComplete();
                                adapter_score.notifyDataSetChanged();
//                                mRecyclerView.refresh();
//                                mRecyclerView.refreshComplete();
                            } else {
                                list_score.addAll(list_data);
                                if (adapter_score == null) {
                                    adapter_score = new ScoreAdapter(list_score, type);
                                    adapter_score.setType(1);
                                    mRecyclerView.setAdapter(adapter_score);
                                }
                                adapter_score.setType(1);
                                mRecyclerView.refreshComplete();
                                adapter_score.notifyDataSetChanged();
                            }
                        } else {
                            mRecyclerView.refreshComplete();
                            mRecyclerView.setNoMore(true);
                        }

                    } else {
                        mRecyclerView.refreshComplete();
                    }
                } else {
                    ToastUtils.showShort(ScoreActivity.this, "服务器返回数据为空！");
                    mRecyclerView.refreshComplete();
                }
            }
        });
    }
}
