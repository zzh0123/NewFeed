package ys.app.feed.vip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.donkingliang.labels.LabelsView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.ModuleAdapter;
import ys.app.feed.adapter.VipPackageAdapter;
import ys.app.feed.adapter.VipTypeAdapter;
import ys.app.feed.bean.GroupPersonNumItem;
import ys.app.feed.bean.ModuleItem;
import ys.app.feed.bean.ModuleVipPackageItem;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.bean.VipTypeItem;
import ys.app.feed.constant.Constants;
import ys.app.feed.order.OrderPaymentActivity;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

public class VipActivity extends Activity implements View.OnClickListener {

    private Button bt_back;

    // 会员套餐
    private RecyclerView mRecyclerView;
    private VipPackageAdapter adapter_module_vip_package;
    private ArrayList<ModuleVipPackageItem> list_module_vip_package = new ArrayList<ModuleVipPackageItem>();
    private JSONArray data_jsonArray_module_vip_package;

    private TextView tv_total_price, tv_renew_vip_now;
    private String total_price;

    // 获取会员类型,金额
    private String url_getMemberType; // 获取会员类型,金额url
    private HashMap<String, String> paramsMap_getMemberType = new HashMap<String, String>();

    // 判断用户是否可以购买体验会员
    private String url_isBuyMember; // url
    private String userId; // 用户名
    private HashMap<String, String> paramsMap_isBuyMember = new HashMap<String, String>();

    // 创建会员订单
    private String url_addMemberOrder; // url
    private HashMap<String, String> paramsMap_addMemberOrder = new HashMap<String, String>();
    private String orderNo, money;
    private String memberCode;
    private String orderType = "会员充值";

    // 用户信息
    private ImageView iv_head;
    private TextView tv_user_name, tv_vip_date, tv_user_type;

    private String headPortrait;
    private String name;
    private String userType;
    private String vipEndDate;

    // 会员权益
    private RecyclerView recyclerview_vip_type;
    private ArrayList<VipTypeItem> list_vipTypeItem = new ArrayList<VipTypeItem>();
    private VipTypeAdapter adapter_vip_type;
    private String vipTypeCode;
    private ImageView iv_rights;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        intent = getIntent();

        headPortrait = getIntent().getStringExtra("headPortrait");
        name = getIntent().getStringExtra("name");
        userType = getIntent().getStringExtra("userType");
        vipEndDate = getIntent().getStringExtra("vipEndDate");

        initView();
//        wxpay();
        getMemberType();
    }

    private void initView() {
        // 用户id
        userId = (String) SPUtils.get(VipActivity.this, "userId", "");

//        alipay();
        bt_back = (Button) findViewById(R.id.bt_back);
        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        tv_renew_vip_now = (TextView) findViewById(R.id.tv_renew_vip_now);
        tv_renew_vip_now.setOnClickListener(this);
        bt_back.setOnClickListener(this);

        // 会员权益
        iv_rights = (ImageView) findViewById(R.id.iv_rights);
        setVipTypeList();
        StaggeredGridLayoutManager sg_layoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerview_vip_type = (RecyclerView) findViewById(R.id.recyclerview_vip_type);
        recyclerview_vip_type.setLayoutManager(sg_layoutManager);
        adapter_vip_type = new VipTypeAdapter(this, list_vipTypeItem);
        adapter_vip_type.setClickCallBack(new VipTypeAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                for (int i = 0; i < list_vipTypeItem.size(); i++) {
                    list_vipTypeItem.get(i).setSelected(false);
                }
                list_vipTypeItem.get(pos).setSelected(true);
                vipTypeCode = list_vipTypeItem.get(pos).getCode();
                adapter_vip_type.notifyDataSetChanged();

                setImage(vipTypeCode);
            }
        });
        recyclerview_vip_type.setAdapter(adapter_vip_type);

        LogUtils.i("--list_vipTypeItem--" , "--list_vipTypeItem--" + list_vipTypeItem.size());
        list_vipTypeItem.get(0).setSelected(true);
        vipTypeCode = list_vipTypeItem.get(0).getCode();
        setImage(vipTypeCode);

        // 会员套餐
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_module_vip);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter_module_vip_package = new VipPackageAdapter(this, list_module_vip_package);
        adapter_module_vip_package.setClickCallBack(new VipPackageAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                for (int i = 0; i < list_module_vip_package.size(); i++) {
                    list_module_vip_package.get(i).setChecked(false);
                }
                list_module_vip_package.get(pos).setChecked(true);
                memberCode = list_module_vip_package.get(pos).getCode();
                total_price = list_module_vip_package.get(pos).getMoney();
                tv_total_price.setText(total_price);
                adapter_module_vip_package.notifyDataSetChanged();
            }
        });
        mRecyclerView.setAdapter(adapter_module_vip_package);

        // 用户信息
        // header
        iv_head = (ImageView) findViewById(R.id.iv_head);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_vip_date = (TextView) findViewById(R.id.tv_vip_date);
        tv_user_type = (TextView) findViewById(R.id.tv_user_type);

        Glide.with(this).load(headPortrait).error(R.mipmap.load_fail).into(iv_head);
        tv_user_name.setText(name);
        if (TextUtils.equals("012001", userType)){ // 非会员 "012001".equals(userType)
            tv_vip_date.setVisibility(View.GONE);
        } else {
            tv_vip_date.setVisibility(View.VISIBLE);
            tv_vip_date.setText("您的会员 " + vipEndDate + " 到期");
        }
    }

    private void setVipTypeList(){
        VipTypeItem vipTypeItem = new VipTypeItem();
        vipTypeItem.setName("体验会员");
        vipTypeItem.setCode("0");
        vipTypeItem.setSelected(false);
        list_vipTypeItem.add(vipTypeItem);

        VipTypeItem vipTypeItem1 = new VipTypeItem();
        vipTypeItem1.setName("月度会员");
        vipTypeItem1.setCode("1");
        vipTypeItem1.setSelected(false);
        list_vipTypeItem.add(vipTypeItem1);

        VipTypeItem vipTypeItem2 = new VipTypeItem();
        vipTypeItem2.setName("季度会员");
        vipTypeItem2.setCode("2");
        vipTypeItem2.setSelected(false);
        list_vipTypeItem.add(vipTypeItem2);

        VipTypeItem vipTypeItem3 = new VipTypeItem();
        vipTypeItem3.setName("半年会员");
        vipTypeItem3.setCode("3");
        vipTypeItem3.setSelected(false);
        list_vipTypeItem.add(vipTypeItem3);

        VipTypeItem vipTypeItem4 = new VipTypeItem();
        vipTypeItem4.setName("年度会员");
        vipTypeItem4.setCode("4");
        vipTypeItem4.setSelected(false);
        list_vipTypeItem.add(vipTypeItem4);
    }
    private void setImage(String code){
        switch (code){
            case "0":
                iv_rights.setImageResource(R.mipmap.rights0);
                break;
            case "1":
                iv_rights.setImageResource(R.mipmap.rights1);
                break;
            case "2":
                iv_rights.setImageResource(R.mipmap.rights2);
                break;
            case "3":
                iv_rights.setImageResource(R.mipmap.rights3);
                break;
            case "4":
                iv_rights.setImageResource(R.mipmap.rights4);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_renew_vip_now: // 立即续费
//                Intent intent = new Intent(VipActivity.this, AlipayActivity.class);
//                intent.putExtra("orderNo", orderNo);
//                startActivity(intent);
                addMemberOrder();
                break;
            case R.id.bt_back: // 返回
                setResult(1, intent);
                finish();
                break;
        }
    }

    // 获取会员类型,金额
    private void getMemberType() {
        url_getMemberType = Constants.baseUrl + Constants.url_getMemberType;
        Okhttp3Utils.getAsycRequest(url_getMemberType, paramsMap_getMemberType, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(VipActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        data_jsonArray_module_vip_package = (JSONArray) response_jsonObject.getJSONArray("data");
//                        String data_json_str = JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
//                        list_module_vip_package_result = JSONObject.parseArray(data_json_str, ModuleVipPackageItem.class);//把字符串转换成集合
                        if (data_jsonArray_module_vip_package != null && data_jsonArray_module_vip_package.size() > 0) {
                            isBuyMember();
                        }
                    } else {
                        ToastUtils.showShort(VipActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(VipActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 判断用户是否可以购买体验会员
    private void isBuyMember() {
        paramsMap_isBuyMember.put("userId", userId);
        url_isBuyMember = Constants.baseUrl + Constants.url_isBuyMember;
        Okhttp3Utils.getAsycRequest(url_isBuyMember, paramsMap_isBuyMember, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(VipActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        Integer isBuyMember = data_jsonObject.getInteger("isBuyMember");

                        if (isBuyMember == 0) { // 0.可以购买体验会员|1.无法购买体验会员
                            for (int i = 0; i < data_jsonArray_module_vip_package.size(); i++) {
                                JSONObject data_jsonObject1 = data_jsonArray_module_vip_package.getJSONObject(i);
                                ModuleVipPackageItem moduleVipPackageItem = new ModuleVipPackageItem();
                                moduleVipPackageItem.setCode(data_jsonObject1.getString("code"));
                                moduleVipPackageItem.setMoney(data_jsonObject1.getString("money"));
                                moduleVipPackageItem.setName(data_jsonObject1.getString("name"));
                                list_module_vip_package.add(moduleVipPackageItem);
                            }
                            list_module_vip_package.get(0).setChecked(true);
                            memberCode = list_module_vip_package.get(0).getCode();
                            total_price = list_module_vip_package.get(0).getMoney();
                            tv_total_price.setText(total_price);
                        } else {
                            for (int i = 0; i < data_jsonArray_module_vip_package.size(); i++) {
                                JSONObject data_jsonObject1 = data_jsonArray_module_vip_package.getJSONObject(i);
                                if ("体验会员".equals(data_jsonObject1.getString("name").trim())) {
                                    continue;
                                }
                                ModuleVipPackageItem moduleVipPackageItem = new ModuleVipPackageItem();
                                moduleVipPackageItem.setCode(data_jsonObject1.getString("code"));
                                moduleVipPackageItem.setMoney(data_jsonObject1.getString("money"));
                                moduleVipPackageItem.setName(data_jsonObject1.getString("name"));
                                list_module_vip_package.add(moduleVipPackageItem);
                            }
                            list_module_vip_package.get(0).setChecked(true);
                            memberCode = list_module_vip_package.get(0).getCode();
                            total_price = list_module_vip_package.get(0).getMoney();
                            tv_total_price.setText(total_price);
                        }
                        adapter_module_vip_package.notifyDataSetChanged();

                    } else {
                        ToastUtils.showShort(VipActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(VipActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 创建会员订单
    private void addMemberOrder() {
        paramsMap_addMemberOrder.put("memberCode", memberCode);
        paramsMap_addMemberOrder.put("userId", userId);
        url_addMemberOrder = Constants.baseUrl + Constants.url_addMemberOrder;
        Okhttp3Utils.getAsycRequest(url_addMemberOrder, paramsMap_addMemberOrder, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(VipActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    orderNo = str_response;
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        Double money = data_jsonObject.getDouble("money");
                        orderNo = data_jsonObject.getString("orderNo");
                        LogUtils.i("--money--", "--money--" + money);
                        Intent intent = new Intent(VipActivity.this, OrderPaymentActivity.class);
                        intent.putExtra("money", money);
                        intent.putExtra("orderNo", orderNo);
                        intent.putExtra("orderType", orderType);
                        startActivity(intent);
//                        alipay();
//                        wxpay();
                    } else {
                        ToastUtils.showShort(VipActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(VipActivity.this, "服务器返回数据为空！");
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
