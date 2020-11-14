package ys.app.feed.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnPageChangeListener;

import java.util.ArrayList;

import ys.app.feed.MainActivity;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.goods.AllGoodsActivity;
import ys.app.feed.goods.GoodsDetailActivity;
import ys.app.feed.login.LoginActivity;
import ys.app.feed.utils.SPUtils;

public class StartActivity extends Activity implements View.OnClickListener {

    private TextView tv_go_to_app; //
    private ConvenientBanner convenientBanner; // 返回
    private ArrayList<Integer> start_imag_list = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView() {
        start_imag_list.add(R.mipmap.guide_1);
        start_imag_list.add(R.mipmap.guide_2);
        start_imag_list.add(R.mipmap.guide_3);
        tv_go_to_app = (TextView) findViewById(R.id.tv_go_to_app);
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);

        tv_go_to_app.setOnClickListener(this);

        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Holder createHolder(View itemView) {
                return new LocalImageHolderView(itemView);
            }

            @Override
            public int getLayoutId() {
                //设置加载哪个布局
                return R.layout.item_guide_page;
            }

        }, start_imag_list)
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setPointViewVisible(true)
                .setCanLoop(false)
                .setOnPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    }

                    @Override
                    public void onPageSelected(int index) {
                        //总共添加了三张图片，如果index为2表示到了最后一张图片，隐藏下面的指示器，显示跳转到主页的按钮
                        if (index == 2) {
                            tv_go_to_app.setVisibility(View.VISIBLE);
                            convenientBanner.setPointViewVisible(false);
                        } else {
                            tv_go_to_app.setVisibility(View.GONE);
                            convenientBanner.setPointViewVisible(true);

                        }
                    }
                });
    }

    /**
     * 轮播图2 对应的holder
     */
    public class LocalImageHolderView extends Holder<Integer> {
        private ImageView mImageView;

        //构造器
        public LocalImageHolderView(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView(View itemView) {
            mImageView = itemView.findViewById(R.id.iv_guide_page);
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        @Override
        public void updateUI(Integer data) {
            mImageView.setImageResource(data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_go_to_app: // 返回
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                SPUtils.put(StartActivity.this, "isFirst", "0"); // 1是0否
                startActivity(intent);
                finish();
                break;
        }
    }
}
