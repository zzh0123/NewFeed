package ys.app.feed.intention;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MainActivity;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.IntentionModuleAdapter;
import ys.app.feed.adapter.MyModuleAdapter;
import ys.app.feed.adapter.ReceivingAddressAdapter;
import ys.app.feed.address.AddAddressActivity;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.IntentionModuleItem;
import ys.app.feed.bean.ModuleItem;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.collect.MyCollectActivity;
import ys.app.feed.comment.MyCommentActivity;
import ys.app.feed.constant.Constants;
import ys.app.feed.invite.InviteActivity;
import ys.app.feed.invite.MyInviteActivity;
import ys.app.feed.login.LoginActivity;
import ys.app.feed.order.MyOrderActivity;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

/**
 *  选择意向产品
 */
public class IntentionActivity extends Activity {

    private RecyclerView mRecyclerView;
    private IntentionModuleAdapter adapter_module;
    private ArrayList<IntentionModuleItem> list_intention_module = new ArrayList<IntentionModuleItem>();

    private String intention = "011001";

    private Button bt_sure;
    // 获取地址列表
    private String url_addUserIntention; // url
    private String userId; // 用户名
    private HashMap<String, String> paramsMap_addUserIntention = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intention);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        userId = (String) SPUtils.get(IntentionActivity.this, "userId", "");
        initView();
    }

    private void initView(){
        setModuleList();
        list_intention_module.get(0).setSelected(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(IntentionActivity.this, 3);
//        StaggeredGridLayoutManager sg_layoutManager =
//                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_intention);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        adapter_module = new IntentionModuleAdapter(this, list_intention_module);
        adapter_module.setClickCallBack(new IntentionModuleAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                intention = list_intention_module.get(pos).getIntention();
                LogUtils.i("--intention--", "--intention--" + intention);
                for (int i=0; i<list_intention_module.size(); i++){
                    list_intention_module.get(i).setSelected(false);
                }
                list_intention_module.get(pos).setSelected(true);
                adapter_module.notifyDataSetChanged();
            }
        });
        mRecyclerView.setAdapter(adapter_module);

        bt_sure = findViewById(R.id.bt_sure);
        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserIntention();
            }
        });
    }
    private void setModuleList() {
        list_intention_module.add(new IntentionModuleItem("猪", R.mipmap.img_pig_unselect,"011001",  false));
        list_intention_module.add(new IntentionModuleItem("鸡", R.mipmap.img_chicken_unselect, "011004",false));
        list_intention_module.add(new IntentionModuleItem("牛", R.mipmap.img_cattle_unselect, "011002",false));
        list_intention_module.add(new IntentionModuleItem("羊", R.mipmap.img_sheep_unselect, "011003",false));
        list_intention_module.add(new IntentionModuleItem("狐貉貂", R.mipmap.img_fox_unselect, "011005",false));
    }

    // 添加用户意向产品
    private void addUserIntention(){
        bt_sure.setClickable(false);
        paramsMap_addUserIntention.put("intention", intention);
        paramsMap_addUserIntention.put("userId", userId);
        url_addUserIntention = Constants.baseUrl + Constants.url_addUserIntention;
        Okhttp3Utils.getAsycRequest(url_addUserIntention, paramsMap_addUserIntention, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(IntentionActivity.this, "服务器未响应！");
                bt_sure.setClickable(true);
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(IntentionActivity.this, "选好了！");
                        bt_sure.setClickable(false);
                        Intent intent = new Intent(IntentionActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.showShort(IntentionActivity.this, resultInfo.getMessage());
                        bt_sure.setClickable(true);
                    }
                } else {
                    ToastUtils.showShort(IntentionActivity.this, "服务器返回数据为空！");
                    bt_sure.setClickable(true);
                }
            }
        });
    }

}
