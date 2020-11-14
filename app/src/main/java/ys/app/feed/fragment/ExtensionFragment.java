package ys.app.feed.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.util.HashMap;

import ys.app.feed.R;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.extension.ExtensionActivity;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

/**
 * 推广
 */
public class ExtensionFragment extends Fragment implements View.OnClickListener{


    private View rootView, header;
    private LinearLayout ll_back; // 返回

    // 用户信息
    private ImageView iv_head;
    private TextView tv_user_name;
    private TextView tv_today_invite_vip;
    private TextView tv_today_invite_group;
    private TextView tv_today_get_coin;
    private TextView tv_total_goldCoin;

    private String headPortrait;
    private String name;

    private String inviteVipCount;
    private String inviteGroupCount;
    private String get_coin;


    // 获取推广中心数据
    private String url_getExtensionData; // url
    private String userId; // 用户名
    private HashMap<String, String> paramsMap_getExtensionData = new HashMap<String, String>();


    // 申请提现
    private String url_launchCashCash; // url
    private HashMap<String, String> paramsMap_launchCashCash = new HashMap<String, String>();
    private Double carryMoney;
    // 申请提现
    private Button bt_apply_cash_out;

    private EditText et_cash;

    public ExtensionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the bottom_group_buy_person_num for this fragment
        rootView = inflater.inflate(R.layout.activity_extension, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ll_back = (LinearLayout) rootView.findViewById(R.id.ll_back);
        ll_back.setVisibility(View.INVISIBLE);

        // 用户信息
        iv_head = (ImageView) rootView.findViewById(R.id.iv_head);
        tv_user_name = (TextView) rootView.findViewById(R.id.tv_user_name);



        tv_today_invite_vip = (TextView) rootView.findViewById(R.id.tv_today_invite_vip);
        tv_today_invite_group = (TextView) rootView.findViewById(R.id.tv_today_invite_group);
        tv_today_get_coin = (TextView) rootView.findViewById(R.id.tv_today_get_coin);
        tv_total_goldCoin = (TextView) rootView.findViewById(R.id.tv_total_goldCoin);

        // 申请提现
        bt_apply_cash_out = (Button) rootView.findViewById(R.id.bt_apply_cash_out);
        bt_apply_cash_out.setOnClickListener(this);

        et_cash = (EditText) rootView.findViewById(R.id.et_cash);
        getExtensionData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_apply_cash_out: // 申请提现
                launchCashCash();
                break;
        }
    }

    // 获取推广中心数据
    private void getExtensionData(){
        // 用户id
        userId = (String) SPUtils.get(getActivity(), "userId", "");
        paramsMap_getExtensionData.put("userId", userId);
        url_getExtensionData = Constants.baseUrl + Constants.url_getExtensionData;
        Okhttp3Utils.getAsycRequest(url_getExtensionData, paramsMap_getExtensionData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(getActivity(), "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        Integer inviteUserCount = data_jsonObject.getInteger("inviteUserCount");
                        String name = data_jsonObject.getString("name");
                        Double GroupBuyCount = data_jsonObject.getDouble("groupBuyCount");
                        String headPortrait = data_jsonObject.getString("headPortrait");
                        Integer dayGetGold = data_jsonObject.getInteger("dayGetGold");
                        Double goldCoin = data_jsonObject.getDouble("goldCoin");

                        Glide.with(getActivity()).load(headPortrait).error(R.mipmap.load_fail).into(iv_head);
                        tv_user_name.setText(name);

                        tv_today_invite_vip.setText(inviteUserCount + "");
                        tv_today_invite_group.setText(GroupBuyCount + "");
                        tv_today_get_coin.setText(dayGetGold + "");
                        tv_total_goldCoin.setText("总金币： " + goldCoin);

                    } else {
                        ToastUtils.showShort(getActivity(), resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "服务器返回数据为空！");
                }
            }
        });
    }

    // 申请提现
    private void launchCashCash(){
        String cash = et_cash.getText().toString().trim();
        if (TextUtils.isEmpty(cash)){
            ToastUtils.showShort(getActivity(), "请输入提现金额！");
            return;
        }
        carryMoney = Double.parseDouble(cash);
        paramsMap_launchCashCash.put("carryMoney", carryMoney + "");
        paramsMap_launchCashCash.put("userId", userId);
        url_launchCashCash = Constants.baseUrl + Constants.url_launchCashCash;
        Okhttp3Utils.getAsycRequest(url_launchCashCash, paramsMap_launchCashCash, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(getActivity(), "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(getActivity(), resultInfo.getMessage());
                    } else {
                        ToastUtils.showShort(getActivity(), resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "服务器返回数据为空！");
                }
            }
        });
    }
}
