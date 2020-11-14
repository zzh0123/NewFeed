package ys.app.feed.goods;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shizhefei.fragment.LazyFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.adapter.CommentAdapter;
import ys.app.feed.adapter.ReceivingAddressAdapter;
import ys.app.feed.address.ReceivingAddressActivity;
import ys.app.feed.bean.Comment;
import ys.app.feed.bean.ReceivingAddress;
import ys.app.feed.bean.ResultInfo;
import ys.app.feed.constant.Constants;
import ys.app.feed.utils.LogUtils;
import ys.app.feed.utils.Okhttp3Utils;
import ys.app.feed.utils.ResultCallback;
import ys.app.feed.utils.ToastUtils;

/**
 *  商品评论
 */

public class CommentFragment extends LazyFragment {

    // 获取商品评论列表
    private String url_getCommentData; // url
    private String commodityId; // 商品id
    private Integer pageNum = 1; // 页
    private Integer pageSize = 10; // 行
    private String type = "017001"; // 级别  好评：017001， 一般：017002， 差评：017003
    private HashMap<String, String> paramsMap_getCommentData = new HashMap<String, String>();
    private ArrayList<Comment> list_comment = new ArrayList<Comment>();
    private XRecyclerView mRecyclerView;
    private CommentAdapter adapter_comment;


    private Handler handler;

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(android.os.Message msg) {
//                initView();
            }
        };

        setContentView(R.layout.fragment_comment);

        initView();
        Bundle bundle = getArguments();
        commodityId = bundle.getString("commodityId");
        LogUtils.i("-comment-commodityId--", "-comment-commodityId--" + commodityId);
        getCommentData();
    }

    private void initView() {
        // 评论列表
        mRecyclerView = (XRecyclerView) this.findViewById(R.id.recyclerview_comment);
        mRecyclerView.setPullRefreshEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.iconfont_downgrey);

        mRecyclerView
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
//        header = LayoutInflater.from(getActivity()).inflate(R.bottom_group_buy_person_num.fragment_goods_header, (ViewGroup) findViewById(android.R.id.content), false);
//        mRecyclerView.addHeaderView(header);
        mRecyclerView.getDefaultFootView().setLoadingHint("加载中...");
        mRecyclerView.getDefaultFootView().setNoMoreHint("没有更多数据了");
        mRecyclerView.getDefaultFootView().setMinimumHeight(100);

        adapter_comment = new CommentAdapter(getActivity(), list_comment);

        mRecyclerView.setAdapter(adapter_comment);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
//                pageNum++;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mRecyclerView.refreshComplete();
                    }

                }, 0);            //refresh data here
            }

            @Override
            public void onLoadMore() {
                Log.e("aaaaa", "call onLoadMore");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        pageNum++;
                        getCommentData();
                    }
                }, 1000);
            }
        });
    }

    // 获取商品评论列表
    private void getCommentData(){
        paramsMap_getCommentData.put("commodityId", commodityId);
        paramsMap_getCommentData.put("pageNum", pageNum + "");
        paramsMap_getCommentData.put("pageSize", pageSize + "");
        paramsMap_getCommentData.put("type", type);

        url_getCommentData = Constants.baseUrl + Constants.url_getCommentData;
        Okhttp3Utils.getAsycRequest(url_getCommentData, paramsMap_getCommentData, new ResultCallback() {
            @Override
            public void onFailure(Exception e) {
                ToastUtils.showShort(getActivity(), "服务器未响应！");
                mRecyclerView.refreshComplete();
                handler.sendEmptyMessageDelayed(1, 1000);
            }

            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    String str_response = response.toString();
                    ResultInfo resultInfo = JSON.parseObject(str_response, ResultInfo.class);
                    if (resultInfo.getCode() == 800) {
                        JSONObject response_jsonObject = JSONObject.parseObject(str_response);
                        JSONArray data_jsonArray = (JSONArray)response_jsonObject.getJSONArray("data");
                        String data_json_str =JSONObject.toJSONString(data_jsonArray);//将array数组转换成字符串
                        List<Comment> list_data = JSONObject.parseArray(data_json_str, Comment.class);//把字符串转换成集合
                        if (list_data != null && list_data.size() > 0){
                            list_comment.addAll(list_data);
                            for (int i = 0; i < list_comment.size(); i++) {
                                JSONObject comment_jsonObject = (JSONObject) data_jsonArray.get(i);
                                JSONArray imgs_jsonArray = (JSONArray) comment_jsonObject.getJSONArray("imgs");
                                ArrayList<String> list_imgesUrl = new ArrayList<>();
                                list_comment.get(i).setImgs(list_imgesUrl);

                                if (imgs_jsonArray != null && imgs_jsonArray.size() > 0) {
                                    for (int j = 0; j < imgs_jsonArray.size(); j++) {
                                        list_comment.get(i).getImgs().add(imgs_jsonArray.get(j).toString().trim());
                                    }
                                }
                            }
                            adapter_comment.notifyDataSetChanged();
                        }
                        mRecyclerView.refreshComplete();
                        handler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        ToastUtils.showShort(getActivity(), resultInfo.getMessage());
                        mRecyclerView.refreshComplete();
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "服务器返回数据为空！");
                    mRecyclerView.refreshComplete();
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        });
    }

    @Override
    public void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        handler.removeCallbacksAndMessages(null);
    }

}
