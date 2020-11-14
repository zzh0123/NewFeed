package ys.app.feed.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ys.app.feed.R;
import ys.app.feed.about.KnowAboutActivity;
import ys.app.feed.adapter.LuckDrawTenAdapter;
import ys.app.feed.adapter.ModuleAdapter;
import ys.app.feed.bargain.ZeroBargainActivity;
import ys.app.feed.bean.GiftItem;
import ys.app.feed.bean.ModuleItem;
import ys.app.feed.discount.GiftPackageActivity;
import ys.app.feed.goods.AllGoodsActivity;

/**
 * 创建自定义的dialog，主要学习其实现原理
 * Created by chengguo on 2016/3/22.
 */
public class LuckDrawTenDialog extends Dialog {

    private ImageView yes;//确定按钮
    private TextView no;//取消按钮
    //确定文本和取消文本的显示内容
    private String yesStr, noStr;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    private RecyclerView mRecyclerView;
    private LuckDrawTenAdapter adapter_ten;
    private ArrayList<GiftItem> list_giftItem_ten = new ArrayList<GiftItem>();

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

    public LuckDrawTenDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_luck_draw_ten_dialog);
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
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
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
    private void initData() {
        StaggeredGridLayoutManager sg_layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_result);
        mRecyclerView.setLayoutManager(sg_layoutManager);
        adapter_ten = new LuckDrawTenAdapter(list_giftItem_ten);
        mRecyclerView.setAdapter(adapter_ten);
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = (ImageView) findViewById(R.id.yes);
        no = (TextView) findViewById(R.id.no);
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

    public void setResultTenList(ArrayList<GiftItem> list_giftItem_ten){
        this.list_giftItem_ten = list_giftItem_ten;
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
}
