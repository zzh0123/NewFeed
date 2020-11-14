package ys.app.feed.address;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.adresspicker.AddressBean;
import ys.app.feed.widget.adresspicker.AreaPickerView;

public class AddAddressActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    private LinearLayout ll_city_address;
    private TextView tv_city_address;
    private AreaPickerView areaPickerView;
    private List<AddressBean> addressBeans;
    private int[] i;

    private EditText et_name, et_phone_num, et_detail_address;
    private Button bt_save;

//    area 区县 string
//    city 城市 string
//    detailAddress 详细地址 string
//    isDefault 是否默认 number 0.不默认 | 1.默认地址
//    name 用户名 string
//    phone 手机号 string
//    province 省份 string
//    userId 用户id string

    private String url_addReceivingAddress; // 添加地址接口url
    private String area, city, detailAddress, province; // 区县, 城市, 详细地址, 省份
    private String name, phone, userId; // 用户名, 手机号, 用户id
    private Integer isDefault; //  0.不默认 | 1.默认地址
    private HashMap<String, Object> paramsMap_addReceivingAddress = new HashMap<String, Object>();
    private JSONObject paramsJsonObject_addReceivingAddress;
    private ReceivingAddress receivingAddress;
    private Intent intent;

    private boolean isFromWeb = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        intent = getIntent();
        isFromWeb = getIntent().getBooleanExtra("isFromWeb", false);
        if (!isFromWeb){
            userId = (String) SPUtils.get(AddAddressActivity.this, "userId", "");
        } else {
            userId = getIntent().getStringExtra("userId");
        }

        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);

        ll_city_address = (LinearLayout) findViewById(R.id.ll_city_address);
        tv_city_address = (TextView) findViewById(R.id.tv_city_address);
        ll_back.setOnClickListener(this);
        ll_city_address.setOnClickListener(this);

        addressBeans = JSON.parseArray(getCityJson(), AddressBean.class);

        areaPickerView = new AreaPickerView(this, R.style.Dialog, addressBeans);
        areaPickerView.setAreaPickerViewCallback(new AreaPickerView.AreaPickerViewCallback() {
            @Override
            public void callback(int... value) {
                i=value;
                if (value.length == 3) {
                    tv_city_address.setText(addressBeans.get(value[0]).getLabel()
                            + "-" + addressBeans.get(value[0]).getChildren().get(value[1]).getLabel()
                            + "-" + addressBeans.get(value[0]).getChildren().get(value[1]).getChildren().get(value[2]).getLabel());
                    province = addressBeans.get(value[0]).getLabel();
                    city = addressBeans.get(value[0]).getChildren().get(value[1]).getLabel();
                    area= addressBeans.get(value[0]).getChildren().get(value[1]).getChildren().get(value[2]).getLabel();
                }
                else {
                    tv_city_address.setText(addressBeans.get(value[0]).getLabel() + "-" + addressBeans.get(value[0]).getChildren().get(value[1]).getLabel());
                }
            }
        });

        et_name = (EditText) findViewById(R.id.et_name);
        et_phone_num = (EditText) findViewById(R.id.et_phone_num);
        tv_city_address = (TextView) findViewById(R.id.tv_city_address);
        et_detail_address = (EditText) findViewById(R.id.et_detail_address);
        bt_save = (Button) findViewById(R.id.bt_save);
        bt_save.setOnClickListener(this);
    }

    private String getCityJson() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = this.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open("region.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_city_address: // 选择省市区
                areaPickerView.setSelect(i);
                areaPickerView.show();
                break;
            case R.id.bt_save: // 保存地址
                addReceivingAddress();
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    private void setData(){
        detailAddress = et_detail_address.getText().toString().trim();
        name = et_name.getText().toString().trim();
        phone = et_phone_num.getText().toString().trim();
        isDefault = 1;

        paramsMap_addReceivingAddress.put("area", area);
        paramsMap_addReceivingAddress.put("city", city);
        paramsMap_addReceivingAddress.put("detailAddress", detailAddress);
        paramsMap_addReceivingAddress.put("isDefault", isDefault);
        paramsMap_addReceivingAddress.put("name", name);
        paramsMap_addReceivingAddress.put("phone", phone);
        paramsMap_addReceivingAddress.put("province", province);
        paramsMap_addReceivingAddress.put("userId", userId);

        String json = JSON.toJSONString(paramsMap_addReceivingAddress);
        paramsJsonObject_addReceivingAddress = JSONObject.parseObject(json);
    }

    // 添加地址
    private void addReceivingAddress(){
        bt_save.setClickable(false);
        url_addReceivingAddress = Constants.baseUrl + Constants.url_addReceivingAddress;
        setData();
        Okhttp3Utils.postAsycRequest_Json(url_addReceivingAddress, paramsJsonObject_addReceivingAddress, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(AddAddressActivity.this, "服务器未响应！");
                bt_save.setClickable(true);
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(AddAddressActivity.this, "保存地址成功！");
                        bt_save.setClickable(false);

                        receivingAddress = new ReceivingAddress();
                        receivingAddress.setArea(area);
                        receivingAddress.setCity(city);
                        receivingAddress.setDetailAddress(detailAddress);
                        receivingAddress.setIsDefault(isDefault);
                        receivingAddress.setName(name);
                        receivingAddress.setPhone(phone);
                        receivingAddress.setProvince(province);
                        intent = new Intent();
                        intent.putExtra("receivingAddress", receivingAddress);
                        setResult(1, intent);
                        finish();
                    } else {
                        ToastUtils.showShort(AddAddressActivity.this, resultInfo.getMessage());
                        bt_save.setClickable(true);
                    }
                } else {
                    ToastUtils.showShort(AddAddressActivity.this, "服务器返回数据为空！");
                    bt_save.setClickable(true);
                }
            }
        });
    }
}
