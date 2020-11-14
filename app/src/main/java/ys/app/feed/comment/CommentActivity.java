package ys.app.feed.comment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donkingliang.labels.LabelsView;
import com.guoxiaoxing.phoenix.core.PhoenixOption;
import com.guoxiaoxing.phoenix.core.model.MediaEntity;
import com.guoxiaoxing.phoenix.core.model.MimeType;
import com.guoxiaoxing.phoenix.picker.Phoenix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.adapter.MediaAdapter;
import ys.app.feed.address.AddAddressActivity;
import ys.app.feed.bean.CommentLevelItem;
import ys.app.feed.bean.GroupPersonNumItem;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.ImageUtils;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;

/**
 * 发表评论
 */
public class CommentActivity extends Activity implements View.OnClickListener, MediaAdapter.OnAddMediaListener {

    private LinearLayout ll_back; // 返回
    private int REQUEST_CODE = 0x000111;
    private MediaAdapter mMediaAdapter;

    private LabelsView labelsView;
    ArrayList<CommentLevelItem> list_levelItem = new ArrayList<CommentLevelItem>();

    private EditText et_comment;
    private TextView tv_submit_comment;

    // 发表商品评论
    private String url_publishComment; // url
    private String commodityId; // 商品id
    private String content; // 内容
    private List<String> arrayList_img = new ArrayList<String>(); // 图片集合
    private String level = "017001"; // 级别  好评：017001， 一般：017002， 差评：017003
    private String userId; // 用户id
    private HashMap<String, Object> paramsMap_publishComment = new HashMap<String, Object>();
    private JSONObject paramsJsonObject_publishComment;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commodityId = getIntent().getStringExtra("commodityId");

        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        labelsView = (LabelsView) findViewById(R.id.labels_level);
        labelsView.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                level = list_levelItem.get(position).getCode();
//                ToastUtils.showShort(CommentActivity.this, "--level--" + level);
            }
        });

        CommentLevelItem commentLevelItem = new CommentLevelItem();
        commentLevelItem.setName("好评");
        commentLevelItem.setCode("017001");
        CommentLevelItem commentLevelItem1 = new CommentLevelItem();
        commentLevelItem1.setName("一般");
        commentLevelItem1.setCode("017002");
        CommentLevelItem commentLevelItem2 = new CommentLevelItem();
        commentLevelItem2.setName("差评");
        commentLevelItem2.setCode("017003");
        list_levelItem.add(commentLevelItem);
        list_levelItem.add(commentLevelItem1);
        list_levelItem.add(commentLevelItem2);

        labelsView.setLabels(list_levelItem, new LabelsView.LabelTextProvider<CommentLevelItem>() {
            @Override
            public CharSequence getLabelText(TextView label, int position, CommentLevelItem data) {
                return data.getName();
            }
        });
        labelsView.setSelects(0);

        tv_submit_comment = (TextView) findViewById(R.id.tv_submit_comment);
        tv_submit_comment.setOnClickListener(this);

        et_comment = (EditText) findViewById(R.id.et_comment);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(CommentActivity.this, 4, GridLayoutManager.VERTICAL, false));
        mMediaAdapter = new MediaAdapter(this);
        recyclerView.setAdapter(mMediaAdapter);
        mMediaAdapter.setOnItemClickListener(new MediaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (mMediaAdapter.getData().size() > 0) {
                    //预览
                    Phoenix.with()
                            .pickedMediaList(mMediaAdapter.getData())
                            .start(CommentActivity.this, PhoenixOption.TYPE_BROWSER_PICTURE, 0);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back: // 返回
                finish();
                break;
            case R.id.tv_submit_comment: // 提交评论
                publishCommen();
                break;
        }
    }

    @Override
    public void onaddMedia() {
        Phoenix.with()
                .theme(PhoenixOption.THEME_DEFAULT)// 主题
                .fileType(MimeType.ofAll())//显示的文件类型图片、视频、图片和视频
                .maxPickNumber(3)// 最大选择数量
                .minPickNumber(0)// 最小选择数量
                .spanCount(4)// 每行显示个数
                .enablePreview(true)// 是否开启预览
                .enableCamera(true)// 是否开启拍照
                .enableAnimation(true)// 选择界面图片点击效果
                .enableCompress(true)// 是否开启压缩
                .compressPictureFilterSize(0)//多少kb以下的图片不压缩
                .compressVideoFilterSize(2018)//多少kb以下的视频不压缩
                .thumbnailHeight(160)// 选择界面图片高度
                .thumbnailWidth(160)// 选择界面图片宽度
                .enableClickSound(false)// 是否开启点击声音
                .pickedMediaList(mMediaAdapter.getData())// 已选图片数据
                .videoFilterTime(30)//显示多少秒以内的视频
                .mediaFilterSize(10000)//显示多少kb以下的图片/视频，默认为0，表示不限制
                .start(CommentActivity.this, PhoenixOption.TYPE_PICK_MEDIA, REQUEST_CODE);
    }

    private void setData() {
        content = et_comment.getText().toString().trim();
        userId = (String) SPUtils.get(CommentActivity.this, "userId", "");

        if (TextUtils.isEmpty(content)){
            ToastUtils.showShort(CommentActivity.this, "评论内容不能为空！");
            return;
        }
        paramsMap_publishComment.put("commodityId", commodityId); // 商品id
        paramsMap_publishComment.put("content", content); // 内容
        paramsMap_publishComment.put("imgs", arrayList_img);
        paramsMap_publishComment.put("level", level); // 级别
        paramsMap_publishComment.put("userId", userId);


        String json = JSON.toJSONString(paramsMap_publishComment);
        paramsJsonObject_publishComment = JSONObject.parseObject(json);
    }

    // 发表商品评论
    private void publishCommen() {
        tv_submit_comment.setClickable(false);
        url_publishComment = Constants.baseUrl + Constants.url_publishComment;
        setData();
        Okhttp3Utils.postAsycRequest_Json(url_publishComment, paramsJsonObject_publishComment, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(CommentActivity.this, "服务器未响应！");
                tv_submit_comment.setClickable(true);
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        ToastUtils.showShort(CommentActivity.this, "评论成功！");
                        tv_submit_comment.setClickable(false);
                        finish();
                    } else {
                        ToastUtils.showShort(CommentActivity.this, resultInfo.getMessage());
                        tv_submit_comment.setClickable(true);
                    }
                } else {
                    ToastUtils.showShort(CommentActivity.this, "服务器返回数据为空！");
                    tv_submit_comment.setClickable(true);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //返回的数据
            List<MediaEntity> result = Phoenix.result(data);
            mMediaAdapter.setData(result);

            if (result != null && result.size() > 0) {
                for (int i = 0; i < result.size(); i++) {
                    LogUtils.i("--result_img--", "--result_img--" + result.get(i).getCompressPath());
                    arrayList_img.add(ImageUtils.imageToBase64(result.get(i).getCompressPath()));
                }
            }
            LogUtils.i("--arrayList_img--", "--arrayList_img--" + arrayList_img.size());
        }
    }

}
