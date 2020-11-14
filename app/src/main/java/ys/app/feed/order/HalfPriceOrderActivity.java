package ys.app.feed.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.kyleduo.switchbutton.SwitchButton;

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
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.snappingstepper.SnappingStepper;
import ys.app.feed.widget.snappingstepper.SnappingStepperValueChangeListener;

/**
 * 创建半价订单
 */
public class HalfPriceOrderActivity extends Activity implements SnappingStepperValueChangeListener, View.OnClickListener {

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
    private Integer halfRestrictCount;

    private ImageView iv_goods_img;
    private TextView tv_goods_type;
    private TextView tv_total_price;
    private SnappingStepper stepper;

    // 半价券
    private LinearLayout ll_half_price_coupon;

    // 商品金额
    private TextView tv_goods_total_price;
    // 半价券抵扣
    private TextView tv_half_price_coupon_deduction;

    // 运费
    private TextView tv_halFreight;
    // 最终总金额
    private TextView tv_total_price_final;
    // 提交订单
    private TextView tv_submit_order;


    // 创建订单-半价
    private String url_createOrderHalf; // url
    private Integer buyCount; // 购买数量

    private Integer halfCouponId = null; // 半价券id
    private Double replaceMoney = 0.00; // 半价券抵扣金额
    private Double halFreight = 0.00; // 运费
    private String receivingAddressId; // 地址id

    private Integer scoreDeductionCount = 0; // 是否积分抵扣 是否使用积分抵扣 0.否 | 1.是

    private Double total_price = 0.00; // 总金额
    private Double totalAmount = 0.00; // 最终总金额


    // 提交订单
    private HashMap<String, Object> paramsMap_createOrderHalf = new HashMap<String, Object>();
    private JSONObject paramsJsonObject_createOrderHalf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_half_price);

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
        // 最小购买数量
        halfRestrictCount = getIntent().getIntExtra("halfRestrictCount", 1);
        // 运费
        halFreight = getIntent().getDoubleExtra("halFreight", 0);
        // 用户id
        userId = (String) SPUtils.get(HalfPriceOrderActivity.this, "userId", "");

        initView();

        setFinalPriceAndhalFreight();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        // 商品金额
        tv_goods_total_price = (TextView) findViewById(R.id.tv_goods_total_price);
        // 半价券抵扣
        tv_half_price_coupon_deduction = (TextView) findViewById(R.id.tv_half_price_coupon_deduction);
        // 运费
        tv_halFreight = (TextView) findViewById(R.id.tv_halFreight);
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
        stepper = (SnappingStepper) findViewById(R.id.stepper);
        stepper.setMinValue(halfRestrictCount);
        stepper.setMaxValue(halfRestrictCount);
        stepper.setValue(halfRestrictCount);
        stepper.setOnValueChangeListener(this);

        Glide.with(this).load(listImg).into(iv_goods_img);
        tv_goods_type.setText(goods_name);
        total_price = halfPrice * (stepper.getValue());
        tv_total_price.setText(total_price + "");
        tv_goods_total_price.setText(total_price + "");

        // 半价券
        ll_half_price_coupon = (LinearLayout) findViewById(R.id.ll_half_price_coupon);
        ll_half_price_coupon.setOnClickListener(this);
    }

    @Override
    public void onValueChange(View view, int value) {
        switch (view.getId()) {
            case R.id.stepper:
                int stepper_value = stepper.getValue();
                if (stepper_value <= halfRestrictCount) {
                    ToastUtils.showShort(HalfPriceOrderActivity.this, "不能再少了！");
                }
                total_price = halfPrice * (stepper_value);
                tv_total_price.setText(total_price + "");
                tv_goods_total_price.setText(total_price + "");

                setFinalPriceAndhalFreight();
                break;
        }
    }

    private void setFinalPriceAndhalFreight(){
        totalAmount = total_price + halFreight;
        tv_total_price_final.setText("¥ " + totalAmount);
        tv_halFreight.setText("+¥ " + halFreight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_address: // 地址
                if (list_data_address != null && list_data_address.size() > 0) {
                    Intent intent = new Intent(HalfPriceOrderActivity.this, ReceivingAddressActivity.class);
                    startActivityForResult(intent, 2);
                } else {
                    Intent intent = new Intent(HalfPriceOrderActivity.this, AddAddressActivity.class);
                    startActivityForResult(intent, 3);
                }
                break;
            case R.id.ll_half_price_coupon: // 半价券 // 优惠券类型 半价券:018001, 砍价券:018002, 免拼券:018003, 代金券:018004
                Intent intent = new Intent(HalfPriceOrderActivity.this, OrderSelectCouponActivity.class);
                intent.putExtra("type", "018001");
                startActivityForResult(intent, 4);
                break;

            case R.id.tv_submit_order: // 提交订单
                if(TextUtils.isEmpty(receivingAddressId)){
                    ToastUtils.showShort(HalfPriceOrderActivity.this, "请选择地址！");
                    return;
                }
                if (halfCouponId == null){
                    ToastUtils.showShort(HalfPriceOrderActivity.this, "请先选择半价券！");
                    return;
                }
                createOrderHalf();
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
                ToastUtils.showShort(HalfPriceOrderActivity.this, "服务器未响应！");
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
                        ToastUtils.showShort(HalfPriceOrderActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(HalfPriceOrderActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    private void setData() {
        buyCount = stepper.getValue();

        paramsMap_createOrderHalf.put("buyCount", buyCount); // 购买数量
        paramsMap_createOrderHalf.put("commodityId", commodityId); // 商品id
        paramsMap_createOrderHalf.put("freight", halFreight); // 运费
        paramsMap_createOrderHalf.put("halfCouponId", halfCouponId); // 半价券
        paramsMap_createOrderHalf.put("receivingAddressId", receivingAddressId); // 地址
        paramsMap_createOrderHalf.put("totalAmount", total_price); // 金额
        paramsMap_createOrderHalf.put("userId", userId); // 用户id

        String json = JSON.toJSONString(paramsMap_createOrderHalf);
        paramsJsonObject_createOrderHalf = JSONObject.parseObject(json);
    }

    // 创建订单-半价 （orderType = " 半价订单结算"）
    private void createOrderHalf() {
        url_createOrderHalf = Constants.baseUrl + Constants.url_createOrderHalf;
        setData();
        Okhttp3Utils.postAsycRequest_Json(url_createOrderHalf, paramsJsonObject_createOrderHalf, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(HalfPriceOrderActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");;
                        String orderNo = data_jsonObject.getString("orderNo");
                        String orderType = "半价订单结算";
                        Intent intent = new Intent(HalfPriceOrderActivity.this, OrderPaymentActivity.class);
                        intent.putExtra("money", totalAmount);
                        intent.putExtra("orderNo", orderNo);
                        intent.putExtra("orderType", orderType);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.showShort(HalfPriceOrderActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(HalfPriceOrderActivity.this, "服务器返回数据为空！");
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
                halfCouponId = data.getIntExtra("halfCouponId", 0);
                tv_half_price_coupon_deduction.setText("已选择半价券，已优惠 ¥ " + total_price);
                setFinalPriceAndhalFreight();
//                LogUtils.i("--cashCouponId--", "--cashCouponId--" + cashCouponId + "--replaceMoney--" + replaceMoney);
            }
            LogUtils.i("--onActivityResult--", "--halfCouponId--" + halfCouponId);
        }
    }
}
