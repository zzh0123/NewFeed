package ys.app.feed.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.batching.BatchingActivity;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.invite.InviteActivity;
import ys.app.feed.login.BindPhoneNumActivity;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.ToastUtils;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private int WX_LOGIN = 1;
    //获取个人信息
    private String url_getUserInfo;
    private IWXAPI wxapi;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        wxapi.handleIntent(intent, this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        wxapi = WXAPIFactory.createWXAPI(this, Constants.AppID);
        wxapi.handleIntent(getIntent(), this);
    }

    /**
     * 微信发送请求到第三方应用时，会回调到该方法
     */
    @Override
    public void onReq(BaseReq baseReq) {

    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     * app发送消息给微信，处理返回消息的回调
     */
    @Override
    public void onResp(BaseResp baseResp) {
        Log.i("WXEntryActivity0" , ">>>errCode = " + baseResp.errCode);
        //微信登录为getType为1，分享为0
        if (baseResp.getType() == WX_LOGIN){
            //登录回调
            SendAuth.Resp resp = (SendAuth.Resp) baseResp;
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    String code = String.valueOf(resp.code);
                    //获取用户信息
                    getAccessToken(code);
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消
                    break;
                default:
                    break;
            }
        }else{ // 微信分享
            switch (baseResp.errCode) {
                // 正确返回
                case BaseResp.ErrCode.ERR_OK:
                    switch (baseResp.getType()) {
                        // ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX是微信分享，api自带
                        case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                            // 只是做了简单的finish操作
//                            ToastUtils.showShort(WXEntryActivity.this, "分享成功！");
                            finish();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    // 错误返回
                    switch (baseResp.getType()) {
                        // 微信分享
                        case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                            Log.i("WXEntryActivity" , ">>>errCode = " + baseResp.errCode);
//                        ToastUtils.showShort(WXEntryActivity.this, "分享失败！");
                            finish();
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }

    }

    private void getAccessToken(String code) {
        HashMap<String, String> paramsMap_url_getAccessToke= new HashMap<String, String>();
        //获取授权
        String url_getAccessToke= "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + Constants.AppID + "&secret=" + Constants.AppSecret + "&code=" + code + "&grant_type=authorization_code";

        Okhttp3Utils.getAsycRequest(url_getAccessToke, paramsMap_url_getAccessToke, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(WXEntryActivity.this, "微信登录失败，服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    String access = null;
                    String openId = null;
                    try {
                        JSONObject jsonObject = new JSONObject(str_response);
                        access = jsonObject.getString("access_token");
                        openId = jsonObject.getString("openid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //获取个人信息
                    url_getUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access + "&openid=" + openId + "";
                    getUserInfo();
                } else {
                    ToastUtils.showShort(WXEntryActivity.this, "微信登录失败，服务器返回数据为空！");
                }
            }
        });

    }

    // 获微信用户信息
    private void getUserInfo(){
        HashMap<String, String> paramsMap_getUserInfo = new HashMap<String, String>();
        Okhttp3Utils.getAsycRequest(url_getUserInfo, paramsMap_getUserInfo, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(WXEntryActivity.this, "微信登录失败，服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    String openId = null;
                    String nickname = null;
                    String headimgurl = null;
                    try {
                        JSONObject jsonObject = new JSONObject(str_response);
                        openId = jsonObject.getString("openid");
                        nickname = jsonObject.getString("nickname");
                        headimgurl = jsonObject.getString("headimgurl");
                        Intent intent = new Intent(WXEntryActivity.this, BindPhoneNumActivity.class);
                        intent.putExtra("headPortrait", headimgurl);
                        intent.putExtra("name", nickname);
                        intent.putExtra("openId", openId);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(WXEntryActivity.this, "微信登录失败，服务器返回数据为空！");
                }
            }
        });
    }
}