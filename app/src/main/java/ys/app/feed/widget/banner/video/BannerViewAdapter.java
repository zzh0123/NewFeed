package ys.app.feed.widget.banner.video;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayerStandard;
import ys.app.feed.bean.GoodsImage;
import ys.app.feed.utils.LogUtils;

public class BannerViewAdapter extends PagerAdapter {

    private Context context;
    private List<GoodsImage> list_goodsImage;
    public JZVideoPlayerStandard jzVideoPlayerStandard;

    public BannerViewAdapter(Activity context, List<GoodsImage> list_goodsImage) {
//        this.context = context.getApplicationContext();
        this.context = context;
        if (list_goodsImage == null || list_goodsImage.size() == 0) {
            this.list_goodsImage = new ArrayList<>();
        } else {
            this.list_goodsImage = list_goodsImage;
        }
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        if (list_goodsImage.get(position).getType() == 0) {//图片
            final ImageView imageView = new ImageView(context);
            Glide.with(context).load(list_goodsImage.get(position).getImgUrl())
//                    .skipMemoryCache(true)
                    .into(imageView);
            container.addView(imageView);

            return imageView;
        } else {//视频

//            final VideoView videoView = new VideoView(context);
//            videoView.setVideoURI(Uri.parse(listBean.get(position).getBannerUrl()));
//            //开始播放
//            videoView.start();
//            container.addView(videoView);

            jzVideoPlayerStandard  = new JZVideoPlayerStandard(context);
            jzVideoPlayerStandard.setUp(list_goodsImage.get(position).getImgUrl(),
                    JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                    "慧合产品视频");
//            jzVideoPlayerStandard.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");

//            jcVideoPlayer = new JCVideoPlayerStandard(context);
//            jcVideoPlayer.setUp(listBean.get(position).getBannerUrl()
//                    , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "哈哈");
            LogUtils.i("视频", "--视频--");
            Glide.with(context)
                    .load(list_goodsImage.get(1).getImgUrl())
                    .into(jzVideoPlayerStandard.thumbImageView);
//            jcVideoPlayer.prepareMediaPlayer ();
            container.addView(jzVideoPlayerStandard);
            return jzVideoPlayerStandard;
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return list_goodsImage.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View) object;
    }
}