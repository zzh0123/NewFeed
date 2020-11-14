package ys.app.feed.orderdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;
import me.shaohui.bottomdialog.BottomDialog;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.HelpCutAdapter;
import ys.app.feed.adapter.MyInviteAdapter;
import ys.app.feed.bean.Comment;
import ys.app.feed.bean.HelpCutItem;
import ys.app.feed.bean.MyInviteItem;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.comment.CommentActivity;
import ys.app.feed.constant.Constants;
import ys.app.feed.invite.MyInviteActivity;
import ys.app.feed.order.OrderPaymentActivity;
import ys.app.feed.utils.DateUtils;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.utils.WRKShareUtil;

/**
 *  0元砍价订单详情
 */
public class ZeroBargainDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ll_back; // 返回

    // 商品信息
    private ImageView iv_goods_img;
    private TextView tv_goods_type;
    private TextView tv_spec;
    private TextView tv_count;

    // 剩余多少钱（砍价后）
    private TextView tv_left_price;
    // 剩余时间
    private CountdownView cv_countdownView_bargarin_time;
    // 邀请好友继续砍
    private TextView tv_invite_friend_zero_bargain;

    // 砍价帮
    private ArrayList<HelpCutItem> list_helpCutItem = new ArrayList<HelpCutItem>();
    private XRecyclerView recyclerview_bargain_team;
    private HelpCutAdapter adapter_help_cut;
    
    // 获取订单详情-砍价
    private String url_getOrderCutDetail; // url
    private Integer orderId ; // 订单id
    private String type = "5"; // 订单类型
    private String status; // 订单状态
    private HashMap<String, String> paramsMap_getOrderCutDetail = new HashMap<String, String>();

    private Integer isShare = 1; // 0未分享|1.已分享

    // 剩余价格
    private Double totalAmount;
    private String commodityName;

    private String url; // 分享的url
    private HashMap<String, String> paramsMap_share = new HashMap<String, String>();

    // 底部按钮
    private TextView tv_comment; // 评价
    private TextView tv_sign; // 签收
    private TextView tv_share_order; // 分享订单
    private TextView tv_go_to_pay; // 去支付

    // 订单分享加积分
    private String url_shareOrderAddScore; // url
    private String userId; // 用户名
    private HashMap<String, String> paramsMap_shareOrderAddScore = new HashMap<String, String>();

    // 签收
    private String url_changeOrderStatus; // url
    private HashMap<String, String> paramsMap_changeOrderStatus = new HashMap<String, String>();

    private Intent intent;

    private boolean isInvite = false; // 是否是邀请好友的标识

    private String commodityId;
    private String orderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zero_bargain_detail);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        intent = getIntent();

        orderId = getIntent().getIntExtra("orderId", 0);
        status = getIntent().getStringExtra("status"); //判断

        // 用户id
        userId = (String) SPUtils.get(ZeroBargainDetailActivity.this, "userId", "");

        initView();
    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        // 商品信息
        iv_goods_img = (ImageView) findViewById(R.id.iv_goods_img);
        tv_goods_type = (TextView) findViewById(R.id.tv_goods_type);
        tv_spec = (TextView) findViewById(R.id.tv_spec);
        tv_count = (TextView) findViewById(R.id.tv_count);
        // 剩余多少钱（砍价后）
        tv_left_price = (TextView) findViewById(R.id.tv_left_price);
        // 剩余时间
        cv_countdownView_bargarin_time = (CountdownView) findViewById(R.id.cv_countdownView_bargarin_time);
        // 邀请好友继续砍
        tv_invite_friend_zero_bargain = (TextView) findViewById(R.id.tv_invite_friend_zero_bargain);
        tv_invite_friend_zero_bargain.setOnClickListener(this);
        if (TextUtils.equals("0", status)){
            tv_invite_friend_zero_bargain.setText("邀请好友继续砍");
            tv_invite_friend_zero_bargain.setClickable(true);
            tv_invite_friend_zero_bargain.setBackgroundResource(R.drawable.bt_rectangle_shape_red2);
        } else {
            tv_invite_friend_zero_bargain.setText("砍价完成");
            tv_invite_friend_zero_bargain.setClickable(false);
            tv_invite_friend_zero_bargain.setBackgroundResource(R.drawable.bt_rectangle_shape_grey);
        }

        // 分享订单
        tv_share_order = (TextView) findViewById(R.id.tv_share_order);
        tv_share_order.setOnClickListener(this);

        tv_comment = (TextView) findViewById(R.id.tv_comment);
        tv_comment.setOnClickListener(this);
        if (TextUtils.equals("3", status)){
            tv_comment.setVisibility(View.VISIBLE);
        } else {
            tv_comment.setVisibility(View.GONE);
        }

        // 去支付
        tv_go_to_pay = (TextView) findViewById(R.id.tv_go_to_pay);
        tv_go_to_pay.setOnClickListener(this);
        if (TextUtils.equals("0", status)){
            tv_go_to_pay.setVisibility(View.VISIBLE);
        } else {
            tv_go_to_pay.setVisibility(View.GONE);
        }

        // 砍价帮
        recyclerview_bargain_team = (XRecyclerView) findViewById(R.id.recyclerview_bargain_team);

        recyclerview_bargain_team.setPullRefreshEnabled(false);
        recyclerview_bargain_team.setLoadingMoreEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ZeroBargainDetailActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_bargain_team.setLayoutManager(layoutManager);

//        mRecyclerView_recommend.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
//        mRecyclerView_recommend.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
//        mRecyclerView_recommend.setArrowImageView(R.mipmap.iconfont_downgrey);

        recyclerview_bargain_team
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
//        header = LayoutInflater.from(getActivity()).inflate(R.bottom_group_buy_person_num.fragment_goods_header, (ViewGroup) findViewById(android.R.id.content), false);
//        mRecyclerView.addHeaderView(header);
//        mRecyclerView_recommend.getDefaultFootView().setLoadingHint("加载中...");
//        mRecyclerView_recommend.getDefaultFootView().setNoMoreHint("没有更多数据了");
//        mRecyclerView_recommend.getDefaultFootView().setMinimumHeight(100);

        adapter_help_cut = new HelpCutAdapter(this, list_helpCutItem);

        recyclerview_bargain_team.setAdapter(adapter_help_cut);

        // 签收
        tv_sign = (TextView) findViewById(R.id.tv_sign);
        tv_sign.setOnClickListener(this);
        if (TextUtils.equals("2", status)){
            tv_sign.setVisibility(View.VISIBLE);
        } else {
            tv_sign.setVisibility(View.GONE);
        }

        getOrderCutDetail();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete_order: // 删除订单
//                deleteOrder();
                break;
            case R.id.tv_comment: // 评价
                Intent intent = new Intent(ZeroBargainDetailActivity.this, CommentActivity.class);
                intent.putExtra("commodityId", commodityId);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_sign: // 签收
                changeOrderStatus();
                break;
            case R.id.tv_go_to_pay: // 去支付
                if (totalAmount != null && totalAmount!= 0){
                    Double money = totalAmount;
                    LogUtils.i("--money--", "--money--" + money);
                    String orderType = "砍价订单结算";
                    Intent intent1 = new Intent(ZeroBargainDetailActivity.this, OrderPaymentActivity.class);
                    intent1.putExtra("money", money);
                    intent1.putExtra("orderNo", orderNo);
                    intent1.putExtra("orderType", orderType);
                    startActivity(intent1);
                    finish();
                }
                break;
            case R.id.tv_invite_friend_zero_bargain: // 邀请好友砍价
                isInvite = true;
                if (TextUtils.isEmpty(url)){
                    ToastUtils.showShort(ZeroBargainDetailActivity.this, "分享链接为空！");
                    return;
                }
                // 分享
                BottomDialog.create(getSupportFragmentManager())
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

            case R.id.tv_share_order: // 分享订单
                isInvite = false;
                HashMap<String, String> paramsMap_share = new HashMap<String, String>();
                paramsMap_share.put("orderId", orderId + "");
                paramsMap_share.put("type", type);
                url = Okhttp3Utils.build_get_url(Constants.url_share_order, paramsMap_share);
                if (TextUtils.isEmpty(url)){
                    ToastUtils.showShort(ZeroBargainDetailActivity.this, "分享链接为空！");
                    return;
                }
                // 分享
                BottomDialog.create(getSupportFragmentManager())
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

            case R.id.rl_wx_chat: // 微信聊天
                String title = null;
                String title_sub = null;
                if (!isInvite){ // 分享
                    title = "是真的！零元饲料免费拿。";
                    title_sub = "";
                } else {
                    title = "是真的！零元饲料免费拿，麻烦您帮忙点击\"砍一刀\"！另外您也可以试试零元拿饲料哟";
                    title_sub = "";
                }
                WRKShareUtil.getInstance(this).shareUrlToWx(url, title,
                        title_sub, "",
                        SendMessageToWX.Req.WXSceneSession);
                if (!isInvite){
                    shareOrderAddScore();
                }
                break;
            case R.id.rl_wx_friend_circle: // 微信朋友圈
                String title1 = null;
                String title_sub1 = null;
                if (!isInvite){ // 分享
                    title1 = "是真的！零元饲料免费拿。";
                    title_sub1 = "";
                } else {
                    title1 = "是真的！零元饲料免费拿，麻烦您帮忙点击\"砍一刀\"！另外您也可以试试零元拿饲料哟";
                    title_sub1 = "";
                }
                WRKShareUtil.getInstance(this).shareUrlToWx(url, title1,
                        title_sub1, "",
                        SendMessageToWX.Req.WXSceneTimeline);
                if (!isInvite){
                    shareOrderAddScore();
                }
                break;

            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    private void initViewBottom(final View view) {
        RelativeLayout rl_wx_chat = (RelativeLayout) view.findViewById(R.id.rl_wx_chat); // 微信聊天
        RelativeLayout rl_wx_friend_circle = (RelativeLayout) view.findViewById(R.id.rl_wx_friend_circle); // 微信朋友圈
        rl_wx_chat.setOnClickListener(this);
        rl_wx_friend_circle.setOnClickListener(this);
    }

    //  获取订单详情-砍价
    private void getOrderCutDetail(){
        paramsMap_getOrderCutDetail.put("orderId", orderId + "");
        url_getOrderCutDetail = Constants.baseUrl + Constants.url_getOrderCutDetail;
        Okhttp3Utils.getAsycRequest(url_getOrderCutDetail, paramsMap_getOrderCutDetail, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(ZeroBargainDetailActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        // 商品信息
                        String listImg = data_jsonObject.getString("listImg");
                        Glide.with(ZeroBargainDetailActivity.this).load(listImg)
                                .into(iv_goods_img);
                        commodityName = data_jsonObject.getString("commodityName");
                        tv_goods_type.setText(commodityName);
                        // 原价
                        Double directPrice = data_jsonObject.getDouble("directPrice");
                        tv_spec.setText("原价¥ " + directPrice );
                        Integer buyCount = data_jsonObject.getInteger("buyCount");
                        tv_count.setText(buyCount + "");

                        // 剩余价格
                        totalAmount = data_jsonObject.getDouble("totalAmount");
                        tv_left_price.setText("剩余 " + totalAmount + "元");
                        // 剩余时间
                        Integer second = data_jsonObject.getInteger("second");
                        long time = (long) (second * 1000);
                        cv_countdownView_bargarin_time.start(time);

                        // 订单号
                        orderNo = data_jsonObject.getString("orderNo");
                        // 商品id
                        commodityId = data_jsonObject.getString("commodityId");
                        // 订单id
                        Integer orderId = data_jsonObject.getInteger("orderId");

                        paramsMap_share.put("orderId", orderId + "");
                        url = Okhttp3Utils.build_get_url(Constants.url_share_order_helpCutInvite, paramsMap_share);
                        LogUtils.i("--share_url_cut--", "--share_url_cut--" + url);

                        // 砍价帮
                        JSONArray helpCut_jsonArray = (JSONArray) data_jsonObject.getJSONArray("helpCut");
                        String helpCut_json_str = JSONObject.toJSONString(helpCut_jsonArray);//将array数组转换成字符串
                        List<HelpCutItem> list_data = JSONObject.parseArray(helpCut_json_str, HelpCutItem.class);//把字符串转换成集合
                        // 商品评价列表
                        if (list_data != null && list_data.size() > 0) {
                            list_helpCutItem.addAll(list_data);
                            adapter_help_cut.notifyDataSetChanged();
                        }
                        // 是否分享
                        isShare = data_jsonObject.getInteger("isShare");
                        if (isShare == 0 && TextUtils.equals("3", status)){ // 0未分享|1.已分享
                            tv_share_order.setVisibility(View.VISIBLE);
                        } else {
                            tv_share_order.setVisibility(View.GONE);
                        }
                    } else {
                        ToastUtils.showShort(ZeroBargainDetailActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(ZeroBargainDetailActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 订单分享加积分
    private void shareOrderAddScore(){
        paramsMap_shareOrderAddScore.put("classType", type);
        paramsMap_shareOrderAddScore.put("orderId", orderId + "");
        paramsMap_shareOrderAddScore.put("userId", userId);
        url_shareOrderAddScore = Constants.baseUrl + Constants.url_shareOrderAddScore;
        Okhttp3Utils.getAsycRequest(url_shareOrderAddScore, paramsMap_shareOrderAddScore, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(ZeroBargainDetailActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(ZeroBargainDetailActivity.this, "分享成功！");
                    } else {
                        ToastUtils.showShort(ZeroBargainDetailActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(ZeroBargainDetailActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 签收
    private void changeOrderStatus(){
        paramsMap_changeOrderStatus.put("classType", type);
        paramsMap_changeOrderStatus.put("orderId", orderId + "");
        url_changeOrderStatus = Constants.baseUrl + Constants.url_changeOrderStatus;
        Okhttp3Utils.getAsycRequest(url_changeOrderStatus, paramsMap_changeOrderStatus, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(ZeroBargainDetailActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(ZeroBargainDetailActivity.this, "签收成功！");
                        setResult(1, intent);
                        finish();
                    } else {
                        ToastUtils.showShort(ZeroBargainDetailActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(ZeroBargainDetailActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(1, intent);
        finish();
    }
}
