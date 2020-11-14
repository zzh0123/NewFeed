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
 * 创建单独购买订单
 */
public class SingleBuyOrderActivity extends Activity implements SnappingStepperValueChangeListener, View.OnClickListener {

    private LinearLayout ll_back;

    private String classType; //

    private String userId; // 用户名
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
    private Double groupBuyPrice;
    // 最小购买数量
    private Integer capacityCount;
    // 满减
    private Integer reduceFreightCount;

    private ImageView iv_goods_img;
    private TextView tv_goods_type;
    private TextView tv_total_price;
    private SnappingStepper stepper;


    // 积分抵扣
    private SwitchButton switch_score_deduction;

    // 商品金额
    private TextView tv_goods_total_price;
    // 可使用积分
    private TextView tv_score_available;
    // 积分抵扣
    private TextView tv_score_deduction;
    // 运费
    private TextView tv_freight;
    // 最终总金额
    private TextView tv_total_price_final;
    // 提交订单
    private TextView tv_submit_order;

    // 积分抵扣接口调用
    private String url_getScoreDeductionData; // url
    private HashMap<String, String> paramsMap_getScoreDeductionData = new HashMap<String, String>();
    private Double deductionScore = 0.00; // 最多抵扣积分
    private Double userScore; // 用户积分
    private Double deductionScoreFinal = 0.00; // 抵扣的积分

    // 创建订单-单独
    private String url_createOrder; // url
    private Integer buyCount; // 购买数量

    private Double freight = 0.00; // 运费
    private Double freight1 = 0.00; // 运费
    private String receivingAddressId; // 地址id

    private Integer scoreDeductionCount = 0; // 是否积分抵扣 是否使用积分抵扣 0.否 | 1.是

    private Double total_price = 0.00; // 总金额
    private Double totalAmount = 0.00; // 最终总金额
    private Double totalAmount1 = 0.00; // 最终总金额


    // 提交订单
    private HashMap<String, Object> paramsMap_createOrder = new HashMap<String, Object>();
    private JSONObject paramsJsonObject_createOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_single_buy);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        commodityId = getIntent().getStringExtra("commodityId");
        // 配送站
        station = getIntent().getStringExtra("station");
        // 商品信息
        listImg = getIntent().getStringExtra("listImg");
        goods_name = getIntent().getStringExtra("goods_name");
        groupBuyPrice = getIntent().getDoubleExtra("groupBuyPrice", 0);
        // 最小购买数量
        capacityCount = getIntent().getIntExtra("capacityCount", 1);
        // 满减
        reduceFreightCount = getIntent().getIntExtra("reduceFreightCount", 1);
        // 运费
        freight = getIntent().getDoubleExtra("freight", 0);
        // 用户id
        userId = (String) SPUtils.get(SingleBuyOrderActivity.this, "userId", "");

        initView();
        getScoreDeductionData();
        setFinalPriceAndFreight();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        // 商品金额
        tv_goods_total_price = (TextView) findViewById(R.id.tv_goods_total_price);

        // 可使用积分
        tv_score_available = (TextView) findViewById(R.id.tv_score_available);
        // 积分抵扣
        tv_score_deduction = (TextView) findViewById(R.id.tv_score_deduction);
        // 运费
        tv_freight = (TextView) findViewById(R.id.tv_freight);
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
        stepper.setMinValue(capacityCount);
        stepper.setValue(capacityCount);
        stepper.setOnValueChangeListener(this);

        Glide.with(this).load(listImg).into(iv_goods_img);
        tv_goods_type.setText(goods_name);
        tv_total_price.setText(groupBuyPrice + "");
        total_price = groupBuyPrice * (stepper.getValue());
        tv_total_price.setText(total_price + "");
        tv_goods_total_price.setText(total_price + "");

        // 积分抵扣
        switch_score_deduction = (SwitchButton) findViewById(R.id.switch_score_deduction);
        switch_score_deduction.setClickable(false);
        switch_score_deduction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switch_score_deduction.isChecked()) {
                    tv_score_deduction.setText("-¥ " + deductionScoreFinal);
                    scoreDeductionCount = 1;
                } else {
                    tv_score_deduction.setText("");
                    scoreDeductionCount = 0;
                }

                setFinalPriceAndFreight();
            }
        });


    }

    @Override
    public void onValueChange(View view, int value) {
        switch (view.getId()) {
            case R.id.stepper:
                int stepper_value = stepper.getValue();
                if (stepper_value <= capacityCount) {
                    ToastUtils.showShort(SingleBuyOrderActivity.this, "不能再少了！");
                }
                total_price = groupBuyPrice * (stepper_value);
                tv_total_price.setText(total_price + "");
                tv_goods_total_price.setText(total_price + "");

                setFinalPriceAndFreight();
                break;
        }
    }

    private void setFinalPriceAndFreight(){
        if (stepper.getValue() >= reduceFreightCount){
            freight1 = 0.00;
            tv_freight.setText("+¥ " + freight1);
        } else {
            freight1 = freight;
            tv_freight.setText("+¥ " + freight1);
        }
        if (!switch_score_deduction.isChecked()){
            deductionScoreFinal = 0.00;
        } else {
            deductionScoreFinal = deductionScore;
        }
        totalAmount = total_price - deductionScoreFinal + freight1;
        tv_total_price_final.setText("¥ " + totalAmount);
        tv_score_deduction.setText("-¥ " + deductionScoreFinal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_address: // 地址
                if (list_data_address != null && list_data_address.size() > 0) {
                    Intent intent = new Intent(SingleBuyOrderActivity.this, ReceivingAddressActivity.class);
                    startActivityForResult(intent, 2);
                } else {
                    Intent intent = new Intent(SingleBuyOrderActivity.this, AddAddressActivity.class);
                    startActivityForResult(intent, 3);
                }
                break;
            case R.id.tv_submit_order: // 提交订单
                if(TextUtils.isEmpty(receivingAddressId)){
                    ToastUtils.showShort(SingleBuyOrderActivity.this, "请选择地址！");
                    return;
                }
                createOrder();
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
                ToastUtils.showShort(SingleBuyOrderActivity.this, "服务器未响应！");
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
                        ToastUtils.showShort(SingleBuyOrderActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(SingleBuyOrderActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 积分抵扣接口调用
    private void getScoreDeductionData() {
        paramsMap_getScoreDeductionData.put("userId", userId);
        url_getScoreDeductionData = Constants.baseUrl + Constants.url_getScoreDeductionData;
        Okhttp3Utils.getAsycRequest(url_getScoreDeductionData, paramsMap_getScoreDeductionData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(SingleBuyOrderActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        deductionScore = data_jsonObject.getDouble("deductionScore"); // 最多抵扣积分
                        userScore = data_jsonObject.getDouble("userScore"); // 用户积分
                        deductionScoreFinal = deductionScore;
                        if (userScore < deductionScore) {
                            tv_score_available.setText("您的积分为 " + userScore + ",可抵扣积分为 " + deductionScore);
                            switch_score_deduction.setClickable(false);
                        } else {
                            tv_score_available.setText("您的积分为 " + userScore + ",可抵扣积分为 " + deductionScore);
                            switch_score_deduction.setClickable(true);
                        }
                    } else {
                        ToastUtils.showShort(SingleBuyOrderActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(SingleBuyOrderActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    private void setData() {
        buyCount = stepper.getValue();
        totalAmount1 = totalAmount - freight1;
        paramsMap_createOrder.put("buyCount", buyCount); // 购买数量
        paramsMap_createOrder.put("commodityId", commodityId); // 商品id
        paramsMap_createOrder.put("freight", freight1); // 运费
        paramsMap_createOrder.put("receivingAddressId", receivingAddressId); // 地址

        paramsMap_createOrder.put("scoreDeductionCount", scoreDeductionCount); // 是否积分抵扣
        paramsMap_createOrder.put("totalAmount", totalAmount1); // 金额
        paramsMap_createOrder.put("userId", userId); // 用户id

        String json = JSON.toJSONString(paramsMap_createOrder);
        paramsJsonObject_createOrder = JSONObject.parseObject(json);
    }

    // 创建订单-单独 （orderType = "直购订单结算"）
    private void createOrder() {
        url_createOrder = Constants.baseUrl + Constants.url_createOrder;
        setData();
        Okhttp3Utils.postAsycRequest_Json(url_createOrder, paramsJsonObject_createOrder, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(SingleBuyOrderActivity.this, "服务器未响应！");
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
                        String orderType = "直购订单结算";
                        Intent intent = new Intent(SingleBuyOrderActivity.this, OrderPaymentActivity.class);
                        intent.putExtra("money", totalAmount);
                        intent.putExtra("orderNo", orderNo);
                        intent.putExtra("orderType", orderType);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.showShort(SingleBuyOrderActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(SingleBuyOrderActivity.this, "服务器返回数据为空！");
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
            }
        }
    }
}
