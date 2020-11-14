package ys.app.feed.address;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.ReceivingAddressAdapter;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

public class ReceivingAddressActivity extends Activity implements View.OnClickListener, ReceivingAddressAdapter.InnerItemOnclickListener{

    private LinearLayout ll_back; // 返回

    private Button bt_add_receiving_address;

    // 获取地址列表
    private String url_get_receiving_address; // url
    private String userId; // 用户名
    private HashMap<String, String> paramsMap_get_receiving_address = new HashMap<String, String>();
    private ArrayList<ReceivingAddress> list_receivingAddress = new ArrayList<ReceivingAddress>();
    private ListView lv_receiving_address;
    private ReceivingAddressAdapter adapter_receivingAddress;

    // 删除地址
    private String url_delete_receiving_address; // 删除地址接口url
    private String  addressId ; // 获取列表接口,返回的id,是地址的唯一标识, 地址id
    private HashMap<String, String> paramsMap_delete_receiving_address = new HashMap<String, String>();

    // 修改默认地址
    private String url_changeDefaultAddress; // 修改默认地址接口url
    private HashMap<String, String> paramsMap_changeDefaultAddress = new HashMap<String, String>();

    private Intent intent;

    private boolean isFromWeb = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_address);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        intent = getIntent();
        isFromWeb = getIntent().getBooleanExtra("isFromWeb", false);
        if (!isFromWeb){
            // 用户id
            userId = (String) SPUtils.get(ReceivingAddressActivity.this, "userId", "");
        } else {
            userId = getIntent().getStringExtra("userId");
        }
        initView();
    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        lv_receiving_address = (ListView) findViewById(R.id.lv_receiving_address);
        bt_add_receiving_address = (Button) findViewById(R.id.bt_add_receiving_address);

        bt_add_receiving_address.setOnClickListener(this);

        adapter_receivingAddress = new ReceivingAddressAdapter(this, list_receivingAddress);
        adapter_receivingAddress.setOnInnerItemOnClickListener(this);
        lv_receiving_address.setAdapter(adapter_receivingAddress);


        getReceivingAddress();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add_receiving_address: // 新增收货地址
                Intent intent1 = new Intent(ReceivingAddressActivity.this, AddAddressActivity.class);
                startActivityForResult(intent1, 1);
                break;
            case R.id.ll_back: // 返回
                setResult(1, intent);
                finish();
                break;
        }
    }

    @Override
    public void itemClick(View v) {
        int position;
        position = (Integer) v.getTag();
        switch (v.getId()) {
            case R.id.ll_set_default:
//                Log.e("内部item--1-->", position + "");
                addressId = list_receivingAddress.get(position).getAddress_id();
                changeDefaultAddress();
                break;
            case R.id.tv_edit:
//                Log.e("内部item--2-->", position + "");
                break;
            case R.id.tv_delete:
//                Log.e("内部item--3-->", position + "");
                addressId = list_receivingAddress.get(position).getAddress_id();
                deleteAddress();
                break;
            default:
                break;
        }

    }

    // 获取地址列表
    private void getReceivingAddress(){
        paramsMap_get_receiving_address.put("userId", userId);
        url_get_receiving_address = Constants.baseUrl + Constants.url_getReceivingAddressData;
        Okhttp3Utils.getAsycRequest(url_get_receiving_address, paramsMap_get_receiving_address, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(ReceivingAddressActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONArray data_jsonArray = (JSONArray)response_jsonObject.getJSONArray("data");
                        String data_json_str =JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                        List<ReceivingAddress> list_data = JSONObject.parseArray(data_json_str, ReceivingAddress.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0){
                            list_receivingAddress.addAll(list_data);
                            adapter_receivingAddress.notifyDataSetChanged();
                        }
                    } else {
                        ToastUtils.showShort(ReceivingAddressActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(ReceivingAddressActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 删除地址
    private void deleteAddress(){
        paramsMap_delete_receiving_address.put("addressId", addressId);
        url_delete_receiving_address = Constants.baseUrl + Constants.url_deleteReceivingAddress;
        Okhttp3Utils.getAsycRequest(url_delete_receiving_address, paramsMap_delete_receiving_address, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(ReceivingAddressActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(ReceivingAddressActivity.this, "删除成功！");
                        list_receivingAddress.clear();
                        getReceivingAddress();
                    } else {
                        ToastUtils.showShort(ReceivingAddressActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(ReceivingAddressActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 修改默认地址
    private void changeDefaultAddress(){
        paramsMap_changeDefaultAddress.put("addressId", addressId); // 地址id
        paramsMap_changeDefaultAddress.put("userId", userId);
        url_changeDefaultAddress = Constants.baseUrl + Constants.url_changeDefaultAddress;
        Okhttp3Utils.getAsycRequest(url_changeDefaultAddress, paramsMap_changeDefaultAddress, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(ReceivingAddressActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(ReceivingAddressActivity.this, "设置成功！");
                        list_receivingAddress.clear();
                        getReceivingAddress();
                    } else {
                        ToastUtils.showShort(ReceivingAddressActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(ReceivingAddressActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

//    resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
            if (requestCode == 1 && resultCode == 1) {
//                ReceivingAddress receivingAddress = (ReceivingAddress) data.getSerializableExtra("receivingAddress");
//                LogUtils.i("onActivityResult", "--receivingAddress" + receivingAddress.getName());
                list_receivingAddress.clear();
                getReceivingAddress();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(1, intent);
        finish();
    }
}
