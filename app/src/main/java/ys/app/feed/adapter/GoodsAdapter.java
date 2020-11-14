package ys.app.feed.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.bean.Coupon;
import ys.app.feed.utils.DateUtils;

/**
 * Created by jianghejie on 15/11/26.
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<Coupon> list_coupon = null;
    public Integer type;
    private ItemClickCallBack clickCallBack;

    public GoodsAdapter(ArrayList<Coupon> list_coupon, Integer type) {
        this.list_coupon = list_coupon;
        this.type = type;
    }
    public void setType(Integer type){
        this.type = type;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_coupon,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        String date_start = DateUtils.stampToDate_Day(list_coupon.get(position).getStartDate());
        String date_end = DateUtils.stampToDate_Day(list_coupon.get(position).getEndDate());
        viewHolder.tv_coupon_date.setText(date_start + "-" + date_end);
        viewHolder.tv_coupon_name.setText(list_coupon.get(position).getName());
        viewHolder.tv_coupon_type.setText(list_coupon.get(position).getCouponType());

        if (type == 0){ // 已领取未使用
            viewHolder.tv_immediate_use.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv_immediate_use.setVisibility(View.INVISIBLE);
        }

        viewHolder.tv_immediate_use.setOnClickListener(
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
        return list_coupon.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_coupon_date;
        public TextView tv_coupon_name;
        public TextView tv_immediate_use;
        public TextView tv_coupon_type;
        public ViewHolder(View view){
            super(view);
            tv_coupon_date = (TextView) view.findViewById(R.id.tv_coupon_date);
            tv_coupon_name = (TextView) view.findViewById(R.id.tv_coupon_name);
            tv_immediate_use = (TextView) view.findViewById(R.id.tv_immediate_use);
            tv_coupon_type = (TextView) view.findViewById(R.id.tv_coupon_type);
        }
    }
}





















