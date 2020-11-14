package ys.app.feed.scoreexchange;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shizhefei.fragment.LazyFragment;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.adapter.CommentAdapter;
import ys.app.feed.bean.Comment;
import ys.app.feed.bean.GoodsImage;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.order.GroupBuyOrderActivity;
import ys.app.feed.order.ScoreExchangeOrderActivity;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.banner.video.BannerViewAdapter;

/**
 *  积分兑换商品详情fragment
 */

public class ScoreExchangeGoodsFragment extends LazyFragment implements View.OnClickListener {

    // 混合轮播
    public ViewPager mViewPager;
    private List<GoodsImage> list_goodsImage = new ArrayList<GoodsImage>();
    ;
    private BannerViewAdapter mAdapter;
    private int autoCurrIndex = 0;//设置当前 第几个图片 被选中
    private long period = 5000;//轮播图展示时长,默认5秒

    // 商品评价列表
    private XRecyclerView mRecyclerView;
    private CommentAdapter adapter_comment;
    private ArrayList<Comment> list_comment = new ArrayList<Comment>();

    // 查看更多商品评价
    private LinearLayout ll_search_more_comment; // 查看更多商品评价

    private View header;
    // 价格
    private TextView tv_price;
    private TextView tv_original_price;
    private TextView tv_full_reduce;
    private Double score; // 抵扣的积分
    private Double money; // 抵扣积分后的价格
    private Double memberPrice; // 原价
    private Integer scoreReduceFreightCount; // 满减，积分运费
    private Double scoreFreight; // 积分运费

    // 规格
    private TextView tv_goods_type;
    private String goods_name;

    // 发货地址
    private TextView tv_station;
    private TextView tv_station_phone;
    private TextView tv_station_address;
    private String address;
    private String phone;
    private String station; // 服务站名
    private String salesman;

    // 获取商品详情
    private String url_getCommodityDetail; // url
    private String userId; // 用户名
    private String commodityId; // 商品Id
    private String classType; //
    private HashMap<String, String> paramsMap_getCommodityDetail = new HashMap<String, String>();

    // 购买按钮
    private TextView tv_to_buy;

    // 收藏
    private ImageView iv_isCollection;
    private Integer isCollection = 1;

    // 向详情activity中传递值
    public CallBackValue callBackValue;

    // 详情页的图片
    private String detailImg;
    // 传到订单的图片
    private String listImg;

    // 商品收藏和取消收藏
    private String url_commodityCollection; // url
    private HashMap<String, String> paramsMap_commodityCollection = new HashMap<String, String>();

    // 最小购买量
    private Integer scoreCapacityCount;


    private Handler handler;

    public ScoreExchangeGoodsFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(android.os.Message msg) {
                initView();
            }
        };

        setContentView(R.layout.fragment_score_exchange_goods);

        Bundle bundle = getArguments();
        commodityId = bundle.getString("commodityId");
        classType = bundle.getString("classType");
        // 用户id
        userId = (String) SPUtils.get(getActivity(), "userId", "");
        LogUtils.i("-gf-commodityId--", "--commodityId--" + commodityId);
        getCommodityDetail();
    }

    private void initView() {
        // 购买按钮
        tv_to_buy = (TextView) findViewById(R.id.tv_to_buy);
        tv_to_buy.setOnClickListener(this);
        tv_to_buy.setVisibility(View.VISIBLE);

        // 商品评价列表
        mRecyclerView = (XRecyclerView) this.findViewById(R.id.recyclerview_goods);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.iconfont_downgrey);

        mRecyclerView
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
        header = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_score_exchange_goods_header, (ViewGroup) findViewById(android.R.id.content), false);
        mRecyclerView.addHeaderView(header);
        mRecyclerView.getDefaultFootView().setLoadingHint("加载中...");
        mRecyclerView.getDefaultFootView().setNoMoreHint("没有更多数据了");
        mRecyclerView.getDefaultFootView().setMinimumHeight(100);

        adapter_comment = new CommentAdapter(getActivity(), list_comment);

        mRecyclerView.setAdapter(adapter_comment);
        setRecyclerView();
        // 查看更多商品评价
        ll_search_more_comment = (LinearLayout) header.findViewById(R.id.ll_search_more_comment);
        ll_search_more_comment.setOnClickListener(this);

        // 顶部轮播视频图片
        mViewPager = (ViewPager) header.findViewById(R.id.viewPager);
        autoBanner();

        // 价格
        tv_price = (TextView) header.findViewById(R.id.tv_price);
        tv_original_price = (TextView) header.findViewById(R.id.tv_original_price);
        tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //添加删除线
        tv_full_reduce = (TextView) header.findViewById(R.id.tv_full_reduce);
        tv_price.setText(score + "积分" + " + ¥" + money);
        tv_original_price.setText("¥" + memberPrice + "");
        tv_full_reduce.setText("满" + scoreReduceFreightCount + "包免运费");
        // 规格
        tv_goods_type = (TextView) header.findViewById(R.id.tv_goods_type);
        tv_goods_type.setText(goods_name);

        // 发货地址
        tv_station = (TextView) header.findViewById(R.id.tv_station);
        tv_station_phone = (TextView) header.findViewById(R.id.tv_station_phone);
        tv_station_address = (TextView) header.findViewById(R.id.tv_station_address);
        tv_station.setText(station);
        tv_station_phone.setText(salesman + " " + phone);
        tv_station_address.setText(address);



        // 收藏
        iv_isCollection = (ImageView) header.findViewById(R.id.iv_isCollection);
        if (isCollection == 1){ // 为收藏
            iv_isCollection.setImageResource(R.mipmap.not_collect);
        } else {
            iv_isCollection.setImageResource(R.mipmap.collect);
        }
        iv_isCollection.setOnClickListener(this);

    }

    private void setRecyclerView() {
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnabled(false);
    }

    private void autoBanner() {
        mViewPager.setOffscreenPageLimit(0);
//        mAdapter = new BannerViewAdapter(getActivity(), list_goodsImage);
//        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
                autoCurrIndex = position;//动态设定轮播图每一页的停留时间
                Log.i("--onPageScrolled--", "--onPageScrolled--" + position);
//                Toast.makeText(getActivity(), "" + autoCurrIndex, Toast.LENGTH_SHORT).show();
                if (position != 0) {
                    mAdapter.jzVideoPlayerStandard.release();
                }
            }

            @Override
            public void onPageSelected(int position) {
                autoCurrIndex = position;//动态设定轮播图每一页的停留时间
//                period = list.get(position).getPlayTime();
                Log.i("--onPageSelected--", "--onPageSelected--" + position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        if (mAdapter == null) {
            mAdapter = new BannerViewAdapter(getActivity(), list_goodsImage);
            mViewPager.setAdapter(mAdapter);
        }

        CirclePageIndicator mIndicator = (CirclePageIndicator) header.findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);//把圆点和ViewPager关联起来
    }

    @Override
    public void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        handler.removeCallbacksAndMessages(null);
        if (mAdapter != null && mAdapter.jzVideoPlayerStandard != null) {
            mAdapter.jzVideoPlayerStandard.release();
        }
    }

    // 获取商品详情
    private void getCommodityDetail() {
        paramsMap_getCommodityDetail.put("classType", classType);
        paramsMap_getCommodityDetail.put("commodityId", commodityId);
        paramsMap_getCommodityDetail.put("userId", userId);
        url_getCommodityDetail = Constants.baseUrl + Constants.url_getCommodityDetail;
        Okhttp3Utils.getAsycRequest(url_getCommodityDetail, paramsMap_getCommodityDetail, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(getActivity(), "服务器未响应！");
                handler.sendEmptyMessageDelayed(1, 1000);
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");
                        Integer commentCount = data_jsonObject.getInteger("commentCount");
                        JSONArray comment_jsonArray = (JSONArray) data_jsonObject.getJSONArray("commentData");
                        String comment_json_str = JSONObject.toJSONString(comment_jsonArray);//将array数组转换成字符串
                        List<Comment> list_data = JSONObject.parseArray(comment_json_str, Comment.class);//把字符串转换成集合
                        // 商品评价列表
                        if (list_data != null && list_data.size() > 0) {
                            list_comment.addAll(list_data);
                            for (int i = 0; i < list_comment.size(); i++) {
                                JSONObject comment_jsonObject = (JSONObject) comment_jsonArray.get(i);
                                JSONArray imgs_jsonArray = (JSONArray) comment_jsonObject.getJSONArray("imgs");
                                ArrayList<String> list_imgesUrl = new ArrayList<>();
                                list_comment.get(i).setImgs(list_imgesUrl);

                                if (imgs_jsonArray != null && imgs_jsonArray.size() > 0) {
                                    for (int j = 0; j < imgs_jsonArray.size(); j++) {
                                        list_comment.get(i).getImgs().add(imgs_jsonArray.get(j).toString().trim());
                                    }
                                }
                            }
                        }
                        // 混合轮播
                        JSONArray commodityImgs_jsonArray = (JSONArray) data_jsonObject.getJSONArray("commodityImgs");
                        String commodityImgs_json_str = JSONObject.toJSONString(commodityImgs_jsonArray);//将array数组转换成字符串
                        List<GoodsImage> list_goodsImage_data = JSONObject.parseArray(commodityImgs_json_str, GoodsImage.class);//把字符串转换成集合
                        if (list_goodsImage_data != null && list_goodsImage_data.size() > 0) {
                            list_goodsImage.addAll(list_goodsImage_data);
                            LogUtils.i("--list_goodsImage--", "--list_goodsImage--" + list_goodsImage.size());

                        }

                        // 价格
                        score = data_jsonObject.getDouble("score"); // 抵扣的积分
                        money = data_jsonObject.getDouble("money"); // 抵扣积分后的价格
                        memberPrice = data_jsonObject.getDouble("memberPrice"); // 原价
                        scoreReduceFreightCount = data_jsonObject.getInteger("scoreReduceFreightCount"); // 满减(积分运费)

                        // 规格
                        goods_name = data_jsonObject.getString("name");

                        // 最小购买量
                        scoreCapacityCount = data_jsonObject.getInteger("scoreCapacityCount");

                        //  积分运费
                        scoreFreight = data_jsonObject.getDouble("scoreFreight");

                        // 发货地址
                        JSONObject station_jsonObject = data_jsonObject.getJSONObject("station");
                        address = station_jsonObject.getString("address");
                        phone = station_jsonObject.getString("phone");
                        station = station_jsonObject.getString("station"); // 服务站名
                        salesman = station_jsonObject.getString("name");

                        // 收藏
                        isCollection = data_jsonObject.getInteger("isCollection");

                        // 详情页的图片
                        detailImg = data_jsonObject.getString("detailImg");
                        callBackValue.SendValue(detailImg);
                        // 传到订单的图片
                        listImg = data_jsonObject.getString("listImg");

                        handler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        ToastUtils.showShort(getActivity(), resultInfo.getMessage());
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "服务器返回数据为空！");
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search_more_comment: // 查看更多商品评价
                callBackValue.SendValue2("searchMoreComment");
                break;
            case R.id.iv_isCollection: // 收藏
                commodityCollection();
                break;
            case R.id.tv_to_buy: // 单独购买按钮
                Intent intent1 = new Intent(getActivity(), ScoreExchangeOrderActivity.class);
                intent1.putExtra("commodityId", commodityId);
                intent1.putExtra("scoreReduceFreightCount", scoreReduceFreightCount); // 满减，积分运费
                intent1.putExtra("scoreFreight", scoreFreight); // 积分运费
                intent1.putExtra("scoreCapacityCount", scoreCapacityCount);

                intent1.putExtra("score", score);
                intent1.putExtra("listImg", listImg);
                intent1.putExtra("goods_name", goods_name);
                intent1.putExtra("money", money);
                startActivity(intent1);
                getActivity().finish();
                break;
        }
    }

    /**
     * fragment与activity产生关联是  回调这个方法
     */
    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        //当前fragment从activity重写了回调接口  得到接口的实例化对象
        callBackValue = (CallBackValue) getActivity();
    }


    // 向详情activity中传递值
    public interface CallBackValue {
        public void SendValue(String strValue);
        public void SendValue2(String strValue);
    }

    // 商品收藏和取消收藏
    private void commodityCollection(){
        paramsMap_commodityCollection.put("commodityId", commodityId);
        paramsMap_commodityCollection.put("userId", userId);
        url_commodityCollection = Constants.baseUrl + Constants.url_commodityCollection;
        Okhttp3Utils.getAsycRequest(url_commodityCollection, paramsMap_commodityCollection, new ResultCallback() {
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
                        if (isCollection == 1){
                            isCollection = 0;
                        } else {
                            isCollection = 1;
                        }
                        if (isCollection == 1){
                            iv_isCollection.setImageResource(R.mipmap.not_collect);
                        } else {
                            iv_isCollection.setImageResource(R.mipmap.collect);
                        }

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
