package ys.app.feed.groupbuy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.hgdendi.expandablerecycleradapter.BaseExpandableRecyclerViewAdapter;
import com.hgdendi.expandablerecycleradapter.ViewProducer;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.AllGoodsAdapter;
import ys.app.feed.adapter.GoodsTitleAdapter;
import ys.app.feed.adapter.GroupBuyLaunchAdapter;
import ys.app.feed.adapter.GroupBuyTeamAdapter;
import ys.app.feed.adapter.ReceivingAddressAdapter;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.AllGoodsItem;
import ys.app.feed.bean.GoodsTitle;
import ys.app.feed.bean.GoodsTitleSub;
import ys.app.feed.bean.LaunchGroupBuyGoods;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.bean.TeamGroupBuyGoods;
import ys.app.feed.constant.Constants;
import ys.app.feed.coupon.CouponActivity;
import ys.app.feed.goods.AllGoodsActivity;
import ys.app.feed.goods.CommentFragment;
import ys.app.feed.goods.GoodsDetailActivity;
import ys.app.feed.goods.GoodsDetailFragment;
import ys.app.feed.goods.GoodsDetailWebActivity;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

/**
 * 饲料拼团
 */
public class GroupBuyActivity extends FragmentActivity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回
    private TextView tv_head_title; // 饲料拼团

    private TextView tv_launch_group_buy; // 发起拼团
    private View view_launch_group_buy; // 发起拼团

    private TextView tv_group_buy_team; // 拼团队伍
    private View view_group_buy_team; // 拼团队伍

    // 获取商品标题
    private String url_getCommodityTitle; // url
    private String userId; // 用户名
    private HashMap<String, String> paramsMap_getCommodityTitle = new HashMap<String, String>();
    private ArrayList<GoodsTitle> list_goodsTitle = new ArrayList<GoodsTitle>();
    private RecyclerView recyclerView; // 全部商品树形列表

    // 获取全部商品列表
    private String classType = "2"; // 1.全部商品列表 | 2.团购直购列表 | 3.半价列表 | 4.积分列表 | 5.砍价列表
    private String subType = ""; // 子类型
    private String type = ""; // 父类型
    private String url_getCommodityData; // url
    private HashMap<String, String> paramsMap_getCommodityData = new HashMap<String, String>();


    // 发起拼团列表
    private XRecyclerView mRecyclerView_launch;
    private ArrayList<LaunchGroupBuyGoods> list_allGoodsItem_launch = new ArrayList<LaunchGroupBuyGoods>();
    private GroupBuyLaunchAdapter adapter_launch;

    // 获取拼团队伍列表
    private String url_getGroupData; // url
    private Integer pageNum = 1; // 页
    private Integer pageSize = 10; // 行
    private HashMap<String, String> paramsMap_getGroupData = new HashMap<String, String>();
    private ArrayList<TeamGroupBuyGoods> list_allGoodsItem_team = new ArrayList<TeamGroupBuyGoods>();
    private XRecyclerView mRecyclerView_team;
    private GroupBuyTeamAdapter adapter_team;

    private Integer module;
    private boolean isLaunch = true;

    private String searchMoreGroup;

    private String groupbuy = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_buy);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        module = getIntent().getIntExtra("module", 0);
        LogUtils.i("--module--", "--module--" + module);
        searchMoreGroup = getIntent().getStringExtra("searchMoreGroup");
        LogUtils.i("--searchMoreGroup--", "--searchMoreGroup--" + searchMoreGroup);
        groupbuy = getIntent().getStringExtra("groupbuy");
        // 用户id
        userId = (String) SPUtils.get(GroupBuyActivity.this, "userId", "");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        getCommodityTitle();
    }

    private void initView() {

        tv_launch_group_buy = (TextView) findViewById(R.id.tv_launch_group_buy);
        tv_group_buy_team = (TextView) findViewById(R.id.tv_group_buy_team);

        view_launch_group_buy = (View) findViewById(R.id.view_launch_group_buy);
        view_group_buy_team = (View) findViewById(R.id.view_group_buy_team);

        tv_launch_group_buy.setOnClickListener(GroupBuyActivity.this);
        tv_group_buy_team.setOnClickListener(GroupBuyActivity.this);

        // 左侧标题列表
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

        // 发起拼团列表
        mRecyclerView_launch = (XRecyclerView) this.findViewById(R.id.recyclerview_launch_group_buy);
        mRecyclerView_launch.setPullRefreshEnabled(false);
        mRecyclerView_launch.setLoadingMoreEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView_launch.setLayoutManager(layoutManager);

        mRecyclerView_launch.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView_launch.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView_launch.setArrowImageView(R.mipmap.iconfont_downgrey);

        mRecyclerView_launch
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
//        header = LayoutInflater.from(getActivity()).inflate(R.bottom_group_buy_person_num.fragment_goods_header, (ViewGroup) findViewById(android.R.id.content), false);
//        mRecyclerView.addHeaderView(header);
        mRecyclerView_launch.getDefaultFootView().setLoadingHint("加载中...");
        mRecyclerView_launch.getDefaultFootView().setNoMoreHint("没有更多数据了");
        mRecyclerView_launch.getDefaultFootView().setMinimumHeight(100);

        adapter_launch = new GroupBuyLaunchAdapter(this, list_allGoodsItem_launch);
        adapter_launch.setClickCallBack(new GroupBuyLaunchAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                String commodityId = list_allGoodsItem_launch.get(pos).getCommodityId();
                Intent intent = new Intent(GroupBuyActivity.this, GoodsDetailActivity.class);
                intent.putExtra("commodityId", commodityId);
                intent.putExtra("classType", classType);
                startActivity(intent);
            }
        });
        mRecyclerView_launch.setAdapter(adapter_launch);

        // 创建StaggeredGridLayoutManager实例
        StaggeredGridLayoutManager sg_layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // 绑定布局管理器
        mRecyclerView_launch.setLayoutManager(sg_layoutManager);


        // 拼团队伍列表
        mRecyclerView_team = (XRecyclerView) this.findViewById(R.id.recyclerview_group_buy_team);
        mRecyclerView_team.setPullRefreshEnabled(false);
        LinearLayoutManager layoutManager_team = new LinearLayoutManager(this);
        layoutManager_team.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView_team.setLayoutManager(layoutManager_team);

        mRecyclerView_team.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView_team.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView_team.setArrowImageView(R.mipmap.iconfont_downgrey);

        mRecyclerView_team
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
//        header = LayoutInflater.from(getActivity()).inflate(R.bottom_group_buy_person_num.fragment_goods_header, (ViewGroup) findViewById(android.R.id.content), false);
//        mRecyclerView.addHeaderView(header);
        mRecyclerView_team.getDefaultFootView().setLoadingHint("加载中...");
        mRecyclerView_team.getDefaultFootView().setNoMoreHint("没有更多数据了");
        mRecyclerView_team.getDefaultFootView().setMinimumHeight(100);

        adapter_team = new GroupBuyTeamAdapter(this, list_allGoodsItem_team);
        adapter_team.setClickCallBack(new GroupBuyTeamAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                String commodityId = list_allGoodsItem_team.get(pos).getCommodityId();
                String groupNo = list_allGoodsItem_team.get(pos).getGroupNo();
                Intent intent = new Intent(GroupBuyActivity.this, GoodsDetailWebActivity.class);
                intent.putExtra("isFromWeb", false);
                intent.putExtra("commodityId", commodityId);
                intent.putExtra("userId", userId);
                intent.putExtra("groupNo", groupNo);
                startActivity(intent);
            }
        });
        mRecyclerView_team.setAdapter(adapter_team);

        mRecyclerView_team.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
//                pageNum++;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mRecyclerView_team.refreshComplete();
                    }

                }, 0);            //refresh data here
            }

            @Override
            public void onLoadMore() {
                Log.e("aaaaa", "call onLoadMore");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        pageNum++;
                        getGroupData();
                        mRecyclerView_team.refreshComplete();

                    }
                }, 1000);
            }
        });

        // 创建StaggeredGridLayoutManager实例
        StaggeredGridLayoutManager sg_layoutManager_team =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // 绑定布局管理器
        mRecyclerView_team.setLayoutManager(sg_layoutManager_team);

        if (!TextUtils.isEmpty(type)) {
            getCommodityData();
        }

//        if (TextUtils.equals("searchMoreGroup", searchMoreGroup)){
//            setChioceItem(1);
//        } else {
//            setChioceItem(0);
//        }
//
//        if (TextUtils.equals("1", groupbuy)){
//            setChioceItem(1);
//        } else {
//            setChioceItem(0);
//        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_launch_group_buy:  // 发起拼团
                setChioceItem(0);
                mRecyclerView_launch.setVisibility(View.VISIBLE);
                mRecyclerView_team.setVisibility(View.GONE);
                isLaunch = true;
                getCommodityData();
                break;
            case R.id.tv_group_buy_team:  // 拼团队伍
                setChioceItem(1);
                mRecyclerView_launch.setVisibility(View.GONE);
                mRecyclerView_team.setVisibility(View.VISIBLE);
                isLaunch = false;
                getGroupData();
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    /**
     * 设置点击选项卡的事件处理
     *
     * @param index 选项卡的标号：0, 1, 2, 3
     */
    private void setChioceItem(int index) {
        clearChioce(); // 清空, 重置选项, 隐藏所有Fragment
        switch (index) { // 0为已领取未使用,1为已使用,2为已过期
            case 0:
                tv_launch_group_buy.setTextColor(getResources().getColor(R.color.text_green));
                view_launch_group_buy.setBackgroundResource(R.color.text_green);
//                list_coupon.clear();
//                adapter_coupon.notifyDataSetChanged();
//                type = 0;
//                getCoupon();
                break;
            case 1:
                tv_group_buy_team.setTextColor(getResources().getColor(R.color.text_green));
                view_group_buy_team.setBackgroundResource(R.color.text_green);
//                list_coupon.clear();
//                adapter_coupon.notifyDataSetChanged();
//                type = 1;
//                getCoupon();
                break;
        }
    }

    /**
     * 当选中其中一个选项卡时，其他选项卡重置为默认
     */
    private void clearChioce() {
        tv_launch_group_buy.setTextColor(getResources().getColor(R.color.text_black));
        view_launch_group_buy.setBackgroundResource(R.color.white);

        tv_group_buy_team.setTextColor(getResources().getColor(R.color.text_black));
        view_group_buy_team.setBackgroundResource(R.color.white);

    }

    // 获取全部商品列表
    private void getCommodityData() {
        list_allGoodsItem_launch.clear();
        list_allGoodsItem_team.clear();

        paramsMap_getCommodityData.put("classType", classType);
        paramsMap_getCommodityData.put("subType", subType);
        paramsMap_getCommodityData.put("type", type);
        paramsMap_getCommodityData.put("userId", userId);
        url_getCommodityData = Constants.baseUrl + Constants.url_getCommodityData;
        Okhttp3Utils.getAsycRequest(url_getCommodityData, paramsMap_getCommodityData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(GroupBuyActivity.this, "服务器未响应！");
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
                        List<LaunchGroupBuyGoods> list_data = JSONObject.parseArray(data_json_str, LaunchGroupBuyGoods.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0) {
                            list_allGoodsItem_launch.addAll(list_data);
                            LogUtils.i("--list_allGoodsItem_launch--", "--list_allGoodsItem_launch--" + list_allGoodsItem_launch.size());
                        }

                        adapter_launch.notifyDataSetChanged();
                    } else {
                        ToastUtils.showShort(GroupBuyActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(GroupBuyActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 获取拼团队伍列表
    private void getGroupData(){
        list_allGoodsItem_launch.clear();
        list_allGoodsItem_team.clear();
        adapter_team.notifyDataSetChanged();

        paramsMap_getGroupData.put("pageNum", pageNum + "");
        paramsMap_getGroupData.put("pageSize", pageSize + "");
        paramsMap_getGroupData.put("subType", subType);
        paramsMap_getGroupData .put("type", type);
        url_getGroupData = Constants.baseUrl + Constants.url_getGroupData;
        Okhttp3Utils.getAsycRequest(url_getGroupData, paramsMap_getGroupData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(GroupBuyActivity.this, "服务器未响应！");
                mRecyclerView_team.refreshComplete();
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONArray data_jsonArray = (JSONArray)response_jsonObject.getJSONArray("data");
                        if (data_jsonArray != null && data_jsonArray.size() > 0){
                            LogUtils.i("--000---", "--000---");
                            String data_json_str =JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                            List<TeamGroupBuyGoods> list_data = JSONObject.parseArray(data_json_str, TeamGroupBuyGoods.class);//把字符串转换成集合
                            if (list_data != null && list_data.size() > 0){
                                list_allGoodsItem_team.addAll(list_data);
                                adapter_team.notifyDataSetChanged();
                            }
                            mRecyclerView_team.refreshComplete();
                        } else {
                            mRecyclerView_team.refreshComplete();
//                            mRecyclerView_team.setNoMore(true);
                        }

                    } else {
                        ToastUtils.showShort(GroupBuyActivity.this, resultInfo.getMessage());
                        mRecyclerView_team.refreshComplete();
                    }
                } else {
                    ToastUtils.showShort(GroupBuyActivity.this, "服务器返回数据为空！");
                    mRecyclerView_team.refreshComplete();
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
                ToastUtils.showShort(GroupBuyActivity.this, "服务器未响应！");
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
                        ToastUtils.showShort(GroupBuyActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(GroupBuyActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }
}
