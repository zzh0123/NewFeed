package ys.app.feed.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.CouponAdapter;
import ys.app.feed.adapter.SearchGoodsAdapter;
import ys.app.feed.bean.Coupon;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.bean.SearchGoodsItem;
import ys.app.feed.constant.Constants;
import ys.app.feed.goods.AllGoodsActivity;
import ys.app.feed.goods.GoodsDetailActivity;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.ToastUtils;

public class SearchActivity extends Activity implements View.OnClickListener {
    private LinearLayout ll_back; // 返回

    private EditText et_search; // 搜索
    private TextView tv_search; // 搜索

    // 获取用户优惠券
    private String url_globalSearch; // url
    private String content;
    private HashMap<String, String> paramsMap_globalSearch = new HashMap<String, String>();
    private ArrayList<SearchGoodsItem> list_searchGoodsItem = new ArrayList<SearchGoodsItem>();

    private XRecyclerView recyclerview_goods;
    private SearchGoodsAdapter adapter_searchGoods;

    private String classType = "2"; // 1.全部商品列表 | 2.团购直购列表 | 3.半价列表 | 4.积分列表 | 5.砍价列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        et_search = (EditText) findViewById(R.id.et_search);
        tv_search = (TextView) findViewById(R.id.tv_search);

        ll_back.setOnClickListener(SearchActivity.this);
        tv_search.setOnClickListener(SearchActivity.this);

        recyclerview_goods = (XRecyclerView) findViewById(R.id.recyclerview_goods);
        recyclerview_goods.setPullRefreshEnabled(false);
        recyclerview_goods.setLoadingMoreEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_goods.setLayoutManager(layoutManager);

        recyclerview_goods.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerview_goods.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        recyclerview_goods.setArrowImageView(R.mipmap.iconfont_downgrey);

        recyclerview_goods
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
        recyclerview_goods.getDefaultFootView().setLoadingHint("加载中...");
        recyclerview_goods.getDefaultFootView().setNoMoreHint("没有更多数据了");
        recyclerview_goods.getDefaultFootView().setMinimumHeight(100);


        adapter_searchGoods = new SearchGoodsAdapter(this, list_searchGoodsItem);
        adapter_searchGoods.setClickCallBack(new SearchGoodsAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
//                ToastUtils.showShort(SearchActivity.this, "--" + pos);
                String commodityId = list_searchGoodsItem.get(pos).getCommodityId();
                Intent intent = new Intent(SearchActivity.this, GoodsDetailActivity.class);
                intent.putExtra("commodityId", commodityId);
                intent.putExtra("classType", classType);
                startActivity(intent);
            }
        });

        recyclerview_goods.setAdapter(adapter_searchGoods);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:  // 搜索
                content = et_search.getText().toString().trim();
//                String search_content = "";
////                try {
////                    search_content = URLDecoder.decode(content, "UTF-8");
////                }catch (UnsupportedEncodingException e){
////                    e.printStackTrace();
////                }
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showShort(SearchActivity.this, "请输入搜索内容！");
                    return;
                }
                globalSearch();
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
            default:
                break;
        }
    }

    // 全局搜索
    private void globalSearch() {
        tv_search.setClickable(false);
        list_searchGoodsItem.clear();
        adapter_searchGoods.notifyDataSetChanged();
        paramsMap_globalSearch.put("content", content);
        url_globalSearch = Constants.baseUrl + Constants.url_globalSearch;
        Okhttp3Utils.getAsycRequest(url_globalSearch, paramsMap_globalSearch, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(SearchActivity.this, "服务器未响应！");
                tv_search.setClickable(true);
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        tv_search.setClickable(false);
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONArray data_jsonArray = (JSONArray) response_jsonObject.getJSONArray("data");
                        String data_json_str = JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                        List<SearchGoodsItem> list_data = JSONObject.parseArray(data_json_str, SearchGoodsItem.class);//把字符串转换成集合

                        if (list_data != null && list_data.size() > 0) {
                            list_searchGoodsItem.addAll(list_data);
                        }
                        adapter_searchGoods.notifyDataSetChanged();

                    } else {
                        ToastUtils.showShort(SearchActivity.this, resultInfo.getMessage());
                        tv_search.setClickable(true);
                    }
                } else {
                    ToastUtils.showShort(SearchActivity.this, "服务器返回数据为空！");
                    tv_search.setClickable(true);
                }
            }
        });
    }
}
