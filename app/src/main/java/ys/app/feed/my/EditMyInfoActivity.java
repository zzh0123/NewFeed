package ys.app.feed.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.guoxiaoxing.phoenix.core.PhoenixOption;
import com.guoxiaoxing.phoenix.core.model.MediaEntity;
import com.guoxiaoxing.phoenix.core.model.MimeType;
import com.guoxiaoxing.phoenix.picker.Phoenix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.MediaAdapter;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.login.LoginActivity;
import ys.app.feed.login.SetPasswordActivity;
import ys.app.feed.utils.ImageUtils;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.CircleImageView;

/**
 * 编辑资料
 */
public class EditMyInfoActivity extends Activity implements View.OnClickListener {

    private LinearLayout ll_back; // 返回

    private LinearLayout ll_head; // 头像
    private CircleImageView iv_head;

    private LinearLayout ll_user_name; // 用户名
    private TextView tv_user_name; // 用户名

    private LinearLayout ll_phone_num; // 手机号
    private TextView tv_phone_num; // 手机号

    private LinearLayout ll_area; // 地区
    private TextView tv_area; // 地区

    private TextView tv_sign_out; // 保 存

    private int REQUEST_CODE = 0x000111;
    private String imagePath = "";

    // 修改用户信息
    private String url_changeUserInfo;  // url
    private String area = "";  // 区县
    private String city = "";  // 城市
    private String headPortrait_new = "";  // 头像
    private String name_new = "";  // 用户名
    private String province = "";  // 省份
    private String userId;  // userId

    private String name = "";  // 用户名
    private String headPortrait = "";  // 头像

    private HashMap<String, String> paramsMap_changeUserInfo = new HashMap<String, String>();
    private JSONObject paramsJsonObject_changeUserInfo;

    // 获取用户详细信息
    private String url_getUserInfoDetail; // url
    private HashMap<String, String> paramsMap_getUserInfoDetail = new HashMap<String, String>();

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_info);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        intent = getIntent();
        // 用户id
        userId = (String) SPUtils.get(EditMyInfoActivity.this, "userId", "");
        initView();
        getUserInfoDetail();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);

        ll_head = (LinearLayout) findViewById(R.id.ll_head);
        iv_head = (CircleImageView) findViewById(R.id.iv_head);

        ll_user_name = (LinearLayout) findViewById(R.id.ll_user_name);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);

        ll_phone_num = (LinearLayout) findViewById(R.id.ll_phone_num);
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);

        ll_area = (LinearLayout) findViewById(R.id.ll_area);
        tv_area = (TextView) findViewById(R.id.tv_area);

        tv_sign_out = (TextView) findViewById(R.id.tv_sign_out);

        ll_back.setOnClickListener(this);
        ll_head.setOnClickListener(this);
        ll_user_name.setOnClickListener(this);
        ll_area.setOnClickListener(this);
        tv_sign_out.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_head: // 头像
                pickImage();
                break;
            case R.id.ll_user_name: // 用户名
                Intent intent0 = new Intent(EditMyInfoActivity.this, EditUserNameActivity.class);
                intent0.putExtra("name", tv_user_name.getText().toString().trim());
                startActivityForResult(intent0, 1);
                break;
            case R.id.ll_area: // 地区
                Intent intent1 = new Intent(EditMyInfoActivity.this, ReceivingAddressActivity.class);
//                intent1.putExtra("name", tv_user_name.getText().toString().trim());
                startActivity(intent1);
                break;
            case R.id.tv_sign_out: // 退出当前账号
                SPUtils.remove(this, "userId");
                MyApplication.getInstance().exit();
                //                Intent intent2 = new Intent(EditMyInfoActivity.this, LoginActivity.class);
//                startActivity(intent2);
//                finish();
                break;
            case R.id.ll_back: // 返回
                setResult(1, intent);
                finish();
                break;
        }
    }

    private void setData() {
        paramsMap_changeUserInfo.put("area", area); // 区县
        paramsMap_changeUserInfo.put("city", city); // 城市
        paramsMap_changeUserInfo.put("headPortrait", headPortrait_new); // 头像
        paramsMap_changeUserInfo.put("name", name_new); // 用户名
        paramsMap_changeUserInfo.put("province", province); // 省份
        paramsMap_changeUserInfo.put("userId", userId);
        String json = JSON.toJSONString(paramsMap_changeUserInfo);
        paramsJsonObject_changeUserInfo = JSONObject.parseObject(json);
    }

    // 编辑用户信息
    private void changeUserInfo() {
        url_changeUserInfo = Constants.baseUrl + Constants.url_changeUserInfo;
        setData();
        Okhttp3Utils.postAsycRequest_Json(url_changeUserInfo, paramsJsonObject_changeUserInfo, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(EditMyInfoActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_getCode = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_getCode, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(EditMyInfoActivity.this, "设置成功！");
//                        Glide.with(EditMyInfoActivity.this).load(headPortrait).error(R.mipmap.load_fail).into(iv_head);
                        getUserInfoDetail();
                    } else {
                        ToastUtils.showShort(EditMyInfoActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(EditMyInfoActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    // 获取用户详细信息
    private void getUserInfoDetail() {
        paramsMap_getUserInfoDetail.put("userId", userId);
        url_getUserInfoDetail = Constants.baseUrl + Constants.url_getUserInfoDetail;
        Okhttp3Utils.getAsycRequest(url_getUserInfoDetail, paramsMap_getUserInfoDetail, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(EditMyInfoActivity.this, "服务器未响应！");
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONObject data_jsonObject = response_jsonObject.getJSONObject("data");

                        String name = data_jsonObject.getString("name");
                        String headPortrait = data_jsonObject.getString("headPortrait");
                        String phone = data_jsonObject.getString("phone");


                        Glide.with(EditMyInfoActivity.this).load(headPortrait).error(R.mipmap.load_fail).into(iv_head);
                        tv_user_name.setText(name);
                        tv_phone_num.setText(phone);
                    } else {
                        ToastUtils.showShort(EditMyInfoActivity.this, resultInfo.getMessage());
                    }
                } else {
                    ToastUtils.showShort(EditMyInfoActivity.this, "服务器返回数据为空！");
                }
            }
        });
    }

    private void pickImage() {
        Phoenix.with()
                .theme(PhoenixOption.THEME_DEFAULT)// 主题
                .fileType(MimeType.ofAll())//显示的文件类型图片、视频、图片和视频
                .maxPickNumber(1)// 最大选择数量
                .minPickNumber(0)// 最小选择数量
                .spanCount(4)// 每行显示个数
                .enablePreview(false)// 是否开启预览
                .enableCamera(true)// 是否开启拍照
                .enableAnimation(true)// 选择界面图片点击效果
                .enableCompress(true)// 是否开启压缩
                .compressPictureFilterSize(0)//多少kb以下的图片不压缩
                .compressVideoFilterSize(2018)//多少kb以下的视频不压缩
                .thumbnailHeight(160)// 选择界面图片高度
                .thumbnailWidth(160)// 选择界面图片宽度
                .enableClickSound(false)// 是否开启点击声音
//                .pickedMediaList(mMediaAdapter.getData())// 已选图片数据
                .videoFilterTime(0)//显示多少秒以内的视频
                .mediaFilterSize(10000)//显示多少kb以下的图片/视频，默认为0，表示不限制
                //如果是在Activity里使用就传Activity，如果是在Fragment里使用就传Fragment
                .start(EditMyInfoActivity.this, PhoenixOption.TYPE_PICK_MEDIA, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                //返回的数据
                List<MediaEntity> result = Phoenix.result(data);
                if (result != null && result.size() > 0) {
                    imagePath = result.get(0).getCompressPath();
                    LogUtils.i("--imagePath--", "--imagePath--" + imagePath);
                    headPortrait_new = ImageUtils.imageToBase64(imagePath);
                }
            } else if (requestCode == 1 && resultCode == 1) {//此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
                name_new = data.getStringExtra("name_new");
                LogUtils.i("--name_new--", "--name_new--" + name_new);
            }

            changeUserInfo();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(1, intent);
        finish();
    }
}
