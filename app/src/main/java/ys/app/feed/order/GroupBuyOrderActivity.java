package ys.app.feed.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.ReceivingAddressAdapter;
import ys.app.feed.address.AddAddressActivity;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.coupon.CouponActivity;
import ys.app.feed.coupon.OrderSelectCouponActivity;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.vip.VipActivity;
import ys.app.feed.widget.snappingstepper.SnappingStepper;
import ys.app.feed.widget.snappingstepper.SnappingStepperValueChangeListener;

/**
 * 创建团购订单
 */
public class GroupBuyOrderActivity extends Activity implements SnappingStepperValueChangeListener, View.OnClickListener {

    private LinearLayout ll_back;

    private String classType; //

    // 创建团
    private String url_createGroup; // url
    private String userId; // 用户名
    private String commodityId; // 商品Id
    private String scale; // 团规模
    private HashMap<String, String> paramsMap_gcreateGroup = new HashMap<String, String>();

    private String groupNo; // 团号


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

    // 代金券
    private LinearLayout ll_cash_coupon;
    // 免拼卡
    private LinearLayout ll_no_spelling_card;
    // 积分抵扣
    private SwitchButton switch_score_deduction;

    // 商品金额
    private TextView tv_goods_total_price;
    // 代金券抵扣
    private TextView tv_cash_coupon_deduction;
    // 免拼卡使用情况
    private TextView tv_no_spelling_card;

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

    // 创建订单-团购
    private String url_createOrderGroup; // url
    private Integer buyCount; // 购买数量

    private Integer cashCouponId; // 抵扣券id
    private Double replaceMoney = 0.00; // 抵扣金额,只有抵扣券才返回
    private Integer noSpellId; // 免拼卡id
    private Double freight = 0.00; // 运费
    private Double freight1 = 0.00; // 运费
    private String receivingAddressId; // 地址id

    private Integer scoreDeductionCount = 0; // 是否积分抵扣 是否使用积分抵扣 0.否 | 1.是

    private Double total_price = 0.00; // 总金额
    private Double totalAmount = 0.00; // 最终总金额
    private Double totalAmount1 = 0.00; // 最终总金额

    private boolean isFromWeb = false;

    // 提交订单
    private HashMap<String, Object> paramsMap_createOrderGroup = new HashMap<String, Object>();
    private JSONObject paramsJsonObject_createOrderGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_group_buy);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        commodityId = getIntent().getStringExtra("commodityId");
        isFromWeb = getIntent().getBooleanExtra("isFromWeb", false);
        if (!isFromWeb){
            scale = getIntent().getStringExtra("scale");
        }
        groupNo = getIntent().getStringExtra("groupNo");
        LogUtils.i("-isFromWeb--", "--isFromWeb--" + isFromWeb);
        LogUtils.i("-groupNo--", "--groupNo--" + groupNo);
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

        if (!isFromWeb){
            // 用户id
            userId = (String) SPUtils.get(GroupBuyOrderActivity.this, "userId", "");
            gcreateGroup();
        } else {
            userId = getIntent().getStringExtra("userId");
        }

        initView();
        getScoreDeductionData();
        setFinalPriceAndFreight();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        // 商品金额
        tv_goods_total_price = (TextView) findViewById(R.id.tv_goods_total_price);
        // 代金券抵扣
        tv_cash_coupon_deduction = (TextView) findViewById(R.id.tv_cash_coupon_deduction);
        // 免拼卡使用情况
        tv_no_spelling_card = (TextView) findViewById(R.id.tv_no_spelling_card);

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

        // 代金券
        ll_cash_coupon = (LinearLayout) findViewById(R.id.ll_cash_coupon);
        ll_cash_coupon.setOnClickListener(this);
        // 免拼卡
        ll_no_spelling_card = (LinearLayout) findViewById(R.id.ll_no_spelling_card);
        ll_no_spelling_card.setOnClickListener(this);
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
                    ToastUtils.showShort(GroupBuyOrderActivity.this, "不能再少了！");
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
        totalAmount = total_price - replaceMoney - deductionScoreFinal + freight1;
        tv_total_price_final.setText("¥ " + totalAmount);

        tv_score_deduction.setText("-¥ " + deductionScoreFinal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_address: // 地址
                if (list_data_address != null && list_data_address.size() > 0) {
                    Intent intent = new Intent(GroupBuyOrderActivity.this, ReceivingAddressActivity.class);
                    if (isFromWeb){
                        intent.putExtra("isFromWeb", isFromWeb);
                        intent.putExtra("userId", userId);
                    }
                    startActivityForResult(intent, 2);
                } else {
                    Intent intent = new Intent(GroupBuyOrderActivity.this, AddAddressActivity.class);
                    if (isFromWeb){
                        intent.putExtra("isFromWeb", isFromWeb);
                        intent.putExtra("userId", userId);
                    }
                    startActivityForResult(intent, 3);
                }
                break;
            case R.id.ll_cash_coupon: // 代金券 // 优惠券类型 半价券:018001, 砍价券:018002, 免拼券:018003, 代金券:018004
                Intent intent = new Intent(GroupBuyOrderActivity.this, OrderSelectCouponActivity.class);
                intent.putExtra("type", "018004");
                startActivityForResult(intent, 4);
                break;
            case R.id.ll_no_spelling_card: // 免拼卡 // 优惠券类型 半价券:018001, 砍价券:018002, 免拼券:018003, 代金券:018004
                Intent intent1 = new Intent(GroupBuyOrderActivity.this, OrderSelectCouponActivity.class);
                intent1.putExtra("type", "018003");
                startActivityForResult(intent1, 5);
                break;
            case R.id.tv_submit_order: // 提交订单
                if (TextUtils.isEmpty(groupNo)){
                    ToastUtils.showShort(GroupBuyOrderActivity.this, "团号为空！");
                    return;
                } else if(TextUtils.isEmpty(receivingAddressId)){
                    ToastUtils.showShort(GroupBuyOrderActivity.this, "请选择地址！");
                    return;
                }
                createOrderGroup();
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    // 创建团
    private void gcreateGroup() {
        paramsMap_gcreateGroup.put("commodityId", commodityId);
        paramsMap_gcreateGroup.put("scale", scale);
        paramsMap_gcreateGroup.put("userId", userId);
        url_createGroup = Constants.baseUrl + Constants.url_createGroup;
        Okhttp3Utils.getAsycRequest(url_createGroup, paramsMap_gcreateGroup, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(GroupBuyOrderActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        groupNo = data_jsonObject.getString("groupNo");
                    } else {
                        ToastUtils.showShort(GroupBuyOrderActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(GroupBuyOrderActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 获取地址列表
    private void getReceivingAddress() {
        paramsMap_get_receiving_address.put("userId", userId);
        url_get_receiving_address = Constants.baseUrl + Constants.url_getReceivingAddressData;
        Okhttp3Utils.getAsycRequest(url_get_receiving_address, paramsMap_get_receiving_address, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(GroupBuyOrderActivity.this, "服务器未响应！");
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
                        ToastUtils.showShort(GroupBuyOrderActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(GroupBuyOrderActivity.this, "服务器返回数据为空！");
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
                ToastUtils.showShort(GroupBuyOrderActivity.this, "服务器未响应！");
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
                        ToastUtils.showShort(GroupBuyOrderActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(GroupBuyOrderActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    private void setData() {
        buyCount = stepper.getValue();
        totalAmount1 = totalAmount - freight1;
        paramsMap_createOrderGroup.put("buyCount", buyCount); // 购买数量
        paramsMap_createOrderGroup.put("cashCouponId", cashCouponId); // 抵扣券
        paramsMap_createOrderGroup.put("commodityId", commodityId); // 商品id

        paramsMap_createOrderGroup.put("freight", freight1); // 运费
        paramsMap_createOrderGroup.put("groupNo", groupNo); // 团号
        paramsMap_createOrderGroup.put("noSpellId", noSpellId); // 免拼卡

        paramsMap_createOrderGroup.put("receivingAddressId", receivingAddressId); // 地址
        paramsMap_createOrderGroup.put("scoreDeductionCount", scoreDeductionCount); // 是否积分抵扣
        paramsMap_createOrderGroup.put("totalAmount", totalAmount1); // 金额
        paramsMap_createOrderGroup.put("userId", userId); // 用户id

        String json = JSON.toJSONString(paramsMap_createOrderGroup);
        paramsJsonObject_createOrderGroup = JSONObject.parseObject(json);
    }

    // 创建订单-团购 （orderType = "团购订单结算"）
    private void createOrderGroup() {
        url_createOrderGroup = Constants.baseUrl + Constants.url_createOrderGroup;
        setData();
        Okhttp3Utils.postAsycRequest_Json(url_createOrderGroup, paramsJsonObject_createOrderGroup, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(GroupBuyOrderActivity.this, "服务器未响应！");
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
                        String orderType = "团购订单结算";
                        Intent intent = new Intent(GroupBuyOrderActivity.this, OrderPaymentActivity.class);
                        intent.putExtra("money", totalAmount);
                        intent.putExtra("orderNo", orderNo);
                        intent.putExtra("orderType", orderType);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.showShort(GroupBuyOrderActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(GroupBuyOrderActivity.this, "服务器返回数据为空！");
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
                cashCouponId = data.getIntExtra("cashCouponId", 0);
                replaceMoney = data.getDoubleExtra("replaceMoney", 0);
                tv_cash_coupon_deduction.setText("-¥ " + replaceMoney);
                setFinalPriceAndFreight();
//                LogUtils.i("--cashCouponId--", "--cashCouponId--" + cashCouponId + "--replaceMoney--" + replaceMoney);
            } else if (requestCode == 5 && resultCode == 1) { // 免拼卡
//                LogUtils.i("onActivityResult5", "--onActivityResult5");
                noSpellId = data.getIntExtra("noSpellId", 0);
                tv_no_spelling_card.setText("已使用");
//                LogUtils.i("--noSpellId--", "--noSpellId--" + noSpellId);
            }
            LogUtils.i("--onActivityResult--", "--cashCouponId--" + cashCouponId + "--noSpellId--" + noSpellId);
        }
    }
}
