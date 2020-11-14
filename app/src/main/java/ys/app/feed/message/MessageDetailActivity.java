package ys.app.feed.message;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.DateUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.ToastUtils;

public class MessageDetailActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    private TextView tv_message_date;
    private TextView tv_message_content;
    // 获取消息通知详情
    private String url_getMessageDetail; // 获取消息通知详情接口url
    private String id; // id
    private HashMap<String, String> paramsMap_getMessageDetail = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
        getMessageDetail();
    }
    private void initView() {
        id = getIntent().getStringExtra("id");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_message_date = (TextView) findViewById(R.id.tv_message_date);
        tv_message_content = (TextView) findViewById(R.id.tv_message_content);
        ll_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    // 获取消息详情
    private void getMessageDetail(){
        paramsMap_getMessageDetail.put("id", id);
        url_getMessageDetail = Constants.baseUrl + Constants.url_getMessageDetail;
        Okhttp3Utils.getAsycRequest(url_getMessageDetail, paramsMap_getMessageDetail, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(MessageDetailActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        String content = data_jsonObject.getString("content");
                        String createDate = data_jsonObject.getString("createDate");
                        if (!TextUtils.isEmpty(content)){
                            tv_message_content.setText(content);
                        }
                        if (!TextUtils.isEmpty(createDate)){
                            String date = DateUtils.stampToDate(createDate);
                            tv_message_date.setText(date);
                        }
                    } else {
                        ToastUtils.showShort(MessageDetailActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(MessageDetailActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }
}
