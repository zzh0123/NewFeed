package ys.app.feed.halfprice;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.goods.CommentFragment;
import ys.app.feed.goods.GoodsDetailFragment;
import ys.app.feed.goods.GoodsFragment;
import ys.app.feed.utils.LogUtils;

/**
 * 半价商品详情
 */
public class HalfPriceGoodsDetailActivity extends FragmentActivity implements View.OnClickListener , HalfPriceGoodsFragment.CallBackValue {

    private LinearLayout ll_back; // 返回

    private IndicatorViewPager indicatorViewPager;
    private ViewPager viewPager;
    private ScrollIndicatorView scroll_indicator;
    private LayoutInflater inflate;
    private String[] names = {"商品", "详情", "评论"};
    public List<Fragment> fragment_list = new ArrayList<Fragment>();
    private int unSelectTextColor;

    private String commodityId;
    private String classType; // 1.全部商品列表 | 2.团购直购列表 | 3.半价列表 | 4.积分列表 | 5.砍价列表;

    // 详情页的图片
    private String detailImg;
    // 跳转评论fg
    private String searchMoreComment;

    private GoodsDetailFragment goodsDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_half_price_goods_detail);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        commodityId = getIntent().getStringExtra("commodityId");
        classType = getIntent().getStringExtra("classType");

        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        HalfPriceGoodsFragment halfPriceGoodsFragment = new HalfPriceGoodsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("commodityId", commodityId);
        bundle.putString("classType", classType);
        halfPriceGoodsFragment.setArguments(bundle);

        goodsDetailFragment = new GoodsDetailFragment();

        CommentFragment commentFragment = new CommentFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString("commodityId", commodityId);
        commentFragment.setArguments(bundle2);

        fragment_list.add(halfPriceGoodsFragment);
        fragment_list.add(goodsDetailFragment);
        fragment_list.add(commentFragment);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        scroll_indicator = (ScrollIndicatorView) findViewById(R.id.scroll_indicator);
        float unSelectSize = 12;
        float selectSize = unSelectSize * 1.3f;
        scroll_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(getResources().getColor(R.color.text_green), Color.GRAY).setSize(selectSize, unSelectSize));

        scroll_indicator.setScrollBar(new ColorBar(this, getResources().getColor(R.color.text_green), 4));

        unSelectTextColor = getResources().getColor(R.color.text_grey);
        // 设置滚动监听
        scroll_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(getResources().getColor(R.color.text_black), unSelectTextColor));

        viewPager.setOffscreenPageLimit(3);
        indicatorViewPager = new IndicatorViewPager(scroll_indicator, viewPager);
        inflate = LayoutInflater.from(getApplicationContext());
        indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        // 设置是否自动布局,默认true ，自动布局
        scroll_indicator.setSplitAuto(true);
//        viewPager.setCurrentItem(2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }


    private int size = names.length;

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_top, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(names[position % names.length]);
            int padding = dipToPix(10);
            textView.setPadding(padding, 0, padding, 0);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
//            GoodsFragment fragment = new GoodsFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt(GoodsFragment.INTENT_INT_INDEX, position);
//            fragment.setArguments(bundle);
            return fragment_list.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
            // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
            return PagerAdapter.POSITION_NONE;
        }

    }

    /**
     * 根据dip值转化成px值
     *
     * @param dip
     * @return
     */
    private int dipToPix(float dip) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
        return size;
    }

    //要获取的值  就是这个参数的值
    @Override
    public void SendValue(String strValue) {
        // TODO Auto-generated method stub
        detailImg = strValue;
        Bundle bundle1 = new Bundle();
        bundle1.putString("detailImg", detailImg);
        goodsDetailFragment.setArguments(bundle1);
    }
    public String getDetailImg() {
        return detailImg;
    }
    @Override
    public void SendValue2(String strValue) {
        // TODO Auto-generated method stub
        searchMoreComment = strValue;
        LogUtils.i("--searchMoreComment--", "--searchMoreComment--");
        if (TextUtils.equals("searchMoreComment", searchMoreComment)){
            viewPager.setCurrentItem(2);
        }
    }
}
