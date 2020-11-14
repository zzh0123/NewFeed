package ys.app.feed.order;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

/**
 *  我的订单
 */

public class MyOrderActivity extends FragmentActivity implements View.OnClickListener{

    private LinearLayout ll_back;

    private IndicatorViewPager indicatorViewPager;
    private ViewPager viewPager;
    private ScrollIndicatorView scroll_indicator;
    private LayoutInflater inflate;
    private String[] order_type_names = {"普通订单", "团购订单", "半价订单", "积分订单", "砍价订单"};
    public List<Fragment> fragment_list = new ArrayList<Fragment>();
    private int unSelectTextColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        fragment_list.add(new NormalOrderFragment());
        fragment_list.add(new GroupBuyOrderFragment());
        fragment_list.add(new HalfPriceOrderFragment());
        fragment_list.add(new ScoreExchangeOrderFragment());
        fragment_list.add(new ZeroBargainOrderFragment());

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        scroll_indicator = (ScrollIndicatorView) findViewById(R.id.scroll_indicator);
        float unSelectSize = 12;
        float selectSize = unSelectSize * 1.3f;
        scroll_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(getResources().getColor(R.color.text_green), Color.GRAY).setSize(selectSize, unSelectSize));

        scroll_indicator.setScrollBar(new ColorBar(this, getResources().getColor(R.color.text_green), 4));

        unSelectTextColor = getResources().getColor(R.color.text_grey);
        // 设置滚动监听
        scroll_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(getResources().getColor(R.color.text_black), unSelectTextColor));

        viewPager.setOffscreenPageLimit(0);
        indicatorViewPager = new IndicatorViewPager(scroll_indicator, viewPager);
        inflate = LayoutInflater.from(getApplicationContext());
        indicatorViewPager.setAdapter(new MyOrderActivity.MyAdapter(getSupportFragmentManager()));

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

    private int size = order_type_names.length;

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
            textView.setText(order_type_names[position % order_type_names.length]);
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
}
