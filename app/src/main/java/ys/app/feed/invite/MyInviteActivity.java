package ys.app.feed.invite;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.shaohui.bottomdialog.BottomDialog;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.MyInviteAdapter;
import ys.app.feed.adapter.RecommendGoodsAdapter;
import ys.app.feed.adapter.ScoreAdapter;
import ys.app.feed.bean.MyInviteItem;
import ys.app.feed.bean.RecommendGoodsItem;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.bean.Score;
import ys.app.feed.constant.Constants;
import ys.app.feed.goods.GoodsDetailActivity;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.utils.WRKShareUtil;

public class MyInviteActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    // 获取我的邀请列表
    private String url_getInviteData; // url
    private String userId; // 用户id
    private HashMap<String, String> paramsMap_getInviteData = new HashMap<String, String>();
    private ArrayList<MyInviteItem> list_myInviteItem = new ArrayList<MyInviteItem>();
    private XRecyclerView recyclerview_my_invite;
    private MyInviteAdapter adapter_my_invite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_invite);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
        getInviteData();
    }

    private void initView() {
        // 用户id
        userId = (String) SPUtils.get(MyInviteActivity.this, "userId", "");

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        // 我的邀请列表
        recyclerview_my_invite = (XRecyclerView) findViewById(R.id.recyclerview_my_invite);

        recyclerview_my_invite.setPullRefreshEnabled(false);
        recyclerview_my_invite.setLoadingMoreEnabled(false);
        LinearLayoutManager  layoutManager = new LinearLayoutManager(MyInviteActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_my_invite.setLayoutManager(layoutManager);

//        mRecyclerView_recommend.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
//        mRecyclerView_recommend.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
//        mRecyclerView_recommend.setArrowImageView(R.mipmap.iconfont_downgrey);

        recyclerview_my_invite
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
//        header = LayoutInflater.from(getActivity()).inflate(R.bottom_group_buy_person_num.fragment_goods_header, (ViewGroup) findViewById(android.R.id.content), false);
//        mRecyclerView.addHeaderView(header);
//        mRecyclerView_recommend.getDefaultFootView().setLoadingHint("加载中...");
//        mRecyclerView_recommend.getDefaultFootView().setNoMoreHint("没有更多数据了");
//        mRecyclerView_recommend.getDefaultFootView().setMinimumHeight(100);

        adapter_my_invite = new MyInviteAdapter(this, list_myInviteItem);

        recyclerview_my_invite.setAdapter(adapter_my_invite);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    // 获取我的邀请列表
    private void getInviteData(){
        paramsMap_getInviteData.put("userId", userId);
        url_getInviteData = Constants.baseUrl + Constants.url_getInviteData;
        Okhttp3Utils.getAsycRequest(url_getInviteData, paramsMap_getInviteData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(MyInviteActivity.this, "服务器未响应！");
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
                        List<MyInviteItem> list_data = JSONObject.parseArray(data_json_str, MyInviteItem.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0){
                            list_myInviteItem.addAll(list_data);
                            adapter_my_invite.notifyDataSetChanged();
                        }
                    } else {
                        ToastUtils.showShort(MyInviteActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(MyInviteActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }
}
