package ys.app.feed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.bean.Comment;
import ys.app.feed.bean.Order;
import ys.app.feed.widget.CircleImageView;

/**
 * Created by jianghejie on 15/11/26.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<Order> list_order = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public OrderAdapter(Context context, ArrayList<Order> list_order) {
        this.context = context;
        this.list_order = list_order;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_order_to_be_paid,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        Glide.with(context).load(list_order.get(position).getListImg())
//                    .skipMemoryCache(true)
                .into(viewHolder.iv_img);
        viewHolder.tv_detail.setText(list_order.get(position).getName());
        viewHolder.tv_price.setText(list_order.get(position).getTotalAmount() + "");
        viewHolder.ll_normal_order.setOnClickListener(
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
        return list_order.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_normal_order;
        public ImageView iv_img;
        public TextView tv_detail;
        public TextView tv_price;

        public ViewHolder(View view){
            super(view);
            ll_normal_order = (LinearLayout) view.findViewById(R.id.ll_normal_order);
            iv_img = (ImageView) view.findViewById(R.id.iv_img);
            tv_detail = (TextView) view.findViewById(R.id.tv_detail);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
        }
    }
}





















