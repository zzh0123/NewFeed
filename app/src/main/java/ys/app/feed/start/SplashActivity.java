package ys.app.feed.start;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sfyc.ctpv.CountTimeProgressView;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import ys.app.feed.MainActivity;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.constant.Constants;
import ys.app.feed.goods.AllGoodsActivity;
import ys.app.feed.login.LoginActivity;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

public class SplashActivity extends Activity {

    private ImageView  iv_splash; // 开机页
    private String  imgUrl; // url

    private CountTimeProgressView countTimeProgressView;
    private String isFirst; // 是否是第一次安装
    private String userId; // 用户名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        isFirst = (String) SPUtils.get(SplashActivity.this, "isFirst", "");
        // 用户id
        userId = (String) SPUtils.get(SplashActivity.this, "userId", "");
        permissiongen();
        initView();
    }

    private void initView() {
        iv_splash = (ImageView) findViewById(R.id.iv_splash);
        imgUrl = Constants.start_img_url;
        Glide.with(SplashActivity.this)
                .load(imgUrl)
                .error(R.mipmap.load_fail)
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv_splash);

        countTimeProgressView = (CountTimeProgressView) findViewById(R.id.countTimeProgressView);
        countTimeProgressView.addOnEndListener(new CountTimeProgressView.OnEndListener() {
            @Override
            public void onAnimationEnd() {
                Log.e("Main", "onAnimationEnd");
                if (TextUtils.equals("0", isFirst)){ // 1是0否
                    if (!TextUtils.isEmpty(userId)){
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, StartActivity.class));
                    finish();
                }

            }
            @Override
            public void onClick(long overageTime) {
                countTimeProgressView.cancelCountTimeAnimation();
                if (TextUtils.equals("0", isFirst)){ // 1是0否
                    if (!TextUtils.isEmpty(userId)){
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, StartActivity.class));
                    finish();
                }
            }

        });

        countTimeProgressView.startCountTimeAnimation();
    }

    private void permissiongen() {
        //处理需要动态申请的权限
        PermissionGen.with(SplashActivity.this)
                .addRequestCode(200)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();
    }

    //申请权限结果的返回
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    //权限申请成功
    @PermissionSuccess(requestCode = 200)
    public void doSomething() {
        //在这个方法中做一些权限申请成功的事情
//        Toast.makeText(getApplication(), "成功", Toast.LENGTH_SHORT).show();
//        ToastUtils.showShort(SplashActivity.this, "成功！");
    }

    //申请失败
    @PermissionFail(requestCode = 200)
    public void doFailSomething() {
//        Toast.makeText(getApplication(), "失败", Toast.LENGTH_SHORT).show();
//        ToastUtils.showShort(SplashActivity.this, "失败！");
    }

}
