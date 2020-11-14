package ys.app.feed.orderdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.util.ArrayList;
import java.util.HashMap;

import me.shaohui.bottomdialog.BottomDialog;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.GroupHeadItemAdapter;
import ys.app.feed.adapter.ModuleAdapter;
import ys.app.feed.bargain.ZeroBargainActivity;
import ys.app.feed.bean.GroupHeadItem;
import ys.app.feed.bean.ModuleItem;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.comment.CommentActivity;
import ys.app.feed.constant.Constants;
import ys.app.feed.goods.AllGoodsActivity;
import ys.app.feed.order.OrderPaymentActivity;
import ys.app.feed.utils.DateUtils;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.utils.WRKShareUtil;
import ys.app.feed.vip.VipActivity;

/**
 * 订单详情（团购）
 */
public class GroupBuyOrderDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    // 商品信息
    private ImageView iv_goods_img;
    private TextView tv_goods_type;
    private TextView tv_spec;
    private TextView tv_count;
    private Integer buyCount;

    // 订单号
    private TextView tv_order_num;
    private String orderNo;
    // 下单时间
    private TextView tv_createOrder_date;
    // 支付方式
    private TextView tv_pay_type;
    // 收货地址
    private TextView tv_receive_address;
    // 收货人
    private TextView tv_receive_user;

    // 发货仓库
    private TextView tv_delivery_warehouse;
    // 发货公司
    private TextView tv_delivery_company;

    // 还差几人
    private TextView tv_left_person_num;
    private Integer scaleCount;
    // 商品金额
    private TextView tv_goods_total_price;
    private Double totalAmount;
    // 促销
    private TextView tv_promotion;
    // 团类型
    private TextView tv_group_scale;
    // 代金券
    private TextView tv_cash_coupon_deduction;
    // 免拼卡
    private TextView tv_no_spelling_card;
    // 积分抵扣
    private TextView tv_score_deduction;
    // 运费
    private TextView tv_freight;

    // 底部按钮
    private TextView tv_delete_order; // 删除订单
    private TextView tv_invite_order; // 邀请好友
    private TextView tv_comment; // 评价
    private TextView tv_go_to_pay; // 去支付
    private TextView tv_sign; // 签收
    private TextView tv_share_order; // 分享订单


    // 获取订单详情-团购
    private String url_getOrderGroupDetail; // url
    private Integer orderId ; // 订单id
    private String type = "2"; // 订单类型
    private String status ; // 订单状态
    private HashMap<String, String> paramsMap_getOrderGroupDetail = new HashMap<String, String>();

    private String commodityName;
    private String spec;

    private String groupNo;
    private String commodityId;
    private String userId; // 用户id

    private Integer isShare = 1; // 0未分享|1.已分享

    // 删除订单
    private String url_deleteOrder; // url
    private HashMap<String, String> paramsMap_deleteOrder = new HashMap<String, String>();

    private String url; // 邀请的url
    private HashMap<String, String> paramsMap_share = new HashMap<String, String>();

    private String url_groupInvite;
    // 头像列表
    private RecyclerView mRecyclerView;
    private GroupHeadItemAdapter adapter_groupHeadItem;
    private ArrayList<GroupHeadItem> list_groupHeadItem = new ArrayList<GroupHeadItem>();

    // 订单分享加积分
    private String url_shareOrderAddScore; // url
    private HashMap<String, String> paramsMap_shareOrderAddScore = new HashMap<String, String>();

    // 签收
    private String url_changeOrderStatus; // url
    private HashMap<String, String> paramsMap_changeOrderStatus = new HashMap<String, String>();

    private Intent intent;

    private boolean isInvite = false; // 是否是邀请好友的标识

    private String userName; // 用户名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_buy_order_detail);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        intent = getIntent();

        orderId = getIntent().getIntExtra("orderId", 0);
        status = getIntent().getStringExtra("status");
       
        // 用户id
        userId = (String) SPUtils.get(GroupBuyOrderDetailActivity.this, "userId", "");
        // 用户名
        userName = (String) SPUtils.get(GroupBuyOrderDetailActivity.this, "userName", "");
        initView();
        getOrderDetail();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        // 商品信息
        iv_goods_img = (ImageView) findViewById(R.id.iv_goods_img);
        tv_goods_type = (TextView) findViewById(R.id.tv_goods_type);
        tv_spec = (TextView) findViewById(R.id.tv_spec);
        tv_count = (TextView) findViewById(R.id.tv_count);

        // 订单号
        tv_order_num = (TextView) findViewById(R.id.tv_order_num);
        // 下单时间
        tv_createOrder_date = (TextView) findViewById(R.id.tv_createOrder_date);
        // 支付方式
        tv_pay_type = (TextView) findViewById(R.id.tv_pay_type);
        // 收货地址
        tv_receive_address = (TextView) findViewById(R.id.tv_receive_address);
        // 收货人
        tv_receive_user = (TextView) findViewById(R.id.tv_receive_user);
        // 发货仓库
        tv_delivery_warehouse = (TextView) findViewById(R.id.tv_delivery_warehouse);
        // 发货公司
        tv_delivery_company = (TextView) findViewById(R.id.tv_delivery_company);

        // 还差几人
        tv_left_person_num = (TextView) findViewById(R.id.tv_left_person_num);

        // 商品金额
        tv_goods_total_price = (TextView) findViewById(R.id.tv_goods_total_price);
        // 促销
        tv_promotion = (TextView) findViewById(R.id.tv_promotion);
        // 团类型
        tv_group_scale = (TextView) findViewById(R.id.tv_group_scale);
        // 代金券
        tv_cash_coupon_deduction = (TextView) findViewById(R.id.tv_cash_coupon_deduction);
        // 免拼卡
        tv_no_spelling_card = (TextView) findViewById(R.id.tv_no_spelling_card);
        // 积分抵扣
        tv_score_deduction = (TextView) findViewById(R.id.tv_score_deduction);
        // 运费
        tv_freight = (TextView) findViewById(R.id.tv_freight);

        // 删除订单 // 底部按钮
        tv_delete_order = (TextView) findViewById(R.id.tv_delete_order);
        tv_delete_order.setOnClickListener(this);
        // 邀请好友
        tv_invite_order = (TextView) findViewById(R.id.tv_invite_order);
        tv_invite_order.setOnClickListener(this);
        if (TextUtils.equals("1", status)){
            tv_invite_order.setVisibility(View.VISIBLE);
        } else {
            tv_invite_order.setVisibility(View.GONE);
        }

        // 分享订单
        tv_share_order = (TextView) findViewById(R.id.tv_share_order);
        tv_share_order.setOnClickListener(this);

        tv_comment = (TextView) findViewById(R.id.tv_comment);
        tv_comment.setOnClickListener(this);
        if (TextUtils.equals("4", status)){
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
        // 头像列表
//        setModuleList();
        StaggeredGridLayoutManager sg_layoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_person);
        mRecyclerView.setLayoutManager(sg_layoutManager);
        adapter_groupHeadItem = new GroupHeadItemAdapter(this, list_groupHeadItem);
        mRecyclerView.setAdapter(adapter_groupHeadItem);

        // 签收
        tv_sign = (TextView) findViewById(R.id.tv_sign);
        tv_sign.setOnClickListener(this);
        if (TextUtils.equals("3", status)){
            tv_sign.setVisibility(View.VISIBLE);
        } else {
            tv_sign.setVisibility(View.GONE);
        }

    }

//    private void setModuleList() {
////        list_module.add(new ModuleItem("友料圈", R.mipmap.head));
////        list_module.add(new ModuleItem("友料圈", R.mipmap.group_buy_empty));
//        list_groupHeadItem.add(new ModuleItem("", R.mipmap.group_buy_empty));
//
//        list_groupHeadItem.add(new ModuleItem("", R.mipmap.group_buy_empty));
//        list_groupHeadItem.add(new ModuleItem("", R.mipmap.group_buy_empty));
//        list_groupHeadItem.add(new ModuleItem("", R.mipmap.group_buy_empty));
//        list_groupHeadItem.add(new ModuleItem("", R.mipmap.group_buy_empty));
//
////        list_module.add(new ModuleItem("慧合产品", R.mipmap.huihe_product));
//
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete_order: // 删除订单
//                deleteOrder();
                break;
            case R.id.tv_comment: // 评价
                Intent intent = new Intent(GroupBuyOrderDetailActivity.this, CommentActivity.class);
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
                    String orderType = "团购订单结算";
                    Intent intent1 = new Intent(GroupBuyOrderDetailActivity.this, OrderPaymentActivity.class);
                    intent1.putExtra("money", money);
                    intent1.putExtra("orderNo", orderNo);
                    intent1.putExtra("orderType", orderType);
                    startActivity(intent1);
                    finish();
                }
                break;
            case R.id.tv_invite_order: // 邀请好友
                isInvite = true;
                if (TextUtils.isEmpty(url)){
                    ToastUtils.showShort(GroupBuyOrderDetailActivity.this, "分享链接为空！");
                    return;
                }
                // 邀请好友
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
                    ToastUtils.showShort(GroupBuyOrderDetailActivity.this, "分享链接为空！");
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
                String single_price = String.format("%.2f", (totalAmount / buyCount));
                String title = null;
                String title_sub = null;
                if (!isInvite){ // 分享
                    title = userName + "在慧合牧场营养app上团购的饲料，" + commodityName + "," + "合每包" + single_price + "元，又好又省。";
                    title_sub = "慧合牧场营养，高档饲料，天天低价";
                } else {
                    title = userName + "邀请您一起拼团，" + commodityName + ",计" + single_price + "元/包，省不少，拼团成功后各加10积分";
                    title_sub = "慧合牧场营养，高档饲料，天天低价";
                }
                WRKShareUtil.getInstance(this).shareUrlToWx(url, title,
                        title_sub, "",
                        SendMessageToWX.Req.WXSceneSession);
                if (!isInvite){
                    shareOrderAddScore();
                }
                break;
            case R.id.rl_wx_friend_circle: // 微信朋友圈
                String single_price1 = String.format("%.2f", (totalAmount / buyCount));;
                String title1 = null;
                String title_sub1 = null;
                if (!isInvite){ // 分享
                    title1 = userName + "在慧合牧场营养app上团购的饲料，" + commodityName + "," + "合每包" + single_price1 + "元，又好又省。";
                    title_sub1 = "慧合牧场营养，高档饲料，天天低价";
                } else {
                    title1 = userName + "邀请您一起拼团，" + commodityName + ",计" + single_price1 + "元/包，省不少，拼团成功后各加10积分";
                    title_sub1 = "慧合牧场营养，高档饲料，天天低价";
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

    //  获取订单详情-团购
    private void getOrderDetail(){
        paramsMap_getOrderGroupDetail.put("orderId", orderId + "");
        url_getOrderGroupDetail = Constants.baseUrl + Constants.url_getOrderGroupDetail;
        Okhttp3Utils.getAsycRequest(url_getOrderGroupDetail, paramsMap_getOrderGroupDetail, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(GroupBuyOrderDetailActivity.this, "服务器未响应！");
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
                        Glide.with(GroupBuyOrderDetailActivity.this).load(listImg)
//                    .skipMemoryCache(true)
                                .into(iv_goods_img);
                        commodityName = data_jsonObject.getString("commodityName");
                        tv_goods_type.setText(commodityName);
                        spec = data_jsonObject.getString("spec");
                        tv_spec.setText(spec);
                        buyCount = data_jsonObject.getInteger("buyCount");
                        tv_count.setText(buyCount + "");

                        // 团号
                        groupNo = data_jsonObject.getString("groupNo");
                        // 商品id
                        commodityId = data_jsonObject.getString("commodityId");
                        paramsMap_share.put("groupNo", groupNo);
                        paramsMap_share.put("commodityId", commodityId);
                        paramsMap_share.put("inviteUserId", userId);
                        url = Okhttp3Utils.build_get_url(Constants.url_groupInvite, paramsMap_share);
                        LogUtils.i("--share_url--", "--share_url--" + url);
                        // 订单号
                        orderNo = data_jsonObject.getString("orderNo");
                        tv_order_num.setText(orderNo);
                        // 下单时间
                        String createDate = data_jsonObject.getString("createDate");
                        tv_createOrder_date.setText(DateUtils.stampToDate_Day(createDate));
                        // 支付方式
                        String payMode = data_jsonObject.getString("payMode");
                        tv_pay_type.setText(payMode);
                        // 收货地址
                        String province = data_jsonObject.getString("province");
                        String area = data_jsonObject.getString("area");
                        String city = data_jsonObject.getString("city");
                        String detailAddress = data_jsonObject.getString("detailAddress");
                        String phone = data_jsonObject.getString("phone");
                        String userName = data_jsonObject.getString("userName");
                        tv_receive_address.setText(province + "," + city + "," + area
                                + "," + detailAddress);
                        // 收货人
                        tv_receive_user.setText(phone + "," + userName);
                        // 发货仓库
                        String station = data_jsonObject.getString("station");
                        String manName = data_jsonObject.getString("manName");
                        String manAddress = data_jsonObject.getString("manAddress");
                        String manPhone = data_jsonObject.getString("manPhone");
                        tv_delivery_warehouse.setText(station);
                        // 发货公司
                        tv_delivery_company.setText(manAddress + "," + manName  + "," + manPhone);

                        // 还差几人
                        tv_left_person_num = (TextView) findViewById(R.id.tv_left_person_num);
                        scaleCount = data_jsonObject.getInteger("scaleCount");

                        // 头像
                        JSONArray userHeadImgs = (JSONArray) data_jsonObject.getJSONArray("userHeadImgs");
                        LogUtils.i("--userHeadImgs", "--userHeadImgs--" + userHeadImgs.size());
                        Integer userHeadImgs_size = userHeadImgs.size();
                        if (userHeadImgs != null && userHeadImgs.size() > 0) {
                            for (int i = 0; i < userHeadImgs.size(); i++) {
//                                LogUtils.i("--data_jsonArray", "--data_jsonArray--" + data_jsonArray.get(i));
                                GroupHeadItem moduleItem = new GroupHeadItem("", userHeadImgs.get(i).toString().trim());
                                list_groupHeadItem.add(moduleItem);
                            }
                            adapter_groupHeadItem.notifyDataSetChanged();
                        }
                        tv_left_person_num.setText("待邀请，还差" + (scaleCount - userHeadImgs_size) + "人");
                        // 商品金额
                        totalAmount = data_jsonObject.getDouble("totalAmount");
                        tv_goods_total_price.setText(totalAmount + "");
                        // 促销
                        String promotionGift = data_jsonObject.getString("promotionGift");
                        tv_promotion.setText(promotionGift);
                        // 团规模
                        String scaleName = data_jsonObject.getString("scaleName");
                        tv_group_scale.setText(scaleName);

                        // 代金券
                        String isCashCoupon = data_jsonObject.getString("isCashCoupon");
                        tv_cash_coupon_deduction.setText(isCashCoupon);
                        // 免拼卡
                        String isNoSpell = data_jsonObject.getString("isNoSpell");
                        tv_no_spelling_card.setText(isNoSpell);
                        // 积分抵扣
                        Double scoreDeductionCount = data_jsonObject.getDouble("scoreDeductionCount");
                        tv_score_deduction.setText(scoreDeductionCount + "");
                        // 运费
                        Double freight = data_jsonObject.getDouble("freight");
                        tv_freight.setText(freight + "");
                        // 是否分享
                        isShare = data_jsonObject.getInteger("isShare");
                        if (isShare == 0 && TextUtils.equals("4", status)){ // 0未分享|1.已分享
                            tv_share_order.setVisibility(View.VISIBLE);
                        } else {
                            tv_share_order.setVisibility(View.GONE);
                        }
                    } else {
                        ToastUtils.showShort(GroupBuyOrderDetailActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(GroupBuyOrderDetailActivity.this, "服务器返回数据为空！");
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
                ToastUtils.showShort(GroupBuyOrderDetailActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(GroupBuyOrderDetailActivity.this, "分享成功！");
                    } else {
                        ToastUtils.showShort(GroupBuyOrderDetailActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(GroupBuyOrderDetailActivity.this, "服务器返回数据为空！");
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
                ToastUtils.showShort(GroupBuyOrderDetailActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(GroupBuyOrderDetailActivity.this, "签收成功！");
                        setResult(1, intent);
                        finish();
                    } else {
                        ToastUtils.showShort(GroupBuyOrderDetailActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(GroupBuyOrderDetailActivity.this, "服务器返回数据为空！");
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

    // 删除订单
//    private void deleteOrder(){
//        paramsMap_deleteOrder.put("orderId", orderId);
//        url_deleteOrder = Constants.baseUrl + Constants.url_deleteOrder;
//        Okhttp3Utils.getAsycRequest(url_deleteOrder, paramsMap_deleteOrder, new ResultCallback() {
//            @Override
//            public void onFailure(Exception e) {
//                ToastUtils.showShort(HalfPriceOrderDetailActivity.this, "服务器未响应！");
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                if (response != null) {
//                    String str_response = response.toString();
//                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
//                    if (resultInfo.getCode() == 800) {
//                        ToastUtils.showShort(HalfPriceOrderDetailActivity.this, "删除成功！");
//                        finish();
//                    } else {
//                        ToastUtils.showShort(HalfPriceOrderDetailActivity.this, resultInfo.getMessage());
//                    }
//                } else {
//                    ToastUtils.showShort(HalfPriceOrderDetailActivity.this, "服务器返回数据为空！");
//                }
//            }
//        });
//    }
}
