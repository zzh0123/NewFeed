package ys.app.feed.coupon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.CouponAdapter;
import ys.app.feed.bargain.ZeroBargainActivity;
import ys.app.feed.bean.Coupon;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.discount.GiftPackageActivity;
import ys.app.feed.groupbuy.GroupBuyActivity;
import ys.app.feed.halfprice.HalfPriceActivity;
import ys.app.feed.score.LuckDrawActivity;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

public class CouponActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_back; // 返回

    private TextView tv_not_used; // 未使用
    private View view_not_used; // 未使用

    private TextView tv_use_record; // 使用记录
    private View view_use_record; // 使用记录

    private TextView tv_overdue; // 已过期
    private View view_overdue; // 已过期

    private XRecyclerView recyclerview_coupon;

    // 获取用户优惠券
    private String url_getCouponData; // 获取地址接口url
    private Integer pageNum = 1; // 页
    private Integer pageSize = 10; // 行
    private Integer type = 0; // 0为已领取未使用,1为已使用,2为已过期
    private String userId; // 用户名,用户id
    private HashMap<String, String> paramsMap_getCouponData = new HashMap<String, String>();
    private ArrayList<Coupon> list_coupon = new ArrayList<Coupon>();

    private CouponAdapter adapter_coupon;

    private Intent intent;
    private Integer cashCouponId; // 抵扣券id
    private Integer noSpellId; // 免拼券id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        intent = getIntent();
        initView();
        setChioceItem(0);
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        tv_not_used = (TextView) findViewById(R.id.tv_not_used);
        tv_use_record = (TextView) findViewById(R.id.tv_use_record);
        tv_overdue = (TextView) findViewById(R.id.tv_overdue);

        view_not_used = (View) findViewById(R.id.view_not_used);
        view_use_record = (View) findViewById(R.id.view_use_record);
        view_overdue = (View) findViewById(R.id.view_overdue);

        tv_not_used.setOnClickListener(CouponActivity.this);
        tv_use_record.setOnClickListener(CouponActivity.this);
        tv_overdue.setOnClickListener(CouponActivity.this);

        recyclerview_coupon = (XRecyclerView) findViewById(R.id.recyclerview_coupon);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_coupon.setLayoutManager(layoutManager);

        recyclerview_coupon.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerview_coupon.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        recyclerview_coupon.setArrowImageView(R.mipmap.iconfont_downgrey);

        recyclerview_coupon
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
        recyclerview_coupon.getDefaultFootView().setLoadingHint("加载中...");
        recyclerview_coupon.getDefaultFootView().setNoMoreHint("没有更多数据了");
        recyclerview_coupon.getDefaultFootView().setMinimumHeight(100);

        recyclerview_coupon.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum++;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        recyclerview_coupon.refreshComplete();
                    }

                }, 0);            //refresh data here
            }

            @Override
            public void onLoadMore() {
                Log.e("aaaaa", "call onLoadMore");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        pageNum++;
                        getCoupon();
                    }
                }, 1000);
            }
        });

        adapter_coupon = new CouponAdapter(this, list_coupon, type);
        adapter_coupon.setClickCallBack(new CouponAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
//                ToastUtils.showShort(CouponActivity.this, "--" + pos);
                String coupon_type = list_coupon.get(pos).getCouponType();
                if (TextUtils.equals("018001", coupon_type)) { // 半价券
                    Intent intent = new Intent(CouponActivity.this, HalfPriceActivity.class);
                    startActivity(intent);
                } else if (TextUtils.equals("018002", coupon_type)) { // 砍价券
                    Intent intent = new Intent(CouponActivity.this, ZeroBargainActivity.class);
                    startActivity(intent);
                } else if (TextUtils.equals("018003", coupon_type)) { // 免拼券
                    Intent intent = new Intent(CouponActivity.this, GroupBuyActivity.class);
                    startActivity(intent);
                } else if (TextUtils.equals("018004", coupon_type)) { // 代金券
                    Intent intent = new Intent(CouponActivity.this, GroupBuyActivity.class);
                    startActivity(intent);
                }else if (TextUtils.equals("018005", coupon_type)) { // 抽奖券
                    Intent intent = new Intent(CouponActivity.this, LuckDrawActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });

        recyclerview_coupon.setAdapter(adapter_coupon);

        // 用户id
        userId = (String) SPUtils.get(CouponActivity.this, "userId", "");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_not_used:  // 未使用
                setChioceItem(0);
                break;
            case R.id.tv_use_record:  // 使用记录
                setChioceItem(1);
                break;
            case R.id.tv_overdue: // 已过期
                setChioceItem(2);
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 设置点击选项卡的事件处理
     *
     * @param index 选项卡的标号：0, 1, 2, 3
     */
    private void setChioceItem(int index) {
        clearChioce(); // 清空, 重置选项, 隐藏所有Fragment
        switch (index) { // 0为已领取未使用,1为已使用,2为已过期
            case 0:
                tv_not_used.setTextColor(getResources().getColor(R.color.text_green));
                view_not_used.setBackgroundResource(R.color.text_green);
                list_coupon.clear();
                adapter_coupon.notifyDataSetChanged();
                type = 0;
                getCoupon();
                break;
            case 1:
                tv_use_record.setTextColor(getResources().getColor(R.color.text_green));
                view_use_record.setBackgroundResource(R.color.text_green);
                list_coupon.clear();
                adapter_coupon.notifyDataSetChanged();
                type = 1;
                getCoupon();
                break;
            case 2:
                tv_overdue.setTextColor(getResources().getColor(R.color.text_green));
                view_overdue.setBackgroundResource(R.color.text_green);
                list_coupon.clear();
                adapter_coupon.notifyDataSetChanged();
                type = 2;
                getCoupon();
                break;
        }
    }

    /**
     * 当选中其中一个选项卡时，其他选项卡重置为默认
     */
    private void clearChioce() {
        tv_overdue.setTextColor(getResources().getColor(R.color.text_black));
        view_overdue.setBackgroundResource(R.color.white);

        tv_use_record.setTextColor(getResources().getColor(R.color.text_black));
        view_use_record.setBackgroundResource(R.color.white);
        tv_not_used.setTextColor(getResources().getColor(R.color.text_black));
        view_not_used.setBackgroundResource(R.color.white);
    }

    // 获取用户优惠券
    private void getCoupon() {
        paramsMap_getCouponData.put("pageNum", pageNum + "");
        paramsMap_getCouponData.put("pageSize", pageSize + "");
        paramsMap_getCouponData.put("type", type + "");
        paramsMap_getCouponData.put("userId", userId);
        url_getCouponData = Constants.baseUrl + Constants.url_getCouponData;
        Okhttp3Utils.getAsycRequest(url_getCouponData, paramsMap_getCouponData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(CouponActivity.this, "服务器未响应！");
                recyclerview_coupon.refreshComplete();
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
                        List<Coupon> list_data = JSONObject.parseArray(data_json_str, Coupon.class);//把字符串转换成集合

                        if (list_data != null && list_data.size() > 0) {
                            list_coupon.addAll(list_data);
                            if (adapter_coupon == null) {
                                adapter_coupon = new CouponAdapter(CouponActivity.this, list_coupon, type);
                                adapter_coupon.setType(type);
                                recyclerview_coupon.setAdapter(adapter_coupon);
                            }
                            adapter_coupon.setType(type);
                            recyclerview_coupon.refreshComplete();
                            adapter_coupon.notifyDataSetChanged();
//                                mRecyclerView.refresh();
//                                mRecyclerView.refreshComplete();
                        } else {
                            recyclerview_coupon.refreshComplete();
                            recyclerview_coupon.setNoMore(true);
                        }

                    } else {
                        recyclerview_coupon.refreshComplete();
                    }
                } else {
                    ToastUtils.showShort(CouponActivity.this, "服务器返回数据为空！");
                    recyclerview_coupon.refreshComplete();
                }
            }
        });
    }
}
