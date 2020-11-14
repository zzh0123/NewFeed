package ys.app.feed.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ys.app.feed.R;
import ys.app.feed.constant.Constants;

public class WRKShareUtil {

    private static IWXAPI wxapi;
    private static WRKShareUtil instance;

    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private static final int THUMB_SIZE = 100; //缩略图大小
    private static String appID;
    private static Context context;

    private WRKShareUtil(Context context, String appID) {
        this.appID = appID;
        this.context = context;
    }

    public static WRKShareUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (WRKShareUtil.class) {
                if (instance == null) {
                    if (appID == null) {
                        //获取清单文件中的APP_ID
                        appID = Constants.AppID;
                    }
                    instance = new WRKShareUtil(context, appID);
                }
            }
        }
        return instance;
    }

    public static void regToWeiXin() {
        wxapi = WXAPIFactory.createWXAPI(context, appID, true);
        wxapi.registerApp(appID);
    }

    /**
     * 判断是否安装微信
     */
    public static boolean isWeiXinAppInstall() {
        if (wxapi == null)
            wxapi = WXAPIFactory.createWXAPI(context, appID);
        if (wxapi.isWXAppInstalled()) {
            return true;
        } else {
            Toast.makeText(context, "没安装微信", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 是否支持分享到朋友圈
     */
    public boolean isWXAppSupportAPI() {
        if (isWeiXinAppInstall()) {
            int wxSdkVersion = wxapi.getWXAppSupportAPI();
            if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static IWXAPI getWxApi() {
        if (wxapi == null)
            wxapi = WXAPIFactory.createWXAPI(context, appID);
        return wxapi;
    }


    /**
     * 微信登录
     */
    public static void wxLogin() {
        if (!isWeiXinAppInstall()){
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        //授权域 获取用户个人信息则填写snsapi_userinfo
        req.scope = "snsapi_userinfo";
        //用于保持请求和回调的状态 可以任意填写
        req.state = "test_wx_login";
        getWxApi().sendReq(req);
    }

    /**
     * 分享文本类型
     *
     * @param text 文本内容
     * @param type 微信会话或者朋友圈等
     */
//    public void shareTextToWx(String text, int type) {
//        if (text == null || text.length() == 0) {
//            return;
//        }
//
//        WXTextObject textObj = new WXTextObject();
//        textObj.text = text;
//
//        WXMediaMessage msg = new WXMediaMessage();
//        msg.mediaObject = textObj;
//        msg.description = text;
//
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("text");
//        req.message = msg;
//        req.scene = type;
//
//        getWxApi().sendReq(req);
//    }

    /**
     * 分享图片到微信
     */

//    public void shareImageToWx(final String imgUrl, String title, String desc, final int wxSceneSession) {
//        Bitmap bmp = BitmapFactory.decodeResource(WRKApplication.getContext().getResources(), R.mipmap.ic_launcher);
//        WXImageObject imgObj = new WXImageObject(bmp);
//
//        final WXMediaMessage msg = new WXMediaMessage();
//        msg.mediaObject = imgObj;
//        msg.title = title;
//        msg.description = desc;
//        final Bitmap[] thumbBmp = {Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true)};
//        bmp.recycle();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    InputStream imageStream = getImageStream(imgUrl);
//                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
//                    thumbBmp[0] = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
//                    bitmap.recycle();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                msg.thumbData = WRKFileUtil.bmpToByteArray(thumbBmp[0], true);
//
//                SendMessageToWX.Req req = new SendMessageToWX.Req();
//                req.transaction = buildTransaction("img");
//                req.message = msg;
//                req.scene = wxSceneSession;
//                getWxApi().sendReq(req);
//            }
//        }).start();
//    }

    /**
     * 分享音乐
     *
     * @param musicUrl       音乐资源地址
     * @param title          标题
     * @param desc           描述
     * @param wxSceneSession
     */
//    public void shareMusicToWx(final String musicUrl, final String title, final String desc, final String iconUrl, final int wxSceneSession) {
//        WXMusicObject music = new WXMusicObject();
//        music.musicUrl = musicUrl;
//
//        final WXMediaMessage msg = new WXMediaMessage();
//        msg.mediaObject = music;
//        msg.title = title;
//        msg.description = desc;
//
//        Bitmap bmp = BitmapFactory.decodeResource(WRKApplication.getContext().getResources(), R.mipmap.ic_launcher);
//        final Bitmap[] thumbBmp = {Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true)};
//        bmp.recycle();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    InputStream imageStream = getImageStream(iconUrl);
//                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
//                    thumbBmp[0] = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
//                    bitmap.recycle();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                msg.thumbData = WRKFileUtil.bmpToByteArray(thumbBmp[0], true);
//                SendMessageToWX.Req req = new SendMessageToWX.Req();
//                req.transaction = buildTransaction("music");
//                req.message = msg;
//                req.scene = wxSceneSession;
//                getWxApi().sendReq(req);
//            }
//        }).start();
//    }

    /**
     * 分享视频
     *
     * @param videoUrl       视频地址
     * @param title          标题
     * @param desc           描述
     * @param wxSceneSession
     */

//    public void shareVideoToWx(String videoUrl, String title, String desc, final String iconUrl, final int wxSceneSession) {
//        WXVideoObject video = new WXVideoObject();
//        video.videoUrl = videoUrl;
//
//        final WXMediaMessage msg = new WXMediaMessage(video);
//        msg.title = title;
//        msg.description = desc;
//        Bitmap bmp = BitmapFactory.decodeResource(WRKApplication.getContext().getResources(), R.mipmap.ic_launcher);
//        final Bitmap[] thumbBmp = {Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true)};
//        bmp.recycle();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    InputStream imageStream = getImageStream(iconUrl);
//                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
//                    thumbBmp[0] = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
//                    bitmap.recycle();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                msg.thumbData = WRKFileUtil.bmpToByteArray(thumbBmp[0], true);
//
//                SendMessageToWX.Req req = new SendMessageToWX.Req();
//                req.transaction = buildTransaction("video");
//                req.message = msg;
//                req.scene = wxSceneSession;
//                getWxApi().sendReq(req);
//            }
//        }).start();
//    }

    /**
     * 分享url地址
     *
     * @param url            地址
     * @param title          标题
     * @param desc           描述
     * @param wxSceneSession 类型
     */
    public void shareUrlToWx(String url, String title, String desc, final String iconUrl, final int wxSceneSession) {
        if (!isWeiXinAppInstall()){
            return;
        }

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;

        msg.description = desc;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        final Bitmap[] thumbBmp = {Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true)};
        bmp.recycle();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream imageStream = getImageStream(iconUrl);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    thumbBmp[0] = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
                    bitmap.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                msg.thumbData = bmpToByteArray(thumbBmp[0], true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = wxSceneSession;
                getWxApi().sendReq(req);
            }
        }).start();
    }

    /**
     * 分享数据
     *
     //     * @param mShareData     分享数据
     //     * @param context        上下文
     //     * @param wxSceneSession 分享位置类型
     */
//    public void shareToWx(ShareData mShareData, Context context, int wxSceneSession) {
//        if (mShareData != null) {
//            String title = mShareData.getTitle();
//            if (title != null && title.length() > 512)
//                title = title.substring(0, 511);
//            String desc = mShareData.getDesc();
//            if (desc != null && desc.length() > 1024)
//                desc = desc.substring(0, 1023);
//            String link = mShareData.getLink();
//            String dataUrl = mShareData.getData_url();
//            String imgUrl = mShareData.getImg_url();
//            String type = mShareData.getType();
//            if (TextUtils.isEmpty(type)) {
//                instance.shareUrlToWx(link, title, desc, imgUrl, wxSceneSession);
//                return;
//            }
//            switch (type) {
//                case "music":
//                    instance.shareMusicToWx(dataUrl, title, desc, imgUrl, wxSceneSession);
//                    break;
//                case "video":
//                    instance.shareVideoToWx(dataUrl, title, desc, imgUrl, wxSceneSession);
//                    break;
//                case "img":
//                    instance.shareImageToWx(imgUrl, title, desc, wxSceneSession);
//                    break;
//                case "link":
//                    instance.shareUrlToWx(link, title, desc, imgUrl, wxSceneSession);
//                    break;
//                default:
//                    instance.shareUrlToWx(link, title, desc, imgUrl, wxSceneSession);
//                    break;
//            }
//        } else {
//            Toast.makeText(context, "分享的数据为空，无法分享", Toast.LENGTH_SHORT).show();
//        }
//    }

    public InputStream getImageStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
