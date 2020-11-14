package ys.app.feed.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ys.app.feed.R;
import ys.app.feed.adapter.CouponOrderSelectAdapter;
import ys.app.feed.address.AddAddressActivity;
import ys.app.feed.bean.CouponOrderSelectItem;
import ys.app.feed.bean.GiftItem;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.coupon.OrderSelectCouponActivity;
import ys.app.feed.score.LuckDrawActivity;
import ys.app.feed.utils.DrawLotteryUtil;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.luckyturntable.LuckyMonkeyPanelView;

/**
 * 创建自定义的dialog，主要学习其实现原理
 * Created by chengguo on 2016/3/22.
 */
public class LuckyTableDialog extends Dialog {

    private ArrayList<GiftItem> list_giftItem = new ArrayList<GiftItem>();
    private LuckyMonkeyPanelView luckyPanelView;
    private long drawTime; //抽奖时间
    private int MARK_LUCKY = 7; //中奖标记
    private static Handler handler = new Handler();

    private Activity activity;
    private ImageView id_draw_btn;//确定按钮
    private ImageView no;//取消按钮
    private TextView tv_buy_luck_draw_coupon; // 购买抽奖券
    private TextView tv_luck_draw_coupon_count; // 您有抽奖券0张

    private TextView tv_uck_draw_one; // 抽奖按钮1次
    private TextView tv_uck_draw_ten; // 抽奖按钮10次

    //确定文本和取消文本的显示内容
    private String yesStr, noStr;

    private String titleStr;//从外界设置的title文本

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    private String userId; // 用户id
    private String type; // 用户id
    private ArrayList<String> code_list = new ArrayList<String>();
    private ArrayList<GiftItem> list_giftItem_ten = new ArrayList<GiftItem>();
    // 扣除用户抽奖券
    private String url_reduceLuckCoupon; // url
    private HashMap<String, String> paramsMap_reduceLuckCoupon = new HashMap<String, String>();


    // 抽奖
    private String url_mall_luckDraw; // url
    private HashMap<String, Object> paramsMap_mall_luckDraw = new HashMap<String, Object>();
    private JSONObject paramsJsonObject_luckDraw;
    private String code;

    // 按照类型获取用户未使用的优惠券
    private String url_getCouponByType; // 获取地址接口url
    private HashMap<String, String> paramsMap_getCouponByType = new HashMap<String, String>();

    private LuckDrawTenDialog luckDrawTenDialog;
    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    public LuckyTableDialog(Context context) {
        super(context, R.style.MyDialog);
        this.activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lucky_table_dialog);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        // 抽奖按钮1次
        tv_uck_draw_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LogUtils.i("--哈哈--","--哈哈--" );
                if (System.currentTimeMillis() - drawTime < 5000) {
                    Toast.makeText(activity, "心急吃不了热豆腐，请5秒后再点击哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                type = "1";
                reduceLuckCoupon();

            }
        });
        // 抽奖按钮10次
        tv_uck_draw_ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LogUtils.i("--哈哈--","--哈哈--" );
                if (System.currentTimeMillis() - drawTime < 5000) {
                    Toast.makeText(activity, "心急吃不了热豆腐，请5秒后再点击哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                type = "10";
                reduceLuckCoupon();

            }
        });

        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
        tv_buy_luck_draw_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
//        if (titleStr != null) {
//            tv_luck_draw_coupon_count.setText(titleStr);
//        }
        getCouponByType();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        luckyPanelView = (LuckyMonkeyPanelView) findViewById(R.id.lucky_panel);
//        id_draw_btn = (ImageView) findViewById(R.id.id_draw_btn);
        no = (ImageView) findViewById(R.id.no);
        tv_buy_luck_draw_coupon = (TextView) findViewById(R.id.tv_buy_luck_draw_coupon);
        tv_luck_draw_coupon_count = (TextView) findViewById(R.id.tv_luck_draw_coupon_count);

        // 抽奖按钮1次
        tv_uck_draw_one = (TextView) findViewById(R.id.tv_uck_draw_one);
        // 抽奖按钮10次
        tv_uck_draw_ten = (TextView) findViewById(R.id.tv_uck_draw_ten);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
//        messageStr = message;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }

    private void getLuckOne() {

        long delay = 0; //延长时间
        long duration = System.currentTimeMillis() - drawTime;
        if (duration < 5000) {
            delay = 5000 - duration;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (activity.isFinishing()) {
                    return;
                }

                MARK_LUCKY = DrawLotteryUtil.drawGift(list_giftItem);
//                MARK_LUCKY = 6;
//                LogUtils.i("--MARK_LUCKY0--", MARK_LUCKY + "--MARK_LUCKY--");
//                if (MARK_LUCKY == 0){
//                    MARK_LUCKY = 1;
////                    LogUtils.i("--MARK_LUCKY--", MARK_LUCKY + "--MARK_LUCKY--" + list_giftItem.get(MARK_LUCKY).getConfigName());
//                } else {
////                    LogUtils.i("--MARK_LUCKY--", MARK_LUCKY + "--MARK_LUCKY--" + list_giftItem.get(MARK_LUCKY).getConfigName());
//                }
                LogUtils.i("--MARK_LUCKY--", MARK_LUCKY + "--MARK_LUCKY--" + list_giftItem.get(MARK_LUCKY).getConfigName());

                luckyPanelView.tryToStop(MARK_LUCKY); // 从0开始
                luckyPanelView.setGameListener(new LuckyMonkeyPanelView.LuckyMonkeyAnimationListener() {
                    @Override
                    public void onAnimationEnd() {
                        //延长1S弹出抽奖结果
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, list_giftItem.get(MARK_LUCKY).getConfigName(), Toast.LENGTH_SHORT).show();
                                code = list_giftItem.get(MARK_LUCKY).getConfigCode(); // 物品code
                                code_list.add(code);
                                luckDraw();

                            }
                        }, 1000);
                    }
                });
            }
        }, delay);
    }

    private void getLuckTen() {

        long delay = 0; //延长时间
        long duration = System.currentTimeMillis() - drawTime;
        if (duration < 5000) {
            delay = 5000 - duration;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (activity.isFinishing()) {
                    return;
                }
                for (int i=0; i<10; i++){
                    MARK_LUCKY = DrawLotteryUtil.drawGift(list_giftItem);
                    LogUtils.i("--MARK_LUCKY--", MARK_LUCKY + "--MARK_LUCKY--" + list_giftItem.get(MARK_LUCKY).getConfigName());
                    code = list_giftItem.get(MARK_LUCKY).getConfigCode(); // 物品code
                    code_list.add(code);
                    list_giftItem_ten.add(list_giftItem.get(MARK_LUCKY));
                }

                luckyPanelView.tryToStop(MARK_LUCKY); // 从0开始
                luckyPanelView.setGameListener(new LuckyMonkeyPanelView.LuckyMonkeyAnimationListener() {
                    @Override
                    public void onAnimationEnd() {
                        //延长1S弹出抽奖结果
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                Toast.makeText(activity, list_giftItem.get(MARK_LUCKY).getConfigName(), Toast.LENGTH_SHORT).show();
//                                code = list_giftItem.get(MARK_LUCKY).getConfigCode(); // 物品code
                                luckDraw();

                            }
                        }, 1000);
                    }
                });
            }
        }, delay);
    }


    /**
     * 根据奖品等级计算出奖品位置+1
     *
     * @param prizeGrade
     * @return
     */
    private int getPrizePosition(int prizeGrade) {
        switch (prizeGrade) {
            case 0:
                return 0;
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            case 8:
                return 7;
//            case 6: //六等奖有三个位置，随机取一个
//                int[] position = {1, 3, 6};
//                Random random = new Random();
//                return position[random.nextInt(3)];
        }
        return prizeGrade;
    }


    /**
     * 奖品名称
     *
     * @param grade
     * @return
     */
    private String getPrizeName(int grade) {
        switch (grade) {
            case 0:
                return "5积分";
            case 1:
                return "5积分";
            case 2:
                return "添加剂（速能）";
            case 3:
                return "再来一次";
            case 4:
                return "代金券一张";
            case 5:
                return "砍价券一张";
            case 6:
                return "半价券一张";
            case 7:
                return "12积分";
            case 8:
                return "实物商品C001";
            default:
                return "";
        }
    }

    public void setLuckyRate(int MARK_LUCKY) {
        this.MARK_LUCKY = MARK_LUCKY;
    }

    public void setList_giftItem(ArrayList<GiftItem> list_giftItem) {
        this.list_giftItem = list_giftItem;
    }

    // 扣除用户抽奖券
    private void reduceLuckCoupon(){
        // 用户id
        userId = (String) SPUtils.get(activity, "userId", "");
        paramsMap_reduceLuckCoupon.put("type", type);
        paramsMap_reduceLuckCoupon.put("userId", userId);
        url_reduceLuckCoupon = Constants.baseUrl + Constants.url_reduceLuckCoupon;
        Okhttp3Utils.getAsycRequest(url_reduceLuckCoupon, paramsMap_reduceLuckCoupon, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(activity, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        //开始抽奖
                        if (!luckyPanelView.isGameRunning()) {
                            drawTime = System.currentTimeMillis();
                            luckyPanelView.startGame();
                            code_list.clear();
                            if (TextUtils.equals("1", type)){
                                getLuckOne();
                            } else {
                                list_giftItem_ten.clear();
                                getLuckTen();
                            }
                        }
                    } else {
                        ToastUtils.showShort(activity, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(activity, "服务器返回数据为空！");
                }
            }
        });
    }

    private void setData(){
        // 用户id
        userId = (String) SPUtils.get(activity, "userId", "");

        paramsMap_mall_luckDraw.put("userId", userId);
        paramsMap_mall_luckDraw.put("codes", code_list);

        String json = JSON.toJSONString(paramsMap_mall_luckDraw);
        paramsJsonObject_luckDraw = JSONObject.parseObject(json);
    }

    // 抽奖
    private void luckDraw(){
        setData();
        url_mall_luckDraw = Constants.baseUrl + Constants.url_mall_luckDraw;
        Okhttp3Utils.postAsycRequest_Json(url_mall_luckDraw, paramsJsonObject_luckDraw, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(activity, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(activity, "抽奖成功！");
                        if (TextUtils.equals("10", type)){
                           // 弹框
                            showTenDialog();
                        }
                        getCouponByType();
                    } else {
                        ToastUtils.showShort(activity, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(activity, "服务器返回数据为空！");
                }
            }
        });
    }

    // 按照类型获取用户未使用的优惠券
    private void getCouponByType() {
        userId = (String) SPUtils.get(activity, "userId", "");
        paramsMap_getCouponByType.put("type", "018005"); // 抽奖券
        paramsMap_getCouponByType.put("userId", userId);
        url_getCouponByType = Constants.baseUrl + Constants.url_getCouponByType;
        Okhttp3Utils.getAsycRequest(url_getCouponByType, paramsMap_getCouponByType, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(activity, "服务器未响应！");
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
                        List<CouponOrderSelectItem> list_data = JSONObject.parseArray(data_json_str, CouponOrderSelectItem.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0) {
                            String count = list_data.size() + "";
                            LogUtils.i("--count--", "--count--" + count);
                            tv_luck_draw_coupon_count.setText("您有抽奖券" + count + "张");
                        } else {
                            tv_luck_draw_coupon_count.setText("您有抽奖券0张");
                        }
                    } else {
                        ToastUtils.showShort(activity, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(activity, "服务器返回数据为空！");
                }
            }
        });
    }

    private void showTenDialog(){
        luckDrawTenDialog = new LuckDrawTenDialog(activity);
        luckDrawTenDialog.setResultTenList(list_giftItem_ten);
//        luckDrawTenDialog.setYesOnclickListener("确定", new LuckDrawTenDialog.onYesOnclickListener() {
//            @Override
//            public void onYesClick() {
////                    Toast.makeText(getActivity(), "点击了--确定--按钮", Toast.LENGTH_LONG).show();
//                luckDrawTenDialog.dismiss();
//            }
//        });
        luckDrawTenDialog.setNoOnclickListener("取消", new LuckDrawTenDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
//                    Toast.makeText(getActivity(), "点击了--取消--按钮", Toast.LENGTH_LONG).show();
                luckDrawTenDialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = luckDrawTenDialog.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        luckDrawTenDialog.getWindow().setAttributes(lp);
        luckDrawTenDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        luckDrawTenDialog.show();
    }
}
