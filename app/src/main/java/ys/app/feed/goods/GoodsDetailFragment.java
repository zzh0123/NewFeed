package ys.app.feed.goods;


import android.graphics.PixelFormat;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shizhefei.fragment.LazyFragment;

import ys.app.feed.R;
import ys.app.feed.widget.x5.X5WebView;

/**
 * A simple {@link Fragment} subclass.
 */

public class GoodsDetailFragment extends LazyFragment {

    private X5WebView iv_goods_detail;
    private String imgUrl;

    private Handler handler;

    public GoodsDetailFragment() {
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

        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT); // 加载网页视频避免闪屏和透明
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getActivity().getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }

        setContentView(R.layout.fragment_goods_detail);

//        GoodsDetailActivity activity = (GoodsDetailActivity) getActivity();
//        imgUrl = activity.getDetailImg();

        Bundle bundle = getArguments();
        imgUrl = bundle.getString("detailImg");
        Log.i("--frg--listImg", "--frg--listImg--" + imgUrl);

        handler.sendEmptyMessageDelayed(1, 1000);
    }

    private void initView() {
        // 商品详情图片
        iv_goods_detail = (X5WebView) findViewById(R.id.iv_goods_detail);
        iv_goods_detail.loadUrl(imgUrl);
//        Glide.with(getActivity())
//                .load(imgUrl)
////                .placeholder(R.mipmap.load_default)
//                .error(R.mipmap.load_fail)
//                .into(iv_goods_detail);
    }

    // 设置img的url
//    public void setImageUrl(String imgUrl) {
//        this.imgUrl = imgUrl;
//    }

    @Override
    public void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        handler.removeCallbacksAndMessages(null);
    }

}
