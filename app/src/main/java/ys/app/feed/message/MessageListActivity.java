package ys.app.feed.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
import ys.app.feed.adapter.MessageAdapter;
import ys.app.feed.bean.MessageItem;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

public class MessageListActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    private XRecyclerView recyclerview_notice;
    private ArrayList<MessageItem> list_message = new ArrayList<MessageItem>();
    private MessageAdapter adapter_message ;
    // 获取消息通知列表
    private String url_getMessageData; // 获取消息通知列表接口url
    private String userId; // 用户名
    private HashMap<String, String> paramsMap_url_getMessageData = new HashMap<String, String>();
    private Integer pageNum = 1; // 页
    private Integer pageSize = 10; // 行
    private String id; // id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
        getMessageData();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        recyclerview_notice = (XRecyclerView) findViewById(R.id.recyclerview_notice);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_notice.setLayoutManager(layoutManager);

        recyclerview_notice.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerview_notice.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        recyclerview_notice.setArrowImageView(R.mipmap.iconfont_downgrey);

        recyclerview_notice
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
//        View header = LayoutInflater.from(this).inflate(R.bottom_group_buy_person_num.activity_score_header, (ViewGroup) findViewById(android.R.id.content), false);
//        header.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "这是头部", Toast.LENGTH_LONG).show();
//            }
//        });
//        recyclerview_notice.addHeaderView(header);
        recyclerview_notice.getDefaultFootView().setLoadingHint("加载中...");
        recyclerview_notice.getDefaultFootView().setNoMoreHint("没有更多数据了");
        recyclerview_notice.getDefaultFootView().setMinimumHeight(100);

        recyclerview_notice.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum++;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        recyclerview_notice.refreshComplete();
                    }

                }, 0);            //refresh data here
            }

            @Override
            public void onLoadMore() {
                Log.e("aaaaa", "call onLoadMore");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        pageNum++;
                        getMessageData();
                    }
                }, 1000);
            }
        });

        adapter_message = new MessageAdapter(list_message);
        adapter_message.setClickCallBack(new MessageAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = new Intent(MessageListActivity.this, MessageDetailActivity.class);
                id = list_message.get(pos).getId();
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        recyclerview_notice.setAdapter(adapter_message);

        // 用户id
        userId = (String) SPUtils.get(MessageListActivity.this, "userId", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    // 获取消息列表
    private void getMessageData(){
        paramsMap_url_getMessageData.put("pageNum", pageNum + "");
        paramsMap_url_getMessageData.put("pageSize", pageSize + "");
        paramsMap_url_getMessageData.put("userId", userId);
        url_getMessageData = Constants.baseUrl + Constants.url_getMessageData;
        Okhttp3Utils.getAsycRequest(url_getMessageData, paramsMap_url_getMessageData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(MessageListActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONArray data_jsonArray = (JSONArray)response_jsonObject.getJSONArray("data");
                        String data_json_str =JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                        List<MessageItem> list_data = JSONObject.parseArray(data_json_str, MessageItem.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0){
                            list_message.addAll(list_data);
                            adapter_message.notifyDataSetChanged();
                        }
                    } else {
                        ToastUtils.showShort(MessageListActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(MessageListActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }
}
