package ys.app.feed.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.bean.Coupon;
import ys.app.feed.utils.DateUtils;
import yyydjk.com.library.CouponView;

/**
 * Created by jianghejie on 15/11/26.
 */
public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<Coupon> list_coupon = null;
    public Integer type;
    public Context context;
    private ItemClickCallBack clickCallBack;

    public CouponAdapter(Context context, ArrayList<Coupon> list_coupon, Integer type) {
        this.list_coupon = list_coupon;
        this.type = type;
        this.context = context;
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
        viewHolder.tv_coupon_type.setText(list_coupon.get(position).getName());
        if (TextUtils.equals("018004", list_coupon.get(position).getCouponType())){
            viewHolder.tv_coupon_name.setText("- ¥" + list_coupon.get(position).getReplaceMoney());
        } else {
            viewHolder.tv_coupon_name.setText(list_coupon.get(position).getName());
        }
        //适配颜色
        if (TextUtils.equals("018001", list_coupon.get(position).getCouponType())){ // 半价券
            viewHolder.couponView.setBackgroundColor(context.getResources().getColor(R.color.bg_coupon2));
            viewHolder.tv_coupon_type.setBackgroundResource(R.drawable.bt_rectangle_shape_coupon2);
            viewHolder.tv_immediate_use.setBackgroundResource(R.drawable.bt_rectangle_shape_empty_coupon2);
            viewHolder.tv_immediate_use.setTextColor(context.getResources().getColor(R.color.bg_coupon2));
        } else if (TextUtils.equals("018002", list_coupon.get(position).getCouponType())){ // 砍价券
            viewHolder.couponView.setBackgroundColor(context.getResources().getColor(R.color.bg_coupon1));
            viewHolder.tv_coupon_type.setBackgroundResource(R.drawable.bt_rectangle_shape_coupon1);
            viewHolder.tv_immediate_use.setBackgroundResource(R.drawable.bt_rectangle_shape_empty_coupon1);
            viewHolder.tv_immediate_use.setTextColor(context.getResources().getColor(R.color.bg_coupon1));
        }else if (TextUtils.equals("018003", list_coupon.get(position).getCouponType())){ // 免拼券
            viewHolder.couponView.setBackgroundColor(context.getResources().getColor(R.color.bg_coupon3));
            viewHolder.tv_coupon_type.setBackgroundResource(R.drawable.bt_rectangle_shape_coupon3);
            viewHolder.tv_immediate_use.setBackgroundResource(R.drawable.bt_rectangle_shape_empty_coupon3);
            viewHolder.tv_immediate_use.setTextColor(context.getResources().getColor(R.color.bg_coupon3));
        }else if (TextUtils.equals("018004", list_coupon.get(position).getCouponType())){ // 代金券
            viewHolder.couponView.setBackgroundColor(context.getResources().getColor(R.color.bg_coupon4));
            viewHolder.tv_coupon_type.setBackgroundResource(R.drawable.bt_rectangle_shape_coupon4);
            viewHolder.tv_immediate_use.setBackgroundResource(R.drawable.bt_rectangle_shape_empty_coupon4);
            viewHolder.tv_immediate_use.setTextColor(context.getResources().getColor(R.color.bg_coupon4));
        }


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
        public CouponView couponView;
        public TextView tv_immediate_use;
        public TextView tv_coupon_type;
        public ViewHolder(View view){
            super(view);
            tv_coupon_date = (TextView) view.findViewById(R.id.tv_coupon_date);
            tv_coupon_name = (TextView) view.findViewById(R.id.tv_coupon_name);
            couponView = (CouponView) view.findViewById(R.id.couponView);
            tv_immediate_use = (TextView) view.findViewById(R.id.tv_immediate_use);
            tv_coupon_type = (TextView) view.findViewById(R.id.tv_coupon_type);
        }
    }
}





















