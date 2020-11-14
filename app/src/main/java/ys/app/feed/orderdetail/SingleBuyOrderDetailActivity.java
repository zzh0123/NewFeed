package ys.app.feed.orderdetail;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;

import me.shaohui.bottomdialog.BottomDialog;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.invite.InviteActivity;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.ToastUtils;

/**
 * 订单详情（单独购买）
 */
public class SingleBuyOrderDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    // 商品信息
    private ImageView iv_goods_img;
    private TextView tv_goods_type;
    private TextView tv_total_price;
    private TextView tv_count;

    // 下单时间
    private TextView tv_createOrder_date;
    // 支付方式
    private TextView tv_pay_type;
    // 收货地址
    private TextView tv_receive_address;
    // 发货仓库
    private TextView tv_delivery_warehouse;

    // 商品金额
    private TextView tv_goods_total_price;
    // 积分抵扣
    private TextView tv_score_deduction;
    // 运费
    private TextView tv_freight;

    // 底部按钮
    private TextView tv_delete_order; // 删除订单
    private TextView tv_share_order; // 分享订单

    // 删除订单
    private String url_deleteOrder; // url
    private String  orderId ; // 订单id
    private HashMap<String, String> paramsMap_deleteOrder = new HashMap<String, String>();

    private String url; // 分享的url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_buy_order_detail);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        // 商品信息
        iv_goods_img = (ImageView) findViewById(R.id.iv_goods_img);
        tv_goods_type = (TextView) findViewById(R.id.tv_goods_type);
        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        tv_count = (TextView) findViewById(R.id.tv_count);

        // 下单时间
        tv_createOrder_date = (TextView) findViewById(R.id.tv_createOrder_date);
        // 支付方式
        tv_pay_type = (TextView) findViewById(R.id.tv_pay_type);
        // 收货地址
        tv_receive_address = (TextView) findViewById(R.id.tv_receive_address);
        // 发货仓库
        tv_delivery_warehouse = (TextView) findViewById(R.id.tv_delivery_warehouse);
        // 商品金额
        tv_goods_total_price = (TextView) findViewById(R.id.tv_goods_total_price);
        // 积分抵扣
        tv_score_deduction = (TextView) findViewById(R.id.tv_score_deduction);
        // 运费
        tv_freight = (TextView) findViewById(R.id.tv_freight);

        // 删除订单 // 底部按钮
        tv_delete_order = (TextView) findViewById(R.id.tv_delete_order);
        tv_delete_order.setOnClickListener(this);
        // 分享订单
        tv_share_order = (TextView) findViewById(R.id.tv_share_order);
        tv_share_order.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete_order: // 删除订单
                deleteOrder();
                break;
            case R.id.tv_share_order: // 分享订单
                if (TextUtils.isEmpty(url)){
                    ToastUtils.showShort(SingleBuyOrderDetailActivity.this, "分享链接为空！");
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

    // 删除订单
    private void deleteOrder(){
        paramsMap_deleteOrder.put("orderId", orderId);
        url_deleteOrder = Constants.baseUrl + Constants.url_deleteOrder;
        Okhttp3Utils.getAsycRequest(url_deleteOrder, paramsMap_deleteOrder, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(SingleBuyOrderDetailActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(SingleBuyOrderDetailActivity.this, "删除成功！");
                        finish();
                    } else {
                        ToastUtils.showShort(SingleBuyOrderDetailActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(SingleBuyOrderDetailActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }
}
