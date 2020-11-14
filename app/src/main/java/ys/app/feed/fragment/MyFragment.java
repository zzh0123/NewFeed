package ys.app.feed.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.guoxiaoxing.phoenix.core.model.MediaEntity;
import com.guoxiaoxing.phoenix.picker.Phoenix;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.shaohui.bottomdialog.BottomDialog;
import ys.app.feed.R;
import ys.app.feed.adapter.AllGoodsAdapter;
import ys.app.feed.adapter.ModuleAdapter;
import ys.app.feed.adapter.MyModuleAdapter;
import ys.app.feed.adapter.ReceivingAddressAdapter;
import ys.app.feed.adapter.RecommendGoodsAdapter;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.AllGoodsItem;
import ys.app.feed.bean.ModuleItem;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.RecommendGoodsItem;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.collect.MyCollectActivity;
import ys.app.feed.comment.MyCommentActivity;
import ys.app.feed.constant.Constants;
import ys.app.feed.coupon.CouponActivity;
import ys.app.feed.goods.GoodsDetailActivity;
import ys.app.feed.invite.InviteActivity;
import ys.app.feed.invite.MyInviteActivity;
import ys.app.feed.login.LoginActivity;
import ys.app.feed.my.EditMyInfoActivity;
import ys.app.feed.order.MyOrderActivity;
import ys.app.feed.score.ScoreActivity;
import ys.app.feed.settings.SettingsActivity;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.utils.WRKShareUtil;
import ys.app.feed.vip.VipActivity;

/**
 * 我的
 */
public class MyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MyFragment";

    private View rootView, header;
    private ImageView iv_settings;

    private LinearLayout ll_score, ll_coupon;

    // header
    private ImageView iv_head;
    private TextView tv_user_name, tv_vip_date, tv_user_type;
    private TextView tv_score, tv_coupon;
    private LinearLayout ll_edit_user_info;

    // 子模块
    private RecyclerView mRecyclerView;
    private MyModuleAdapter adapter_module;
    private ArrayList<ModuleItem> list_module = new ArrayList<ModuleItem>();

    // 获取用户信息
    private String url_getUserInfo; // 获取用户信息接口url
    private String userId; // 用户id
    private HashMap<String, String> paramsMap_get_getUserInfo = new HashMap<String, String>();

    private TextView tv_renew_vip;


    private String headPortrait;
    private String name;
    private String userType;
    private String vipEndDate;
    private String phone;

    // 获取推荐列表
    private String url_getMoreRecommend; // url
    private HashMap<String, String> paramsMap_getMoreRecommend = new HashMap<String, String>();
    private ArrayList<RecommendGoodsItem> list_recommendGoodsItem = new ArrayList<RecommendGoodsItem>();
    private XRecyclerView mRecyclerView_recommend;
    private RecommendGoodsAdapter adapter_recommend;

    // 跳转团购商品详情
    private String classType = "2"; // 1.全部商品列表 | 2.团购直购列表 | 3.半价列表 | 4.积分列表 | 5.砍价列表

    private String url_share;
    private String title = "";
    private String title_sub = "";

    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the bottom_group_buy_person_num for this fragment
        rootView = inflater.inflate(R.layout.fragment_my, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 用户id
        userId = (String) SPUtils.get(getActivity(), "userId", "");
        // 推荐列表
        mRecyclerView_recommend = (XRecyclerView) getView().findViewById(R.id.recyclerview_goods_recommend);

        if (savedInstanceState == null) {
            final LayoutInflater layoutInflater = getActivity().getLayoutInflater();

            header = layoutInflater.inflate(R.layout.fragment_my_header, null);
//            View footer = layoutInflater.inflate(R.bottom_group_buy_person_num.list_item_header_footer, null);
//            TextView txtHeaderTitle = (TextView) header.findViewById(R.id.txt_title);
//            TextView txtFooterTitle = (TextView) footer.findViewById(R.id.txt_title);
//            txtHeaderTitle.setText("THE HEADER!");
//            txtFooterTitle.setText("THE FOOTER!");

            mRecyclerView_recommend.addHeaderView(header);
//            mGridView.addFooterView(footer);
        }

        mRecyclerView_recommend.setPullRefreshEnabled(false);
        mRecyclerView_recommend.setLoadingMoreEnabled(false);
        LinearLayoutManager layoutManager_recommend = new LinearLayoutManager(getActivity());
        layoutManager_recommend.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView_recommend.setLayoutManager(layoutManager_recommend);

//        mRecyclerView_recommend.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
//        mRecyclerView_recommend.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
//        mRecyclerView_recommend.setArrowImageView(R.mipmap.iconfont_downgrey);

        mRecyclerView_recommend
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
//        header = LayoutInflater.from(getActivity()).inflate(R.bottom_group_buy_person_num.fragment_goods_header, (ViewGroup) findViewById(android.R.id.content), false);
//        mRecyclerView.addHeaderView(header);
//        mRecyclerView_recommend.getDefaultFootView().setLoadingHint("加载中...");
//        mRecyclerView_recommend.getDefaultFootView().setNoMoreHint("没有更多数据了");
//        mRecyclerView_recommend.getDefaultFootView().setMinimumHeight(100);

        adapter_recommend = new RecommendGoodsAdapter(getActivity(), list_recommendGoodsItem);
        adapter_recommend.setClickCallBack(new RecommendGoodsAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                // 跳转团购商品详情
                String commodityId = list_recommendGoodsItem.get(pos).getCommodityId();
                Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
                intent.putExtra("commodityId", commodityId);
                intent.putExtra("classType", classType);
                startActivity(intent);
            }
        });
        mRecyclerView_recommend.setAdapter(adapter_recommend);


        // 创建StaggeredGridLayoutManager实例
        StaggeredGridLayoutManager sg_layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // 绑定布局管理器
        mRecyclerView_recommend.setLayoutManager(sg_layoutManager);

        iv_settings = (ImageView) header.findViewById(R.id.iv_settings);
        ll_score = (LinearLayout) header.findViewById(R.id.ll_score);
        ll_coupon = (LinearLayout) header.findViewById(R.id.ll_coupon);
        tv_renew_vip = (TextView) header.findViewById(R.id.tv_renew_vip);
        ll_edit_user_info = (LinearLayout) header.findViewById(R.id.ll_edit_user_info);
        iv_settings.setOnClickListener(this);
        ll_score.setOnClickListener(this); // 积分
        ll_coupon.setOnClickListener(this); // 优惠券
        tv_renew_vip.setOnClickListener(this); // 续费会员
        ll_edit_user_info.setOnClickListener(this); // 修改用户信息

        // header
        iv_head = (ImageView) header.findViewById(R.id.iv_head);
        tv_user_name = (TextView) header.findViewById(R.id.tv_user_name);
        tv_vip_date = (TextView) header.findViewById(R.id.tv_vip_date);
        tv_user_type = (TextView) header.findViewById(R.id.tv_user_type);
        tv_score = (TextView) header.findViewById(R.id.tv_score);
        tv_coupon = (TextView) header.findViewById(R.id.tv_coupon);

        // 子模块
        setModuleList();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 4);
//        StaggeredGridLayoutManager sg_layoutManager =
//                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerView = (RecyclerView) header.findViewById(R.id.recyclerview_module_my);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        adapter_module = new MyModuleAdapter(list_module);
        adapter_module.setClickCallBack(new MyModuleAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                String moduleName = list_module.get(pos).getModuleName();
                switch (moduleName) {
                    case "收货地址": // 收货地址
                        Intent intent = new Intent(getActivity(), ReceivingAddressActivity.class);
                        startActivity(intent);
                        break;
                    case "我的评价": // 我的评价
                        Intent intent1 = new Intent(getActivity(), MyCommentActivity.class);
                        startActivity(intent1);
                        break;
                    case "我的收藏": // 我的收藏
                        Intent intent2 = new Intent(getActivity(), MyCollectActivity.class);
                        startActivity(intent2);
                        break;
                    case "客服中心": // 客服中心
                        // 打电话
                        Intent intent3 = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:01069251500");
                        intent3.setData(data);
                        startActivity(intent3);
                        break;
                    case "邀请好友": // 邀请好友
                        Intent intent4 = new Intent(getActivity(), InviteActivity.class);
                        startActivity(intent4);
                        break;
                    case "我的邀请": // 我的邀请
                        Intent intent5 = new Intent(getActivity(), MyInviteActivity.class);
                        startActivity(intent5);
                        break;
                    case "我的订单": // 我的订单
                        Intent intent6 = new Intent(getActivity(), MyOrderActivity.class);
                        startActivity(intent6);
                        break;

                    case "抽奖券": // 抽奖券
                        title = "慧合福利一，慧合牧场营养送您一张免费抽奖券，百分之百中奖，最高可抽到200元的饲料一包，下载慧合app可用，祝您好运气";
                        title_sub = "";
                        url_share = Constants.url_shareLuckDraw;
//                        if (TextUtils.isEmpty(url_share)){
//                            ToastUtils.showShort(getActivity(), "分享链接为空！");
//                            return;
//                        }
                        // 分享
                        BottomDialog.create(getFragmentManager())
                                .setViewListener(new BottomDialog.ViewListener() {
                                    @Override
                                    public void bindView(View v) {
                                        initViewBottom(v);
                                    }
                                })
                                .setLayoutRes(R.layout.layout_share)
                                .setDimAmount(0.9f)
                                .setTag("BottomDialog")
                                .show();
                        break;

                    case "代金券": // 代金券
                        title = "慧合福利，慧合牧场营养送您一张50元代金券，价值50元人民币，更多购料优惠，请下载慧合牧场营养app";
                        title_sub = "";
                        url_share = Constants.url_shareLuckDrawCash;
//                        if (TextUtils.isEmpty(url_share)){
//                            ToastUtils.showShort(getActivity(), "分享链接为空！");
//                            return;
//                        }
                        // 分享
                        BottomDialog.create(getFragmentManager())
                                .setViewListener(new BottomDialog.ViewListener() {
                                    @Override
                                    public void bindView(View v) {
                                        initViewBottom(v);
                                    }
                                })
                                .setLayoutRes(R.layout.layout_share)
                                .setDimAmount(0.9f)
                                .setTag("BottomDialog")
                                .show();
                        break;

                    case "砍价券": // 砍价券
                        title = "0元砍到底，见证友谊的时候到";
                        title_sub = "";
                        url_share = Constants.url_shareLuckDrawBargain;
//                        if (TextUtils.isEmpty(url_share)){
//                            ToastUtils.showShort(getActivity(), "分享链接为空！");
//                            return;
//                        }
                        // 分享
                        BottomDialog.create(getFragmentManager())
                                .setViewListener(new BottomDialog.ViewListener() {
                                    @Override
                                    public void bindView(View v) {
                                        initViewBottom(v);
                                    }
                                })
                                .setLayoutRes(R.layout.layout_share)
                                .setDimAmount(0.9f)
                                .setTag("BottomDialog")
                                .show();
                        break;

                    case "免拼卡": // 免拼卡
                        title = "慧合团购免拼卡";
                        title_sub = "";
                        url_share = Constants.url_shareLuckDrawSpell;
//                        if (TextUtils.isEmpty(url_share)){
//                            ToastUtils.showShort(getActivity(), "分享链接为空！");
//                            return;
//                        }
                        // 分享
                        BottomDialog.create(getFragmentManager())
                                .setViewListener(new BottomDialog.ViewListener() {
                                    @Override
                                    public void bindView(View v) {
                                        initViewBottom(v);
                                    }
                                })
                                .setLayoutRes(R.layout.layout_share)
                                .setDimAmount(0.9f)
                                .setTag("BottomDialog")
                                .show();
                        break;

                    case "半价券": // 半价券
                        title = "慧合饲料，天天半价";
                        title_sub = "";
                        url_share = Constants.url_shareLuckDrawHalf;
//                        if (TextUtils.isEmpty(url_share)){
//                            ToastUtils.showShort(getActivity(), "分享链接为空！");
//                            return;
//                        }
                        // 分享
                        BottomDialog.create(getFragmentManager())
                                .setViewListener(new BottomDialog.ViewListener() {
                                    @Override
                                    public void bindView(View v) {
                                        initViewBottom(v);
                                    }
                                })
                                .setLayoutRes(R.layout.layout_share)
                                .setDimAmount(0.9f)
                                .setTag("BottomDialog")
                                .show();
                        break;

                    case "下载app": // 下载app
                        title = "我在用的一款购料配料电商平台，下来试试吧，半价饲料、团购饲料，又好又便宜。";
                        title_sub = "";
                        url_share = Constants.url_welfareIndex;
//                        if (TextUtils.isEmpty(url_share)){
//                            ToastUtils.showShort(getActivity(), "分享链接为空！");
//                            return;
//                        }
                        // 分享
                        BottomDialog.create(getFragmentManager())
                                .setViewListener(new BottomDialog.ViewListener() {
                                    @Override
                                    public void bindView(View v) {
                                        initViewBottom(v);
                                    }
                                })
                                .setLayoutRes(R.layout.layout_share)
                                .setDimAmount(0.9f)
                                .setTag("BottomDialog")
                                .show();
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(adapter_module);
//        mRecyclerView.setLayoutManager(gridLayoutManager);
        getUserInfo();
        getMoreRecommend();
    }

    private void setModuleList() {
        list_module.add(new ModuleItem("收货地址", R.mipmap.address));
//        list_module.add(new ModuleItem("我的评价", R.mipmap.comment));
        list_module.add(new ModuleItem("我的收藏", R.mipmap.my_collect));
        list_module.add(new ModuleItem("客服中心", R.mipmap.customer_service));

        list_module.add(new ModuleItem("邀请好友", R.mipmap.invite_friend));
        list_module.add(new ModuleItem("我的邀请", R.mipmap.my_invite));
        list_module.add(new ModuleItem("我的订单", R.mipmap.order));


    }

    private void initViewBottom(final View view) {
        RelativeLayout rl_wx_chat = (RelativeLayout) view.findViewById(R.id.rl_wx_chat); // 微信聊天
        RelativeLayout rl_wx_friend_circle = (RelativeLayout) view.findViewById(R.id.rl_wx_friend_circle); // 微信朋友圈
        rl_wx_chat.setOnClickListener(this);
        rl_wx_friend_circle.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_settings: // 设置
//                SPUtils.clear(getActivity());
//                getActivity().finish();
                Intent intent0 = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent0);
                break;
            case R.id.ll_edit_user_info: // 修改用户信息
                Intent intent4 = new Intent(getActivity(), EditMyInfoActivity.class);
                startActivityForResult(intent4, 1);
                break;
            case R.id.ll_score: // 积分
                Intent intent1 = new Intent(getActivity(), ScoreActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_coupon: // 优惠券
                Intent intent2 = new Intent(getActivity(), CouponActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_renew_vip: // 升级会员
                Intent intent3 = new Intent(getActivity(), VipActivity.class);
                intent3.putExtra("headPortrait", headPortrait);
                intent3.putExtra("name", name);
                intent3.putExtra("userType", userType);
                if ("012001".equals(userType)) { // 非会员
                } else {
                    intent3.putExtra("vipEndDate", vipEndDate);
                }
                startActivityForResult(intent3, 2);
                break;

            case R.id.rl_wx_chat: // 微信聊天
//                String title = userName + "邀请您下载使用慧合app,慧合配料购料平台，高档饲料最低价";
//                String title_sub = "";
                WRKShareUtil.getInstance(getActivity()).shareUrlToWx(url_share, title,
                        title_sub, "",
                        SendMessageToWX.Req.WXSceneSession);
                break;
            case R.id.rl_wx_friend_circle: // 微信朋友圈
//                String title1 = userName + "邀请您下载使用慧合app,慧合配料购料平台，高档饲料最低价";
//                String title_sub1 = "";
                WRKShareUtil.getInstance(getActivity()).shareUrlToWx(url_share, title,
                        title_sub, "",
                        SendMessageToWX.Req.WXSceneTimeline);
                break;
        }
    }

    // 获取用户首页信息
    private void getUserInfo() {
        paramsMap_get_getUserInfo.put("userId", userId);
        url_getUserInfo = Constants.baseUrl + Constants.url_getUserInfo;
        Okhttp3Utils.getAsycRequest(url_getUserInfo, paramsMap_get_getUserInfo, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(getActivity(), "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        String score = data_jsonObject.getString("score");
                        String couponCount = data_jsonObject.getString("couponCount");

                        name = data_jsonObject.getString("name");
                        userType = data_jsonObject.getString("userType");
                        headPortrait = data_jsonObject.getString("headPortrait");
                        vipEndDate = data_jsonObject.getString("vipEndDate");
                        String userId = data_jsonObject.getString("userId");
                        String userTypeStr = data_jsonObject.getString("userTypeStr");
                        phone = data_jsonObject.getString("phone");
                        if (TextUtils.equals("13120401694", phone)) {
                            list_module.add(new ModuleItem("抽奖券", R.mipmap.hand)); // 抽奖券
                            list_module.add(new ModuleItem("代金券", R.mipmap.hand)); // 代金券
                            list_module.add(new ModuleItem("砍价券", R.mipmap.hand)); // 砍价券

                            list_module.add(new ModuleItem("免拼卡", R.mipmap.hand)); // 免拼卡
                            list_module.add(new ModuleItem("半价券", R.mipmap.hand)); // 半价券
                            list_module.add(new ModuleItem("下载app", R.mipmap.hand)); // 下载app
                            adapter_module.notifyDataSetChanged();
                        }

                        Glide.with(getActivity()).load(headPortrait).error(R.mipmap.load_fail).into(iv_head);
                        if ("012001".equals(userType)) { // 非会员
                            tv_vip_date.setVisibility(View.GONE);
                        } else {
                            tv_vip_date.setVisibility(View.VISIBLE);
                            tv_vip_date.setText("您的会员 " + vipEndDate + " 到期");
                        }
                        tv_user_type.setText(userTypeStr);
                        tv_user_name.setText(name);
                        SPUtils.put(getActivity(), "userName", name);
                        tv_score.setText(score);
                        tv_coupon.setText(couponCount);

                    } else {
                        ToastUtils.showShort(getActivity(), resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "服务器返回数据为空！");
                }
            }
        });
    }

    // 获取用户更多推荐
    private void getMoreRecommend() {
        paramsMap_getMoreRecommend.put("userId", userId);
        url_getMoreRecommend = Constants.baseUrl + Constants.url_getMoreRecommend;
        Okhttp3Utils.getAsycRequest(url_getMoreRecommend, paramsMap_getMoreRecommend, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(getActivity(), "服务器未响应！");
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
                        List<RecommendGoodsItem> list_data = JSONObject.parseArray(data_json_str, RecommendGoodsItem.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0) {
                            list_recommendGoodsItem.addAll(list_data);
                            adapter_recommend.notifyDataSetChanged();
                        }
                    } else {
                        ToastUtils.showShort(getActivity(), resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "服务器返回数据为空！");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == 1 && resultCode == 1) {
                //返回的数据
                getUserInfo();
//                LogUtils.i("--走了--", "--走了--");
            } else if (requestCode == 2 && resultCode == 1) {
                //返回的数据
                getUserInfo();
                LogUtils.i("--走了--", "--走了--");
            }
        }
    }
}
