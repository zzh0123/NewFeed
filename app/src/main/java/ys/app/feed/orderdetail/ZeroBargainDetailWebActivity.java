package ys.app.feed.orderdetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;
import me.shaohui.bottomdialog.BottomDialog;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.HelpCutAdapter;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.HelpCutItem;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.comment.CommentActivity;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.utils.WRKShareUtil;

/**
 *  0元砍价订单详情(web)
 */
public class ZeroBargainDetailWebActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ll_back; // 返回

    // 商品信息
    private ImageView iv_goods_img;
    private TextView tv_goods_type;
    private TextView tv_spec;
    private TextView tv_count;

    // 剩余多少钱（砍价后）
    private TextView tv_left_price;
    // 剩余时间
    private CountdownView cv_countdownView_bargarin_time;
    // 帮砍一刀
    private TextView tv_help_one_zero_bargain;

    // 砍价帮
    private ArrayList<HelpCutItem> list_helpCutItem = new ArrayList<HelpCutItem>();
    private XRecyclerView recyclerview_bargain_team;
    private HelpCutAdapter adapter_help_cut;
    
    // 获取订单详情-砍价
    private String url_getOrderCutDetail; // url
    private String orderId ; // 订单id
    private String type = "2"; // 订单类型
    private HashMap<String, String> paramsMap_getOrderCutDetail = new HashMap<String, String>();

    // 剩余价格
    private Double totalAmount;
    private String commodityName;

    private String url; // 分享的url
    private HashMap<String, String> paramsMap_share = new HashMap<String, String>();

    // 底部按钮
    private TextView tv_comment; // 评价

    // 砍价
    private String url_goHelpCut; // url
    private String userId; // 用户名
    private HashMap<String, String> paramsMap_goHelpCut = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zero_bargain_detail_web);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        getValueFromWeb();

        initView();
    }

    private void getValueFromWeb() {
        //链接中取值取值
        Intent i_getvalue = getIntent();
        String action = i_getvalue.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = i_getvalue.getData();
            if (uri != null) {
                userId = uri.getQueryParameter("userId");
                orderId = uri.getQueryParameter("orderId");
                LogUtils.i("web--userId", "--userId--" + userId);
                LogUtils.i("web--orderId", "--orderId--" + orderId);
            }
        }
    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        // 商品信息
        iv_goods_img = (ImageView) findViewById(R.id.iv_goods_img);
        tv_goods_type = (TextView) findViewById(R.id.tv_goods_type);
        tv_spec = (TextView) findViewById(R.id.tv_spec);
        tv_count = (TextView) findViewById(R.id.tv_count);
        // 剩余多少钱（砍价后）
        tv_left_price = (TextView) findViewById(R.id.tv_left_price);
        // 剩余时间
        cv_countdownView_bargarin_time = (CountdownView) findViewById(R.id.cv_countdownView_bargarin_time);
        // 帮砍一刀
        tv_help_one_zero_bargain = (TextView) findViewById(R.id.tv_help_one_zero_bargain);
        tv_help_one_zero_bargain.setOnClickListener(this);

        // 砍价帮
        recyclerview_bargain_team = (XRecyclerView) findViewById(R.id.recyclerview_bargain_team);

        recyclerview_bargain_team.setPullRefreshEnabled(false);
        recyclerview_bargain_team.setLoadingMoreEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ZeroBargainDetailWebActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_bargain_team.setLayoutManager(layoutManager);

//        mRecyclerView_recommend.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
//        mRecyclerView_recommend.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
//        mRecyclerView_recommend.setArrowImageView(R.mipmap.iconfont_downgrey);

        recyclerview_bargain_team
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
//        header = LayoutInflater.from(getActivity()).inflate(R.bottom_group_buy_person_num.fragment_goods_header, (ViewGroup) findViewById(android.R.id.content), false);
//        mRecyclerView.addHeaderView(header);
//        mRecyclerView_recommend.getDefaultFootView().setLoadingHint("加载中...");
//        mRecyclerView_recommend.getDefaultFootView().setNoMoreHint("没有更多数据了");
//        mRecyclerView_recommend.getDefaultFootView().setMinimumHeight(100);

        adapter_help_cut = new HelpCutAdapter(this, list_helpCutItem);

        recyclerview_bargain_team.setAdapter(adapter_help_cut);

        getOrderCutDetail();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete_order: // 删除订单
//                deleteOrder();
                break;
            case R.id.tv_comment: // 评价
                Intent intent = new Intent(ZeroBargainDetailWebActivity.this, CommentActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_help_one_zero_bargain: // 分享订单
                goHelpCut();
                break;

            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

    // 砍价
    private void goHelpCut(){
        paramsMap_goHelpCut.put("orderId", orderId);
        paramsMap_goHelpCut.put("userId", userId);
        url_goHelpCut = Constants.baseUrl + Constants.url_goHelpCut;
        Okhttp3Utils.getAsycRequest(url_goHelpCut, paramsMap_goHelpCut, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(ZeroBargainDetailWebActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(ZeroBargainDetailWebActivity.this, "帮砍成功！");
                        finish();
                    } else {
                        ToastUtils.showShort(ZeroBargainDetailWebActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(ZeroBargainDetailWebActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    //  获取订单详情-砍价
    private void getOrderCutDetail(){
        paramsMap_getOrderCutDetail.put("orderId", orderId + "");
        url_getOrderCutDetail = Constants.baseUrl + Constants.url_getOrderCutDetail;
        Okhttp3Utils.getAsycRequest(url_getOrderCutDetail, paramsMap_getOrderCutDetail, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(ZeroBargainDetailWebActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        // 商品信息
                        String listImg = data_jsonObject.getString("listImg");
                        Glide.with(ZeroBargainDetailWebActivity.this).load(listImg)
                                .into(iv_goods_img);
                        commodityName = data_jsonObject.getString("commodityName");
                        tv_goods_type.setText(commodityName);
                        // 原价
                        Double directPrice = data_jsonObject.getDouble("totalAmount");
                        tv_spec.setText("原价¥ " + directPrice );
                        Integer buyCount = data_jsonObject.getInteger("buyCount");
                        tv_count.setText(buyCount + "");

                        // 剩余价格
                        totalAmount = data_jsonObject.getDouble("totalAmount");
                        tv_left_price.setText("剩余 " + totalAmount + "元");
                        // 剩余时间
                        Integer second = data_jsonObject.getInteger("second");
                        long time = (long) (second * 1000);
                        cv_countdownView_bargarin_time.start(time);

                        // 订单号
                        String orderNo = data_jsonObject.getString("orderNo");
                        // 订单id
                        Integer orderId = data_jsonObject.getInteger("orderId");

                        paramsMap_share.put("orderId", orderId + "");
                        url = Okhttp3Utils.build_get_url(Constants.url_share_order_helpCutInvite, paramsMap_share);
                        LogUtils.i("--share_url_cut--", "--share_url_cut--" + url);

                        // 砍价帮
                        JSONArray helpCut_jsonArray = (JSONArray) data_jsonObject.getJSONArray("helpCut");
                        String helpCut_json_str = JSONObject.toJSONString(helpCut_jsonArray);//将array数组转换成字符串
                        List<HelpCutItem> list_data = JSONObject.parseArray(helpCut_json_str, HelpCutItem.class);//把字符串转换成集合
                        // 商品评价列表
                        if (list_data != null && list_data.size() > 0) {
                            list_helpCutItem.addAll(list_data);
                            adapter_help_cut.notifyDataSetChanged();
                        }
                    } else {
                        ToastUtils.showShort(ZeroBargainDetailWebActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(ZeroBargainDetailWebActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }
}
