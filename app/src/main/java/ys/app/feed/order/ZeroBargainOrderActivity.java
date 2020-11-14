package ys.app.feed.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.address.AddAddressActivity;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.coupon.OrderSelectCouponActivity;
import ys.app.feed.orderdetail.ZeroBargainDetailActivity;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.snappingstepper.SnappingStepper;
import ys.app.feed.widget.snappingstepper.SnappingStepperValueChangeListener;

/**
 * 创建0元砍价订单
 */
public class ZeroBargainOrderActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_back;

    private String classType; //

    private String userId; // 用户id
    private String commodityId; // 商品Id

    // 获取地址列表
    private TextView tv_address;
    private LinearLayout ll_address;
    private String url_get_receiving_address; // url
    private HashMap<String, String> paramsMap_get_receiving_address = new HashMap<String, String>();
    private List<ReceivingAddress> list_data_address;

    private TextView tv_name_phone;
    // 配送站
    private String station;
    private TextView tv_station;

    // 商品信息
    private String listImg;
    private String goods_name;
    private Double halfPrice;
    // 限购购买数量
    private Integer zeroRestrictCount = 1;

    private ImageView iv_goods_img;
    private TextView tv_goods_type;
    private TextView tv_total_price;
    private TextView tv_count;


    // 砍价券
    private LinearLayout ll_zero_bargain_coupon;

    // 商品金额
    private TextView tv_goods_total_price;
    // 砍价券抵扣
    private TextView tv_zero_bargain_deduction;

    // 最终总金额
    private TextView tv_total_price_final;
    // 提交订单
    private TextView tv_submit_order;


    // 创建订单-半价
    private String url_createOrderCut; // url
    private Integer buyCount = 1; // 购买数量

    private Integer cutCouponId = null; // 砍价券id
    private Double replaceMoney = 0.00; // 半价券抵扣金额
    private Double zeroBargainFreight = 0.00; // 运费
    private String receivingAddressId; // 地址id

    private Integer scoreDeductionCount = 0; // 是否积分抵扣 是否使用积分抵扣 0.否 | 1.是

    private Double total_price = 0.00; // 总金额
    private Double totalAmount = 0.00; // 最终总金额
    private Double price; // 原价

    // 提交订单
    private HashMap<String, Object> paramsMap_createOrderCut= new HashMap<String, Object>();
    private JSONObject paramsJsonObject_createOrderCut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_zero_bargain);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        commodityId = getIntent().getStringExtra("commodityId");
        // 配送站
        station = getIntent().getStringExtra("station");
        // 商品信息
        listImg = getIntent().getStringExtra("listImg");
        goods_name = getIntent().getStringExtra("goods_name");
        halfPrice = getIntent().getDoubleExtra("halfPrice", 0);
        LogUtils.i("--halfPrice--", "--halfPrice--" + halfPrice);
        price = getIntent().getDoubleExtra("price", 0);
        totalAmount = price;
        // 用户id
        userId = (String) SPUtils.get(ZeroBargainOrderActivity.this, "userId", "");

        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        // 商品金额
        tv_goods_total_price = (TextView) findViewById(R.id.tv_goods_total_price);
        // 砍价券抵扣
        tv_zero_bargain_deduction = (TextView) findViewById(R.id.tv_zero_bargain_deduction);
        // 最终总金额
        tv_total_price_final = (TextView) findViewById(R.id.tv_total_price_final);
        // 提交订单
        tv_submit_order = (TextView) findViewById(R.id.tv_submit_order);
        tv_submit_order.setOnClickListener(this);

        // 地址
        ll_address = (LinearLayout) findViewById(R.id.ll_address);
        ll_address.setOnClickListener(this);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_name_phone = (TextView) findViewById(R.id.tv_name_phone);
        getReceivingAddress();
        // 配送站
        tv_station = (TextView) findViewById(R.id.tv_station);
        tv_station.setText(station);
        // 商品信息
        iv_goods_img = (ImageView) findViewById(R.id.iv_goods_img);
        tv_goods_type = (TextView) findViewById(R.id.tv_goods_type);
        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_count.setText("1");

        Glide.with(this).load(listImg).into(iv_goods_img);
        tv_goods_type.setText(goods_name);
        tv_total_price.setText(total_price + "");
        tv_goods_total_price.setText(total_price + "");
        tv_total_price_final.setText(total_price + "");

        // 砍价券
        ll_zero_bargain_coupon = (LinearLayout) findViewById(R.id.ll_zero_bargain_coupon);
        ll_zero_bargain_coupon.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_address: // 地址
                if (list_data_address != null && list_data_address.size() > 0) {
                    Intent intent = new Intent(ZeroBargainOrderActivity.this, ReceivingAddressActivity.class);
                    startActivityForResult(intent, 2);
                } else {
                    Intent intent = new Intent(ZeroBargainOrderActivity.this, AddAddressActivity.class);
                    startActivityForResult(intent, 3);
                }
                break;
            case R.id.ll_zero_bargain_coupon: // 砍价券 // 优惠券类型 半价券:018001, 砍价券:018002, 免拼券:018003, 代金券:018004
                Intent intent = new Intent(ZeroBargainOrderActivity.this, OrderSelectCouponActivity.class);
                intent.putExtra("type", "018002");
                startActivityForResult(intent, 4);
                break;

            case R.id.tv_submit_order: // 提交订单
                if(TextUtils.isEmpty(receivingAddressId)){
                    ToastUtils.showShort(ZeroBargainOrderActivity.this, "请选择地址！");
                    return;
                }
                if (cutCouponId == null){
                    ToastUtils.showShort(ZeroBargainOrderActivity.this, "请先选择砍价券！");
                    return;
                }
                createOrderCut();
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    // 获取地址列表
    private void getReceivingAddress() {
        paramsMap_get_receiving_address.put("userId", userId);
        url_get_receiving_address = Constants.baseUrl + Constants.url_getReceivingAddressData;
        Okhttp3Utils.getAsycRequest(url_get_receiving_address, paramsMap_get_receiving_address, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(ZeroBargainOrderActivity.this, "服务器未响应！");
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
                        list_data_address = JSONObject.parseArray(data_json_str, ReceivingAddress.class);//把字符串转换成集合
                        if (list_data_address != null && list_data_address.size() > 0) {
                            String default_address = list_data_address.get(0).getCity() + list_data_address.get(0).getArea()
                                    + list_data_address.get(0).getDetailAddress();
                            String name = list_data_address.get(0).getName();
                            String phone = list_data_address.get(0).getPhone();
                            tv_address.setText(default_address);
                            tv_name_phone.setText(name + " " + phone);
                            receivingAddressId = list_data_address.get(0).getAddress_id();
                        } else {
                            tv_address.setText("请点击添加收货地址");
                        }
                    } else {
                        ToastUtils.showShort(ZeroBargainOrderActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(ZeroBargainOrderActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    private void setData() {

        paramsMap_createOrderCut.put("buyCount", buyCount); // 购买数量
        paramsMap_createOrderCut.put("commodityId", commodityId); // 商品id
        paramsMap_createOrderCut.put("cutCouponId", cutCouponId); // 砍价券id
        paramsMap_createOrderCut.put("freight", zeroBargainFreight); // 运费

        paramsMap_createOrderCut.put("receivingAddressId", receivingAddressId); // 地址
        paramsMap_createOrderCut.put("totalAmount", totalAmount); // 金额
        paramsMap_createOrderCut.put("userId", userId); // 用户id

        String json = JSON.toJSONString(paramsMap_createOrderCut);
        paramsJsonObject_createOrderCut = JSONObject.parseObject(json);
    }

    // 创建订单-半价 （orderType = " 砍价订单结算"）
    private void createOrderCut() {
        url_createOrderCut = Constants.baseUrl + Constants.url_createOrderCut;
        setData();
        Okhttp3Utils.postAsycRequest_Json(url_createOrderCut, paramsJsonObject_createOrderCut, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(ZeroBargainOrderActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        // 砍价不用支付，跳转订单详情
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");;
                        Integer orderId = data_jsonObject.getInteger("id");
                        Intent intent = new Intent(ZeroBargainOrderActivity.this, ZeroBargainDetailActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("status", "0");
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.showShort(ZeroBargainOrderActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(ZeroBargainOrderActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
            if (requestCode == 2 && resultCode == 1) {
//                ReceivingAddress receivingAddress = (ReceivingAddress) data.getSerializableExtra("receivingAddress");
                LogUtils.i("onActivityResult2", "--onActivityResult2");
                getReceivingAddress();
            } else if (requestCode == 3 && resultCode == 1) {
                LogUtils.i("onActivityResult3", "--onActivityResult3");
                getReceivingAddress();
            } else if (requestCode == 4 && resultCode == 1) { // 代金券
//                LogUtils.i("onActivityResult4", "--onActivityResult4");
                cutCouponId = data.getIntExtra("cutCouponId", 0);
                tv_zero_bargain_deduction.setText("已选择砍价券");
//                LogUtils.i("--cashCouponId--", "--cashCouponId--" + cashCouponId + "--replaceMoney--" + replaceMoney);
            }
            LogUtils.i("--onActivityResult--", "--cutCouponId--" + cutCouponId);
        }
    }
}
