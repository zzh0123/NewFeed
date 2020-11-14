package ys.app.feed.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;

import ys.app.feed.R;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

/**
 * 创建自定义的dialog，主要学习其实现原理
 * Created by chengguo on 2016/3/22.
 */
public class SelfDialog extends Dialog {

    private TextView yes;//确定按钮
    private ImageView no;//取消按钮
    //确定文本和取消文本的显示内容
    private String yesStr, noStr;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    // 获取新人礼包
    private String url_getGiftPackage; // 获取新人礼包接口url
    private HashMap<String, String> paramsMap_getGiftPackage = new HashMap<String, String>();

    private Context context;
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

    public SelfDialog(Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_dialog);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
//        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGiftPackage();
//                if (yesOnclickListener != null) {
//                    yesOnclickListener.onYesClick();
//                }
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
    }

    /**
     * 初始化界面控件的显示数据
     */
//    private void initData() {
//    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = (TextView) findViewById(R.id.yes);
        no = (ImageView) findViewById(R.id.no);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
//        titleStr = title;
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

    // 获取新人礼包
    private void getGiftPackage() {
        yes.setClickable(false);
        // 用户id
        String userId = (String) SPUtils.get(context, "userId", "");
        paramsMap_getGiftPackage.put("userId", userId);
        url_getGiftPackage = Constants.baseUrl + Constants.url_getGiftPackage;
        Okhttp3Utils.getAsycRequest(url_getGiftPackage, paramsMap_getGiftPackage, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(context, "服务器未响应！");
                yes.setClickable(true);
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        SPUtils.put(context, "isGiftPackage", 1);
                        ToastUtils.showShort(context, "领取成功！");
                    } else {
//                        ToastUtils.showShort(getActivity(), resultInfo.getMessage());
                        ToastUtils.showShort(context, resultInfo.getMessage());
                        LogUtils.i("getGiftPackage", "--getGiftPackage--" + resultInfo.getMessage());
                    }
                    yes.setClickable(false);
                    dismiss();
                } else {
                    ToastUtils.showShort(context, "服务器返回数据为空！");
                    yes.setClickable(true);
                }
            }
        });
    }
}
