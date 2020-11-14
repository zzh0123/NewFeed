package ys.app.feed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ys.app.feed.fragment.BatchingFragment;
import ys.app.feed.fragment.MyFragment;
import ys.app.feed.fragment.ExtensionFragment;
import ys.app.feed.fragment.ShoppingMallFragment;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.ScreenUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 初始化顶部栏显示
//    private ImageView titleLeftImv;
//    private TextView titleTv;
    // 定义4个Fragment对象
    private ShoppingMallFragment shoppingMallFragment;
    private BatchingFragment batchingFragment;
    private ExtensionFragment extensionFragment;
    private MyFragment myFragment;
    // 线性布局对象，用来存放Fragment对象
    private LinearLayout ll_container;
    // 定义每个选项中的相关控件
    private LinearLayout ll_first;
    private LinearLayout ll_second;
    private LinearLayout ll_third;
    private LinearLayout ll_fourth;
    private ImageView img_first;
    private ImageView img_second;
    private ImageView img_third;
    private ImageView img_fourth;
    private TextView tv_first;
    private TextView tv_second;
    private TextView tv_third;
    private TextView tv_fourth;
    // 定义FragmentManager对象管理器
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        fragmentManager = getSupportFragmentManager();
        initView(); // 初始化界面控件
        setChioceItem(0); // 初始化页面加载时显示第一个选项卡

//        int screenHeight = ScreenUtils.getScreenHeight(this);
//        int screenWidth = ScreenUtils.getScreenWidth(this);
//        LogUtils.i("--screenHeight--", "--screenHeight--" + screenHeight);
//        LogUtils.i("--screenWidth--", "--screenWidth--" + screenWidth);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        // 初始化页面标题栏
//        titleLeftImv = (ImageView) findViewById(R.id.title_imv);
//        titleLeftImv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            }
//        });
//        titleTv = (TextView) findViewById(R.id.title_text_tv);
//        titleTv.setText("首 页");
        // 初始化底部导航栏的控件
        img_first = (ImageView) findViewById(R.id.img_first);
        img_second = (ImageView) findViewById(R.id.img_second);
        img_third = (ImageView) findViewById(R.id.img_third);
        img_fourth = (ImageView) findViewById(R.id.img_fourth);
        tv_first = (TextView) findViewById(R.id.tv_first);
        tv_second = (TextView) findViewById(R.id.tv_second);
        tv_third = (TextView) findViewById(R.id.tv_third);
        tv_fourth = (TextView) findViewById(R.id.tv_fourth);
        ll_first = (LinearLayout) findViewById(R.id.ll_first);
        ll_second = (LinearLayout) findViewById(R.id.ll_second);
        ll_third = (LinearLayout) findViewById(R.id.ll_third);
        ll_fourth = (LinearLayout) findViewById(R.id.ll_fourth);

        ll_first.setOnClickListener(MainActivity.this);
        ll_second.setOnClickListener(MainActivity.this);
        ll_third.setOnClickListener(MainActivity.this);
        ll_fourth.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_first:
                setChioceItem(0);
                break;
            case R.id.ll_second:
                setChioceItem(1);
                break;
            case R.id.ll_third:
                setChioceItem(2);
                break;
            case R.id.ll_fourth:
                setChioceItem(3);
                break;
            default:
                break;
        }
    }

    /**
     * 设置点击选项卡的事件处理
     *
     * @param index 选项卡的标号：0, 1, 2, 3
     */
    private void setChioceItem(int index) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        clearChioce(); // 清空, 重置选项, 隐藏所有Fragment
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
                img_first.setImageResource(R.mipmap.guanlilv); // 需要的话自行修改
                tv_first.setTextColor(getResources().getColor(R.color.tab_text_select));
//                ll_first.setBackgroundColor(gray);
                // 如果fg1为空，则创建一个并添加到界面上
                if (shoppingMallFragment == null) {
                    shoppingMallFragment = new ShoppingMallFragment();
                    fragmentTransaction.add(R.id.ll_container, shoppingMallFragment);
                } else {
                    // 如果不为空，则直接将它显示出来
                    fragmentTransaction.show(shoppingMallFragment);
                }
                break;
            case 1:
                img_second.setImageResource(R.mipmap.order_select);
                tv_second.setTextColor(getResources().getColor(R.color.tab_text_select));
//                ll_second.setBackgroundColor(gray);
                if (batchingFragment == null) {
                    batchingFragment = new BatchingFragment();
                    fragmentTransaction.add(R.id.ll_container, batchingFragment);
                } else {
                    fragmentTransaction.show(batchingFragment);
                }
                break;
            case 2:
                img_third.setImageResource(R.mipmap.zhishilv);
                tv_third.setTextColor(getResources().getColor(R.color.tab_text_select));
//                ll_third.setBackgroundColor(gray);
                if (extensionFragment == null) {
                    extensionFragment = new ExtensionFragment();
                    fragmentTransaction.add(R.id.ll_container, extensionFragment);
                } else {
                    fragmentTransaction.show(extensionFragment);
                }
                break;
            case 3:
                img_fourth.setImageResource(R.mipmap.wodelv);
                tv_fourth.setTextColor(getResources().getColor(R.color.tab_text_select));
//                ll_fourth.setBackgroundColor(gray);
                if (myFragment == null) {
                    myFragment = new MyFragment();
                    fragmentTransaction.add(R.id.ll_container, myFragment);
                } else {
                    fragmentTransaction.show(myFragment);
                }
                break;
        }
        fragmentTransaction.commit(); // 提交
    }

    /**
     * 当选中其中一个选项卡时，其他选项卡重置为默认
     */
    private void clearChioce() {
        img_first.setImageResource(R.mipmap.guanlihui);
        tv_first.setTextColor(getResources().getColor(R.color.tab_text_normal));
//        ll_first.setBackgroundColor(whirt);
        img_second.setImageResource(R.mipmap.order_unselect);
        tv_second.setTextColor(getResources().getColor(R.color.tab_text_normal));
//        ll_second.setBackgroundColor(whirt);
        img_third.setImageResource(R.mipmap.zhishihui);
        tv_third.setTextColor(getResources().getColor(R.color.tab_text_normal));
//        ll_third.setBackgroundColor(whirt);
        img_fourth.setImageResource(R.mipmap.wodehui);
        tv_fourth.setTextColor(getResources().getColor(R.color.tab_text_normal));
//        ll_fourth.setBackgroundColor(whirt);
    }

    /**
     * 隐藏Fragment
     *
     * @param fragmentTransaction
     */
    private void hideFragments(FragmentTransaction fragmentTransaction) {
        if (shoppingMallFragment != null) {
            fragmentTransaction.hide(shoppingMallFragment);
        }
        if (batchingFragment != null) {
            fragmentTransaction.hide(batchingFragment);
        }
        if (extensionFragment != null) {
            fragmentTransaction.hide(extensionFragment);
        }
        if (myFragment != null) {
            fragmentTransaction.hide(myFragment);
        }
    }

    // 解决fragment造成的重影问题， 2019/4/24 by zzh
    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (shoppingMallFragment == null && fragment instanceof ShoppingMallFragment) {
            shoppingMallFragment = (ShoppingMallFragment) fragment;
            getSupportFragmentManager().beginTransaction().hide(shoppingMallFragment).commit();
        } else if (batchingFragment == null && fragment instanceof BatchingFragment) {
            batchingFragment = (BatchingFragment) fragment;
            getSupportFragmentManager().beginTransaction().hide(batchingFragment).commit();
        } else if (extensionFragment == null && fragment instanceof ExtensionFragment) {
            extensionFragment = (ExtensionFragment) fragment;
            getSupportFragmentManager().beginTransaction().hide(extensionFragment).commit();
        } else if (myFragment == null && fragment instanceof MyFragment) {
            myFragment = (MyFragment) fragment;
            getSupportFragmentManager().beginTransaction().hide(myFragment).commit();
        }
    }

}
