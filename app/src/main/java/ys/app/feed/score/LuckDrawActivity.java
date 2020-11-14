package ys.app.feed.score;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import ys.app.feed.about.KnowAboutActivity;
import ys.app.feed.adapter.ReceivingAddressAdapter;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.GiftItem;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.DrawLotteryUtil;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.dialog.LuckyTableDialog;

/**
 *  积分抽奖
 */
public class LuckDrawActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    private String userId; // 用户id

    private LuckyTableDialog luckyTableDialog;

    // 获取抽奖物品
    private String url_getLuckDrawData; // url
    private HashMap<String, String> paramsMap_getLuckDrawData = new HashMap<String, String>();
    private ArrayList<GiftItem> list_giftItem = new ArrayList<GiftItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luck_draw);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        // 用户id
        userId = (String) SPUtils.get(LuckDrawActivity.this, "userId", "");

        initView();
        getLuckDrawData();
    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
            default:
                break;
        }
    }

    // 积分抽奖（1-8）
    private void showLuckyTableDialog(){
        luckyTableDialog = new LuckyTableDialog(this);
        luckyTableDialog.setList_giftItem(list_giftItem);
        luckyTableDialog.setYesOnclickListener("确定", new LuckyTableDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                Intent intent = new Intent(LuckDrawActivity.this, BuyLuckDrawCouponActivity.class);
//                startActivity(intent);
                luckyTableDialog.dismiss();
                startActivityForResult(intent, 1);
            }
        });
        luckyTableDialog.setNoOnclickListener("取消", new LuckyTableDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
//                    Toast.makeText(getActivity(), "点击了--取消--按钮", Toast.LENGTH_LONG).show();
                luckyTableDialog.dismiss();
                finish();
            }
        });

        WindowManager.LayoutParams lp = luckyTableDialog.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        luckyTableDialog.getWindow().setAttributes(lp);
        luckyTableDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        luckyTableDialog.show();
    }

    // 获取抽奖物品
    private void getLuckDrawData(){
        list_giftItem.clear();
        url_getLuckDrawData = Constants.baseUrl + Constants.url_getLuckDrawData;
        Okhttp3Utils.getAsycRequest(url_getLuckDrawData, paramsMap_getLuckDrawData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(LuckDrawActivity.this, "服务器未响应！");
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
                        List<GiftItem> list_data = JSONObject.parseArray(data_json_str, GiftItem.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0){
                            list_giftItem.addAll(list_data);
                            showLuckyTableDialog();
//                            for(int i=0;i<100;i++){
//                                int index = DrawLotteryUtil.drawGift(list_giftItem);
//                                System.out.println(list_giftItem.get(index).getConfigName());
//                            }
                        }
                    } else {
                        ToastUtils.showShort(LuckDrawActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(LuckDrawActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
            if (requestCode == 1 && resultCode == 1) {
//                ReceivingAddress receivingAddress = (ReceivingAddress) data.getSerializableExtra("receivingAddress");
                LogUtils.i("onActivityResult", "--onActivityResult--");
                getLuckDrawData();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
