package ys.app.feed.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import q.rorbin.badgeview.QBadgeView;
import ys.app.feed.R;
import ys.app.feed.about.KnowAboutActivity;
import ys.app.feed.adapter.ModuleAdapter;
import ys.app.feed.bargain.ZeroBargainActivity;
import ys.app.feed.batching.BatchingActivity;
import ys.app.feed.bean.ModuleItem;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.discount.DiscountActivity;
import ys.app.feed.discount.GiftPackageActivity;
import ys.app.feed.extension.ExtensionActivity;
import ys.app.feed.goods.AllGoodsActivity;
import ys.app.feed.groupbuy.GroupBuyActivity;
import ys.app.feed.halfprice.HalfPriceActivity;
import ys.app.feed.intention.IntentionActivity;
import ys.app.feed.login.LoginActivity;
import ys.app.feed.message.MessageListActivity;
import ys.app.feed.score.LuckDrawActivity;
import ys.app.feed.score.ScoreRedPacketActivity;
import ys.app.feed.scoreexchange.ScoreExchangeActivity;
import ys.app.feed.search.SearchActivity;
import ys.app.feed.start.SplashActivity;
import ys.app.feed.utils.AppUtils;
import ys.app.feed.utils.DensityUtil;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ScreenUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.banner.FlyBanner;
import ys.app.feed.widget.dialog.LuckyTableDialog;
import ys.app.feed.widget.dialog.ScoreRedPacketDialog;
import ys.app.feed.widget.dialog.SelfDialog;
import ys.app.feed.widget.dialog.UpdateDialog;

/**
 * 营养商城
 */
public class ShoppingMallFragment extends Fragment implements View.OnClickListener {

    private View rootView, header;

    private SelfDialog selfDialog;


    // 版本更新
    private UpdateDialog updateDialog;

    private ImageView iv_notice;
    private LinearLayout ll_notice;
    private LinearLayout ll_search;
    private TextView tv_hot;
    private FlyBanner banner;
    // 测试轮播图片
//    private String[] mImagesUrl = {
//            "http://img4.imgtn.bdimg.com/it/u=2430963138,1300578556&fm=23&gp=0.jpg",
//            "http://img1.imgtn.bdimg.com/it/u=2755648979,3568014048&fm=23&gp=0.jpg",
//            "http://img0.imgtn.bdimg.com/it/u=2272739960,4287902102&fm=23&gp=0.jpg",
//            "http://img3.imgtn.bdimg.com/it/u=1078051055,1310741362&fm=23&gp=0.jpg"
//    };
    List<String> imgesUrl = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ModuleAdapter adapter_module;
    private ArrayList<ModuleItem> list_module = new ArrayList<ModuleItem>();


    // 获取消息通知个数
    private String url_getUnreadMessageCount; // 获取消息通知个数接口url
    private String userId; // 用户名
    private HashMap<String, String> paramsMap_getUnreadMessageCount = new HashMap<String, String>();
    private String unreadMessageCount;
    // 获取轮播图片
    private String url_getBannerData; // 获取轮播图片接口url
    private HashMap<String, String> paramsMap_getBannerData = new HashMap<String, String>();

    private Integer isGiftPackage = 1; // 0.未领取|1.已领取 新人礼包

    // 十惠导购
    private LinearLayout ll_discount;
    private LinearLayout ll_money; // 0元砍价
    private LinearLayout ll_luck_draw; // 积分抽奖
    private LinearLayout ll_score_red_packet; // 积分红包
    // 饲料拼团
    private LinearLayout ll_group_buy;

    // 半价抢购
    private LinearLayout ll_half_price;

    // 积分兑换
    private LinearLayout ll_score_exchange;
    // 精准配料
    private LinearLayout ll_accurate_batching;
    // 推广中心
    private LinearLayout ll_promotion_center;


    // 检验更新
    private String url_inspectionUpdate; // url
    private String code; // 版本号
    private HashMap<String, String> paramsMap_inspectionUpdate = new HashMap<String, String>();
    private String url_apk;
    private String fileName; // apk名称(包括文件类型)
    private String content;
    private Integer isUpdate = 0; // 0.不需要更新|1.需要更新

    public ShoppingMallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the bottom_group_buy_person_num for this fragment
        rootView = inflater.inflate(R.layout.fragment_shopping_mall, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i("--ShoppingMallFragment--", "--ShoppingMallFragment--" );
        iv_notice = (ImageView) rootView.findViewById(R.id.iv_notice);

        ll_notice = (LinearLayout) rootView.findViewById(R.id.ll_notice);
        ll_search = (LinearLayout) rootView.findViewById(R.id.ll_search);
        ll_group_buy = (LinearLayout) rootView.findViewById(R.id.ll_group_buy);
        ll_half_price = (LinearLayout) rootView.findViewById(R.id.ll_half_price);
        ll_score_exchange = (LinearLayout) rootView.findViewById(R.id.ll_score_exchange);
        // 精准配料
        ll_accurate_batching = (LinearLayout) rootView.findViewById(R.id.ll_accurate_batching);
        // 推广中心
        ll_promotion_center = (LinearLayout) rootView.findViewById(R.id.ll_promotion_center);

        ll_notice.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        ll_group_buy.setOnClickListener(this);
        ll_half_price.setOnClickListener(this);
        ll_score_exchange.setOnClickListener(this);
        ll_accurate_batching.setOnClickListener(this);
        ll_promotion_center.setOnClickListener(this);

        // 轮播图
        banner = (FlyBanner) rootView.findViewById(R.id.banner);
//        banner.setOnItemClickListener(new FlyBanner.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                ToastUtils.showShort(getActivity(), "点击了第" + position + "张图片");
//            }
//        });

//        LogUtils.i("--screenHeight--", "--screenHeight--" + DensityUtil.dip2px(getActivity(),150));
//        LogUtils.i("--screenWidth--", "--screenWidth--" + DensityUtil.dip2px(getActivity(),10));

        // 圆圈分类
        setModuleList();
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 4);
        StaggeredGridLayoutManager sg_layoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview_module);
        mRecyclerView.setLayoutManager(sg_layoutManager);
        adapter_module = new ModuleAdapter(list_module);
        adapter_module.setClickCallBack(new ModuleAdapter.ItemClickCallBack() {
            @Override
            public void onItemClick(int pos) {
                if (TextUtils.equals(list_module.get(pos).getModuleName(), "0元砍价")) {
                    Intent intent = new Intent(getActivity(), ZeroBargainActivity.class);
                    startActivity(intent);
                } else if (TextUtils.equals(list_module.get(pos).getModuleName(), "全部商品")) {
                    Intent intent = new Intent(getActivity(), AllGoodsActivity.class);
                    startActivity(intent);
                } else if (TextUtils.equals(list_module.get(pos).getModuleName(), "了解慧河")) {
                    Intent intent = new Intent(getActivity(), KnowAboutActivity.class);
                    startActivity(intent);
                } else if (TextUtils.equals(list_module.get(pos).getModuleName(), "充值增值")) {
//                    ToastUtils.showShort(getActivity(), "优惠礼包");
                    Intent intent = new Intent(getActivity(), GiftPackageActivity.class);
                    startActivity(intent);
                } else if (TextUtils.equals(list_module.get(pos).getModuleName(), "组团购买")) {
//                    ToastUtils.showShort(getActivity(), "优惠礼包");
                    Intent intent = new Intent(getActivity(), GroupBuyActivity.class);
                    startActivity(intent);
                }
            }
        });
        mRecyclerView.setAdapter(adapter_module);


        // 用户id
        userId = (String) SPUtils.get(getActivity(), "userId", "");
        getUnreadMessageCount();
        getBannerData();

        // 十惠导购
        ll_discount = (LinearLayout) rootView.findViewById(R.id.ll_discount);
        ll_money = (LinearLayout) rootView.findViewById(R.id.ll_money); // 0元砍价
        ll_luck_draw = (LinearLayout) rootView.findViewById(R.id.ll_luck_draw); // 积分抽奖
        ll_score_red_packet = (LinearLayout) rootView.findViewById(R.id.ll_score_red_packet); // 积分红包
        ll_discount.setOnClickListener(this);
        ll_money.setOnClickListener(this);
        ll_luck_draw.setOnClickListener(this);
        ll_score_red_packet.setOnClickListener(this);


        // 新人礼包
        isGiftPackage = (Integer) SPUtils.get(getActivity(), "isGiftPackage", 1);
        if (isGiftPackage == 0) {  // 0.未领取|1.已领取
            selfDialog = new SelfDialog(getActivity());
            selfDialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
                @Override
                public void onYesClick() {
//                    Toast.makeText(getActivity(), "点击了--确定--按钮", Toast.LENGTH_LONG).show();
//                    selfDialog.dismiss();
                }
            });
            selfDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                @Override
                public void onNoClick() {
//                    Toast.makeText(getActivity(), "点击了--取消--按钮", Toast.LENGTH_LONG).show();
                    selfDialog.dismiss();
                }
            });

            WindowManager.LayoutParams lp = selfDialog.getWindow().getAttributes();
            lp.dimAmount = 0.5f;
            selfDialog.getWindow().setAttributes(lp);
            selfDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            selfDialog.show();
        }

        inspectionUpdate();
    }

    private void setModuleList() {
//        list_module.add(new ModuleItem("友料圈", R.mipmap.group_buying));
//        list_module.add(new ModuleItem("友料圈", R.mipmap.group_buying));

        list_module.add(new ModuleItem("0元砍价", R.mipmap.zero_bargain));
        list_module.add(new ModuleItem("了解慧河", R.mipmap.huihe_product));
        list_module.add(new ModuleItem("充值增值", R.mipmap.gift_pack));
        list_module.add(new ModuleItem("全部商品", R.mipmap.product_classify));
        list_module.add(new ModuleItem("组团购买", R.mipmap.group_buying));

//        list_module.add(new ModuleItem("慧合产品", R.mipmap.huihe_product));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search: // 搜索
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_notice: // 消息通知
                Intent intent1 = new Intent(getActivity(), MessageListActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_discount: // 十惠购料
                Intent intent2 = new Intent(getActivity(), DiscountActivity.class);
                startActivity(intent2);
                break;
            case R.id.ll_money: // 0元砍价
                Intent intent21 = new Intent(getActivity(), ZeroBargainActivity.class);
                startActivity(intent21);
                break;
            case R.id.ll_luck_draw: // 积分抽奖
                //抽奖转盘
                Intent intent22 = new Intent(getActivity(), LuckDrawActivity.class);
                startActivity(intent22);
                break;
            case R.id.ll_score_red_packet: // 积分红包
//                showScoreRedPacketDialog();
                Intent intent6 = new Intent(getActivity(), ScoreRedPacketActivity.class);
                startActivity(intent6);
                break;
            case R.id.ll_group_buy: // 拼团
                Intent intent3 = new Intent(getActivity(), GroupBuyActivity.class);
                intent3.putExtra("module", 0); // 0饲料拼团，1半价抢购，2积分兑换
                startActivity(intent3);
                break;
            case R.id.ll_half_price: // 半价抢购
                Intent intent4 = new Intent(getActivity(), HalfPriceActivity.class);
                intent4.putExtra("module", 1); // 0饲料拼团，1半价抢购，2积分兑换
                startActivity(intent4);
                break;
            case R.id.ll_score_exchange: // 积分兑换
                Intent intent5 = new Intent(getActivity(), ScoreExchangeActivity.class);
                intent5.putExtra("module", 2); // 0饲料拼团，1半价抢购，2积分兑换
                startActivity(intent5);
                break;
            case R.id.ll_accurate_batching: // 精准配料
                Intent intent7 = new Intent(getActivity(), BatchingActivity.class);
                startActivity(intent7);
                break;
            case R.id.ll_promotion_center: // 推广中心
                Intent intent8 = new Intent(getActivity(), ExtensionActivity.class);
                startActivity(intent8);
                break;
        }
    }


    // 获取消息个数
    private void getUnreadMessageCount() {
        paramsMap_getUnreadMessageCount.put("userId", userId);
        url_getUnreadMessageCount = Constants.baseUrl + Constants.url_getUnreadMessageCount;
        Okhttp3Utils.getAsycRequest(url_getUnreadMessageCount, paramsMap_getUnreadMessageCount, new ResultCallback() {
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
                        unreadMessageCount = data_jsonObject.getString("unreadMessageCount");
                        if (!TextUtils.isEmpty(unreadMessageCount)) {
                            new QBadgeView(getActivity())
                                    .bindTarget(ll_notice)
                                    .setBadgeTextColor(Color.WHITE)
                                    .setGravityOffset(0, 0, true)
                                    .setBadgePadding(4, true)
                                    .setBadgeNumber(Integer.parseInt(unreadMessageCount));
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

    // 获取轮播图片
    private void getBannerData() {
        url_getBannerData = Constants.baseUrl + Constants.url_getBannerData;
        Okhttp3Utils.getAsycRequest(url_getBannerData, paramsMap_getBannerData, new ResultCallback() {
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
//                        String data_json_str = JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                        if (data_jsonArray != null && data_jsonArray.size() > 0) {
                            for (int i = 0; i < data_jsonArray.size(); i++) {
//                                LogUtils.i("--data_jsonArray", "--data_jsonArray--" + data_jsonArray.get(i));
                                imgesUrl.add(data_jsonArray.get(i).toString().trim());
                            }
                            banner.setImagesUrl(imgesUrl);
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

    // 检验更新
    private void inspectionUpdate() {
        code = AppUtils.getVersionCode(getActivity()) + "";
        paramsMap_inspectionUpdate.put("code", code);
        url_inspectionUpdate = Constants.baseUrl + Constants.url_inspectionUpdate;
        Okhttp3Utils.getAsycRequest(url_inspectionUpdate, paramsMap_inspectionUpdate, new ResultCallback() {
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
                        content = data_jsonObject.getString("content");
                        isUpdate = data_jsonObject.getInteger("isUpdate"); // 0.不需要更新|1.需要更新
                        if (isUpdate == 1){
                            showUpdateDialog();
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

    // 版本更新dialog
    private void showUpdateDialog() {
        updateDialog = new UpdateDialog(getActivity());
        updateDialog.setContent(content);
        updateDialog.setYesOnclickListener("确定", new UpdateDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {

                url_apk = Constants.url_apk;
                downApk(url_apk, "app-release.apk");

//                updateDialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = updateDialog.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        updateDialog.getWindow().setAttributes(lp);
        updateDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        updateDialog.show();

    }

    private void downApk(String url, final String fileName){
        ToastUtils.showShort(getActivity(), "开始更新...");
        Okhttp3Utils.downLoadFile(url_apk, fileName, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(getActivity(), "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    installApk(getActivity(), fileName);
                } else {
                    ToastUtils.showShort(getActivity(), "下载apk失败！");
                }
            }
        });
    }

    private void installApk(Context context, final String fileName) {
//        Intent installintent = new Intent();
//        installintent.setAction(Intent.ACTION_VIEW);
//        // 在Boradcast中启动活动需要添加Intent.FLAG_ACTIVITY_NEW_TASK
//        installintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        installintent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/fileName")),
//                "application/vnd.android.package-archive");//存储位置为Android/data/包名/file/Download文件夹
//        context.startActivity(installintent);

        //设置apk存储路径和名称
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/NewFeed/";
        File file = new File(path + fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //安卓7.0以上需要在在Manifest.xml里的application里，设置provider路径
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(getActivity(), "ys.app.feed.fileprovider", new File(file.getPath()));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        startActivity(intent);

    }




}
