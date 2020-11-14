package ys.app.feed.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.bean.BuyScoreItem;
import ys.app.feed.bean.Order;

/**
 * Created by jianghejie on 15/11/26.
 */
public class BuyScoreAdapter extends RecyclerView.Adapter<BuyScoreAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<BuyScoreItem> list_buyScoreItem = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public BuyScoreAdapter(Context context, ArrayList<BuyScoreItem> list_buyScoreItem) {
        this.context = context;
        this.list_buyScoreItem = list_buyScoreItem;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_buy_score,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        String coupon_str = list_buyScoreItem.get(position).getCouponStr();
        Double score = list_buyScoreItem.get(position).getScore();
        String title = "充值" + list_buyScoreItem.get(position).getMoney() + "元得" + score + "积分再送" + coupon_str;
        viewHolder.tv_title.setText(title);
        viewHolder.tv_get_coupon.setText("领取" + coupon_str + "一张");

        viewHolder.tv_buy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(clickCallBack != null){
                            clickCallBack.onItemClick(position);
                        }
                    }
                }
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return list_buyScoreItem.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title;
        public TextView tv_get_coupon;
        public TextView tv_buy;

        public ViewHolder(View view){
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_get_coupon = (TextView) view.findViewById(R.id.tv_get_coupon);
            tv_buy = (TextView) view.findViewById(R.id.tv_buy);
        }
    }
}





















