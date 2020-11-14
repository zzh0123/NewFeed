package ys.app.feed.collect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.AllGoodsAdapter;
import ys.app.feed.adapter.MyCollectAdapter;
import ys.app.feed.adapter.MyInviteAdapter;
import ys.app.feed.bean.MyCollectionItem;
import ys.app.feed.bean.MyInviteItem;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.goods.AllGoodsActivity;
import ys.app.feed.goods.GoodsDetailActivity;
import ys.app.feed.invite.MyInviteActivity;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

public class MyCollectActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    // 获取用户收藏商品
    private String url_getCollectionData; // url
    private Integer pageNum = 1; // 页
    private Integer pageSize = 10; // 行
    private String userId; // 用户id
    private HashMap<String, String> paramsMap_getCollectionData = new HashMap<String, String>();
    private ArrayList<MyCollectionItem> list_myCollectionItem = new ArrayList<MyCollectionItem>();
    private XRecyclerView recyclerview_my_collect;
    private MyCollectAdapter adapter_my_collect;

    // 商品收藏和取消收藏
    private String commodityId; // 商品Id
    private String url_commodityCollection; // url
    private HashMap<String, String> paramsMap_commodityCollection = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        // 用户id
        userId = (String) SPUtils.get(MyCollectActivity.this, "userId", "");
        initView();
        getCollectionData();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        // 获取用户收藏商品
        recyclerview_my_collect = (XRecyclerView) findViewById(R.id.recyclerview_my_collect);

        recyclerview_my_collect.setPullRefreshEnabled(false);
//        recyclerview_my_collect.setLoadingMoreEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyCollectActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_my_collect.setLayoutManager(layoutManager);

//        mRecyclerView_recommend.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
//        mRecyclerView_recommend.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
//        mRecyclerView_recommend.setArrowImageView(R.mipmap.iconfont_downgrey);

        recyclerview_my_collect
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
//        header = LayoutInflater.from(getActivity()).inflate(R.bottom_group_buy_person_num.fragment_goods_header, (ViewGroup) findViewById(android.R.id.content), false);
//        mRecyclerView.addHeaderView(header);
        recyclerview_my_collect.getDefaultFootView().setLoadingHint("加载中...");
        recyclerview_my_collect.getDefaultFootView().setNoMoreHint("没有更多数据了");
        recyclerview_my_collect.getDefaultFootView().setMinimumHeight(100);

        recyclerview_my_collect.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum++;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        recyclerview_my_collect.refreshComplete();
                    }

                }, 0);            //refresh data here
            }

            @Override
            public void onLoadMore() {
                Log.e("aaaaa", "call onLoadMore");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        pageNum++;
                        getCollectionData();
                    }
                }, 1000);
            }
        });

        adapter_my_collect = new MyCollectAdapter(this, list_myCollectionItem);
        adapter_my_collect.setClickCallBack(new MyCollectAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                commodityId = list_myCollectionItem.get(pos).getCommodityId();
                commodityCollection();
            }
        });

        recyclerview_my_collect.setAdapter(adapter_my_collect);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    // 获取用户收藏商品
    private void getCollectionData(){
        paramsMap_getCollectionData.put("pageNum", pageNum + "");
        paramsMap_getCollectionData.put("pageSize", pageSize + "");
        paramsMap_getCollectionData.put("userId", userId);
        url_getCollectionData = Constants.baseUrl + Constants.url_getCollectionData;
        Okhttp3Utils.getAsycRequest(url_getCollectionData, paramsMap_getCollectionData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(MyCollectActivity.this, "服务器未响应！");
                recyclerview_my_collect.refreshComplete();
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
                        List<MyCollectionItem> list_data = JSONObject.parseArray(data_json_str, MyCollectionItem.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0){
                            list_myCollectionItem.addAll(list_data);
                            adapter_my_collect.notifyDataSetChanged();
                            recyclerview_my_collect.refreshComplete();
                        } else {
                            recyclerview_my_collect.refreshComplete();
                            recyclerview_my_collect.setNoMore(true);
                        }
                    } else {
                        ToastUtils.showShort(MyCollectActivity.this, resultInfo.getMessage());
                        recyclerview_my_collect.refreshComplete();
                    }
                } else {
                    ToastUtils.showShort(MyCollectActivity.this, "服务器返回数据为空！");
                    recyclerview_my_collect.refreshComplete();
                }
            }
        });
    }

    // 商品收藏和取消收藏
    private void commodityCollection(){
        list_myCollectionItem.clear();
        paramsMap_commodityCollection.put("commodityId", commodityId);
        paramsMap_commodityCollection.put("userId", userId);
        url_commodityCollection = Constants.baseUrl + Constants.url_commodityCollection;
        Okhttp3Utils.getAsycRequest(url_commodityCollection, paramsMap_commodityCollection, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(MyCollectActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        getCollectionData();
                    } else {
                        ToastUtils.showShort(MyCollectActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(MyCollectActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }
}