package ys.app.feed.goods;


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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.donkingliang.labels.LabelsView;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shizhefei.fragment.LazyFragment;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.bakumon.library.view.BulletinView;
import me.shaohui.bottomdialog.BottomDialog;
import ys.app.feed.R;
import ys.app.feed.adapter.BulletinBoardAdapter;
import ys.app.feed.adapter.CommentAdapter;
import ys.app.feed.adapter.PromotionAdapter;
import ys.app.feed.bean.Comment;
import ys.app.feed.bean.GoodsImage;
import ys.app.feed.bean.GroupDataItem;
import ys.app.feed.bean.GroupPersonNumItem;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.groupbuy.GroupBuyActivity;
import ys.app.feed.order.GroupBuyOrderActivity;
import ys.app.feed.order.SingleBuyOrderActivity;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.banner.video.BannerViewAdapter;

/**
 * 团购/全部商品详情fragment(web)
 */

public class GoodsWebFragment extends LazyFragment implements View.OnClickListener {

    // 混合轮播
    public ViewPager mViewPager;
    private List<GoodsImage> list_goodsImage = new ArrayList<GoodsImage>();
    ;
    private BannerViewAdapter mAdapter;
    private int autoCurrIndex = 0;//设置当前 第几个图片 被选中
    private long period = 5000;//轮播图展示时长,默认5秒

    // 拼团轮播
    private TextView tv_group_buying_person_num; // 正在拼单人数
    private Integer groupCount;
    private BulletinView mBulletinViewSale;
    private ArrayList<GroupDataItem> list_groupData = new ArrayList<GroupDataItem>();

    // 查看更多拼团
    private LinearLayout ll_search_more_group; // 查看更多拼团

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
    private Double groupMemberPrice;
    private Double groupPrice;
    private Integer reduceFreightCount;

    // 规格
    private TextView tv_goods_type;
    private String goods_name;
    // 促销
    private LinearLayout ll_promotion;
    private TextView tv_promotion;
    private ArrayList<String> list_promotionItem = new ArrayList<String>();
    private PromotionAdapter promotionAdapter;
    private JSONArray promotion_jsonArray;

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
    private LinearLayout ll_bottom;
    private LinearLayout ll_single_buy;
    private LinearLayout ll_group_buy;
    private TextView tv_single_price;
    private TextView tv_start_group_price;
    private Double directBuyPrice;
    private Double groupBuyPrice;

    private Double freight; // 运费

    // 最小购买数量
    private Integer capacityCount;

    // 收藏
    private ImageView iv_isCollection;
    private Integer isCollection = 1;

    // 向详情activity中传递值
    public CallBackValue callBackValue;
    // 详情页的图片
    private String detailImg;
    // 传到订单的图片
    private String listImg;

    // 获取团规模
    private String url_getGroupScale; // url
    private HashMap<String, String> paramsMap_getGroupScale = new HashMap<String, String>();
    ArrayList<GroupPersonNumItem> list_groupPersonNumItem = new ArrayList<GroupPersonNumItem>();

    private String scale; // 团规模

    // 商品收藏和取消收藏
    private String url_commodityCollection; // url
    private HashMap<String, String> paramsMap_commodityCollection = new HashMap<String, String>();

    private String groupNo; // 团号

    private Handler handler;

    public GoodsWebFragment() {
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

        setContentView(R.layout.fragment_goods_web);

        Bundle bundle = getArguments();
        commodityId = bundle.getString("commodityId");
        classType = bundle.getString("classType");
        // 用户id
        userId = bundle.getString("userId");
        groupNo = bundle.getString("groupNo");
        LogUtils.i("-gf-commodityId--", "--commodityId--" + commodityId);
        getCommodityDetail();
    }

    private void initView() {
        // 购买按钮
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        ll_single_buy = (LinearLayout) findViewById(R.id.ll_single_buy);
        ll_group_buy = (LinearLayout) findViewById(R.id.ll_group_buy);
        ll_single_buy.setOnClickListener(this);
        ll_group_buy.setOnClickListener(this);
        ll_bottom.setVisibility(View.VISIBLE);
        tv_single_price = (TextView) findViewById(R.id.tv_single_price);
        tv_start_group_price = (TextView) findViewById(R.id.tv_start_group_price);
        tv_single_price.setText("¥ " + directBuyPrice);
        tv_start_group_price.setText("¥ " + groupBuyPrice);

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
        header = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_goods_header, (ViewGroup) findViewById(android.R.id.content), false);
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

        // 拼单轮播
        tv_group_buying_person_num = (TextView) header.findViewById(R.id.tv_group_buying_person_num);
        tv_group_buying_person_num.setText(groupCount + "人正在拼单，可直接参与");
        mBulletinViewSale = (BulletinView) header.findViewById(R.id.bulletin_view);
        setGroupView();
        // 查看更多拼团
        ll_search_more_group = (LinearLayout) header.findViewById(R.id.ll_search_more_group);
        ll_search_more_group.setOnClickListener(this);

        // 价格
        tv_price = (TextView) header.findViewById(R.id.tv_price);
        tv_original_price = (TextView) header.findViewById(R.id.tv_original_price);
        tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //添加删除线
        tv_full_reduce = (TextView) header.findViewById(R.id.tv_full_reduce);
        tv_price.setText("¥" + groupMemberPrice + "");
        tv_original_price.setText("¥" + groupPrice + "");
        tv_full_reduce.setText("满" + reduceFreightCount + "包免运费");
        // 规格
        tv_goods_type = (TextView) header.findViewById(R.id.tv_goods_type);
        tv_goods_type.setText(goods_name);
        // 促销
        ll_promotion = (LinearLayout) header.findViewById(R.id.ll_promotion);
        tv_promotion = (TextView) header.findViewById(R.id.tv_promotion);
        ll_promotion.setOnClickListener(this);
//        LogUtils.i("--promotion_jsonArray1--", "--promotion_jsonArray1--" + promotion_jsonArray.toString());
        if (promotion_jsonArray != null && promotion_jsonArray.size() > 0) {
            ll_promotion.setVisibility(View.VISIBLE);
            tv_promotion.setText(promotion_jsonArray.get(0).toString().trim());
            for (int i = 0; i < promotion_jsonArray.size(); i++) {
//                                LogUtils.i("--data_jsonArray", "--data_jsonArray--" + data_jsonArray.get(i));
                list_promotionItem.add(promotion_jsonArray.get(i).toString().trim());
            }

        } else {
            ll_promotion.setVisibility(View.GONE);
        }

        // 发货地址
        tv_station = (TextView) header.findViewById(R.id.tv_station);
        tv_station_phone = (TextView) header.findViewById(R.id.tv_station_phone);
        tv_station_address = (TextView) header.findViewById(R.id.tv_station_address);
        tv_station.setText(station);
        tv_station_phone.setText(salesman + " " + phone);
        tv_station_address.setText(address);



        // 收藏
        iv_isCollection = (ImageView) header.findViewById(R.id.iv_isCollection);
        if (isCollection == 1){
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

    private void setGroupView() {
        mBulletinViewSale.setAdapter(new BulletinBoardAdapter(getActivity(), list_groupData));
        mBulletinViewSale.setOnBulletinItemClickListener(new BulletinView.OnBulletinItemClickListener() {
            @Override
            public void onBulletinItemClick(int position) {
                // 跳到团购下单界面
                ToastUtils.showShort(getActivity(), list_groupData.get(position).getGroupScale());
            }
        });
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
                        // 拼团轮播
                        groupCount = data_jsonObject.getInteger("groupCount");

                        JSONArray groupData_jsonArray = (JSONArray) data_jsonObject.getJSONArray("groupData");
                        String groupData_json_str = JSONObject.toJSONString(groupData_jsonArray);//将array数组转换成字符串
                        List<GroupDataItem> list_groupData_data = JSONObject.parseArray(groupData_json_str, GroupDataItem.class);//把字符串转换成集合
                        if (list_groupData_data != null && list_groupData_data.size() > 0) {
                            list_groupData.addAll(list_groupData_data);
                            LogUtils.i("--list_groupData--", "--list_groupData--" + list_groupData.size());
                        }
                        // 价格
                        groupMemberPrice = data_jsonObject.getDouble("groupMemberPrice"); // 团购价
                        groupPrice = data_jsonObject.getDouble("groupPrice"); // 原价
                        reduceFreightCount = data_jsonObject.getInteger("reduceFreightCount"); // 满减

                        // 规格
                        goods_name = data_jsonObject.getString("name");

                        // 促销
                        promotion_jsonArray = (JSONArray) response_jsonObject.getJSONArray("promotion");
//                        LogUtils.i("--promotion_jsonArray00--", "--promotion_jsonArray00--" + promotion_jsonArray);

                        // 发货地址
                        JSONObject station_jsonObject = data_jsonObject.getJSONObject("station");
                        address = station_jsonObject.getString("address");
                        phone = station_jsonObject.getString("phone");
                        station = station_jsonObject.getString("station"); // 服务站名
                        salesman = station_jsonObject.getString("name");

                        // 购买按钮
                        directBuyPrice = data_jsonObject.getDouble("directBuyPrice");
                        groupBuyPrice = data_jsonObject.getDouble("groupBuyPrice");
                        // 最小购买数量
                        capacityCount = data_jsonObject.getInteger("capacityCount");

                        // 运费
                        freight = data_jsonObject.getDouble("freight");

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
            case R.id.ll_search_more_group: // 查看更多拼团
                Intent intent = new Intent(getActivity(), GroupBuyActivity.class);
                intent.putExtra("searchMoreGroup", "searchMoreGroup");
                startActivity(intent);
                break;
            case R.id.ll_search_more_comment: // 查看更多商品评价
                callBackValue.SendValue2("searchMoreComment");
                break;
            case R.id.iv_isCollection: // 收藏
                commodityCollection();
                break;
            case R.id.ll_promotion: // 促销
                BottomDialog.create(getChildFragmentManager())
                        .setViewListener(new BottomDialog.ViewListener() {
                            @Override
                            public void bindView(View v) {
                                initViewBottom1(v);
                            }
                        })
                        .setLayoutRes(R.layout.layout_list_promotion)
                        .setDimAmount(0.9f)
                        .setTag("BottomDialog")
                        .show();
                break;
            case R.id.ll_single_buy: // 单独购买按钮
                Intent intent1 = new Intent(getActivity(), SingleBuyOrderActivity.class);
                intent1.putExtra("commodityId", commodityId);
                intent1.putExtra("scale", scale);
                intent1.putExtra("capacityCount", capacityCount);
                intent1.putExtra("station", station);
                intent1.putExtra("listImg", listImg);
                intent1.putExtra("goods_name", goods_name);
                intent1.putExtra("groupBuyPrice", groupBuyPrice);
                intent1.putExtra("freight", freight);
                startActivity(intent1);
                getActivity().finish();
                break;
            case R.id.ll_group_buy: // 拼团购买按钮 (立即加入)
//                BottomDialog.create(getChildFragmentManager())
//                        .setViewListener(new BottomDialog.ViewListener() {
//                            @Override
//                            public void bindView(View v) {
//                                getGroupScale(v);
//                            }
//                        })
//                        .setLayoutRes(R.layout.bottom_group_buy_person_num)
//                        .setDimAmount(0.9f)
//                        .setTag("BottomDialog")
//                        .show();
                Intent intent2 = new Intent(getActivity(), GroupBuyOrderActivity.class);
                intent2.putExtra("commodityId", commodityId);
//                intent2.putExtra("scale", scale);
                intent2.putExtra("capacityCount", capacityCount);
                intent2.putExtra("station", station);
                intent2.putExtra("listImg", listImg);
                intent2.putExtra("goods_name", goods_name);
                intent2.putExtra("groupBuyPrice", groupBuyPrice);
                intent2.putExtra("freight", freight);
                intent2.putExtra("groupNo", groupNo);
                intent2.putExtra("isFromWeb", true);
                intent2.putExtra("userId", userId);
                startActivity(intent2);
                getActivity().finish();
                break;
        }
    }


    private void initViewBottom1(final View view) {
        ListView lv_promotion = (ListView) view.findViewById(R.id.lv_promotion);
        if (promotionAdapter == null) {
            promotionAdapter = new PromotionAdapter(getActivity(), list_promotionItem);
        }
        lv_promotion.setAdapter(promotionAdapter);
    }

    private void initViewBottom2(final View view) {
        LabelsView labelsView = (LabelsView) view.findViewById(R.id.labels);
        TextView tv_sure = (TextView) view.findViewById(R.id.tv_sure);
        labelsView.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
//                Toast.makeText(getActivity(), position + " : " + data,
//                        Toast.LENGTH_LONG).show();
                scale = list_groupPersonNumItem.get(position).getConfigCode();

            }
        });
        labelsView.setLabels(list_groupPersonNumItem, new LabelsView.LabelTextProvider<GroupPersonNumItem>() {
            @Override
            public CharSequence getLabelText(TextView label, int position, GroupPersonNumItem data) {
                return data.getConfigName();
            }
        });
        labelsView.setSelects(0);
        scale = list_groupPersonNumItem.get(0).getConfigCode();

        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), GroupBuyOrderActivity.class);
                intent1.putExtra("commodityId", commodityId);
                intent1.putExtra("scale", scale);
                intent1.putExtra("capacityCount", capacityCount);
                intent1.putExtra("station", station);
                intent1.putExtra("listImg", listImg);
                intent1.putExtra("goods_name", goods_name);
                intent1.putExtra("groupBuyPrice", groupBuyPrice);
                intent1.putExtra("freight", freight);
                intent1.putExtra("groupNo", groupNo);
                intent1.putExtra("isFromWeb", true);
                startActivity(intent1);
                getActivity().finish();
            }
        });
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

    // 获取团规模
    private void getGroupScale(final View v) {
        list_groupPersonNumItem.clear();
        paramsMap_getGroupScale.put("userId", userId);
        url_getGroupScale = Constants.baseUrl + Constants.url_getGroupScale;
        Okhttp3Utils.getAsycRequest(url_getGroupScale, paramsMap_getGroupScale, new ResultCallback() {
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
                        JSONArray data_jsonArray = (JSONArray) response_jsonObject.getJSONArray("data");
                        String data_json_str = JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                        List<GroupPersonNumItem> list_data = JSONObject.parseArray(data_json_str, GroupPersonNumItem.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0) {
                            list_groupPersonNumItem.addAll(list_data);
                            initViewBottom2(v);
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
