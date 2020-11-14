package ys.app.feed.goods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hgdendi.expandablerecycleradapter.BaseExpandableRecyclerViewAdapter;
import com.hgdendi.expandablerecycleradapter.ViewProducer;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.AllGoodsAdapter;
import ys.app.feed.adapter.GoodsTitleAdapter;

import ys.app.feed.bean.AllGoodsItem;
import ys.app.feed.bean.GoodsTitle;
import ys.app.feed.bean.GoodsTitleSub;
import ys.app.feed.bean.LaunchGroupBuyGoods;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;

import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

/**
 *  全部商品
 */
public class AllGoodsActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "AllGoodsActivity";

    private LinearLayout ll_back; // 返回

    // 获取商品标题
    private String url_getCommodityTitle; // url
    private String userId; // 用户id
    private HashMap<String, String> paramsMap_getCommodityTitle = new HashMap<String, String>();
    private ArrayList<GoodsTitle> list_goodsTitle = new ArrayList<GoodsTitle>();
    private RecyclerView recyclerView; // 全部商品树形列表

    // 获取全部商品列表
    private String classType = "1"; // 1.全部商品列表 | 2.团购直购列表 | 3.半价列表 | 4.积分列表 | 5.砍价列表
    private String subType = ""; // 子类型
    private String type = ""; // 父类型
    private String url_getCommodityData; // url
    private HashMap<String, String> paramsMap_getCommodityData = new HashMap<String, String>();
    private ArrayList<AllGoodsItem> list_allGoodsItem = new ArrayList<AllGoodsItem>();

    // 全部商品列表
    private XRecyclerView mRecyclerView;
    private AllGoodsAdapter adapter_allGoodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_goods);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        // 用户id
        userId = (String) SPUtils.get(AllGoodsActivity.this, "userId", "");

        getCommodityTitle();

//        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        type = list_goodsTitle.get(0).getCode();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GoodsTitleAdapter adapter = new GoodsTitleAdapter(list_goodsTitle);
        adapter.setEmptyViewProducer(new ViewProducer() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new DefaultEmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.empty, parent, false)
                );
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder) {

            }
        });

        adapter.setListener(new BaseExpandableRecyclerViewAdapter.ExpandableRecyclerViewOnClickListener<GoodsTitle, GoodsTitleSub>() {
            @Override
            public boolean onGroupLongClicked(GoodsTitle groupItem) {
//                Log.i("--onGroupLongClicked--", "--onGroupLongClicked--");
                return true;
            }

            @Override
            public boolean onInterceptGroupExpandEvent(GoodsTitle groupItem, boolean isExpand) {
//                Log.i("--onIntercepndEvent--", "--onInterceptGroupExpandEvent--");
//                Toast.makeText(AllGoodsActivity.this, "111type = "+groupItem.getName() , Toast.LENGTH_SHORT).show();
                type = groupItem.getCode();
                subType = "";
                getCommodityData();
                return false;
            }

            @Override
            public void onGroupClicked(GoodsTitle groupItem) {
//                Log.i("--onGroupClicked--", "--onGroupClicked--");
//                Toast.makeText(AllGoodsActivity.this, "type = " + groupItem.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildClicked(GoodsTitle groupItem, GoodsTitleSub childItem) {
//                Toast.makeText(AllGoodsActivity.this, "type = " + groupItem.getName() + "--subtype= " + childItem.getSubName(), Toast.LENGTH_SHORT).show();
                type = groupItem.getCode();
                subType = childItem.getSubCode();
                getCommodityData();
            }
        });
        recyclerView.setAdapter(adapter);

        // 全部商品列表
        mRecyclerView = (XRecyclerView) this.findViewById(R.id.recyclerview_all_goods);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.iconfont_downgrey);

        mRecyclerView
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
//        header = LayoutInflater.from(getActivity()).inflate(R.bottom_group_buy_person_num.fragment_goods_header, (ViewGroup) findViewById(android.R.id.content), false);
//        mRecyclerView.addHeaderView(header);
        mRecyclerView.getDefaultFootView().setLoadingHint("加载中...");
        mRecyclerView.getDefaultFootView().setNoMoreHint("没有更多数据了");
        mRecyclerView.getDefaultFootView().setMinimumHeight(100);

        adapter_allGoodsAdapter = new AllGoodsAdapter(this, list_allGoodsItem);
        adapter_allGoodsAdapter.setClickCallBack(new AllGoodsAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                String commodityId = list_allGoodsItem.get(pos).getCommodityId();
                Intent intent = new Intent(AllGoodsActivity.this, GoodsDetailActivity.class);
                intent.putExtra("commodityId", commodityId);
                intent.putExtra("classType", classType);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(adapter_allGoodsAdapter);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
//                pageNum++;
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
//                        pageNum++;
//                        getScore();
                        mRecyclerView.refreshComplete();

                    }
                }, 1000);
            }
        });

        // 创建StaggeredGridLayoutManager实例
        StaggeredGridLayoutManager sg_layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // 绑定布局管理器
        mRecyclerView.setLayoutManager(sg_layoutManager);

        if (!TextUtils.isEmpty(type)) {
            getCommodityData();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    // 获取全部商品列表
    private void getCommodityData() {
        list_allGoodsItem.clear();
        paramsMap_getCommodityData.put("classType", classType);
        paramsMap_getCommodityData.put("subType", subType);
        paramsMap_getCommodityData.put("type", type);
        paramsMap_getCommodityData.put("userId", userId);
        url_getCommodityData = Constants.baseUrl + Constants.url_getCommodityData;
        Okhttp3Utils.getAsycRequest(url_getCommodityData, paramsMap_getCommodityData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(AllGoodsActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONArray data_jsonArray = (JSONArray) response_jsonObject.getJSONArray("data");
                        String data_json_str = JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                        List<AllGoodsItem> list_data = JSONObject.parseArray(data_json_str, AllGoodsItem.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0) {
                            list_allGoodsItem.addAll(list_data);
                            LogUtils.i("--list_allGoodsItem--", "--list_allGoodsItem--" + list_allGoodsItem.size());
                            adapter_allGoodsAdapter.notifyDataSetChanged();
                        } else {
                            adapter_allGoodsAdapter.notifyDataSetChanged();
                        }
                    } else {
                        ToastUtils.showShort(AllGoodsActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(AllGoodsActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 获取商品标题
    private void getCommodityTitle() {
        paramsMap_getCommodityTitle.put("userId", userId);
        url_getCommodityTitle = Constants.baseUrl + Constants.url_getCommodityTitle;
        Okhttp3Utils.getAsycRequest(url_getCommodityTitle, paramsMap_getCommodityTitle, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(AllGoodsActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONArray data_jsonArray = (JSONArray) response_jsonObject.getJSONArray("data");
                        String data_json_str = JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                        List<GoodsTitle> list_data = JSONObject.parseArray(data_json_str, GoodsTitle.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0) {
                            list_goodsTitle.addAll(list_data);
                            for (int i = 0; i < list_data.size(); i++) {
                                JSONObject jsonObject_title = (JSONObject) data_jsonArray.get(i);
                                JSONArray data_jsonArray_sub = (JSONArray) jsonObject_title.getJSONArray("subType");
                                String data_json_str_sub = JSONObject.toJSONString(data_jsonArray_sub);//将array数组转换成字符串
                                List<GoodsTitleSub> list_data_sub = JSONObject.parseArray(data_json_str_sub, GoodsTitleSub.class);//把字符串转换成集合
                                if (list_data_sub != null && list_data_sub.size() > 0) {
                                    list_goodsTitle.get(i).setmList(list_data_sub);
                                }

                            }

                            LogUtils.i("--list_goodsTitle--", "--list_goodsTitle--" + list_goodsTitle.size());
                            initView();
                        }
//                        LogUtils.i("--list_goodsTitle--", "--list_goodsTitle--" + list_goodsTitle.get(1).getList_goodsTitleSub().size());
                    } else {
                        ToastUtils.showShort(AllGoodsActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(AllGoodsActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }
}
