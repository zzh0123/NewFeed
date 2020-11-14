package ys.app.feed.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
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
import ys.app.feed.bean.LaunchGroupBuyGoods;

/**
 * Created by jianghejie on 15/11/26.
 */
public class GroupBuyLaunchAdapter extends RecyclerView.Adapter<GroupBuyLaunchAdapter.ViewHolder>{
    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<LaunchGroupBuyGoods> list_launch_group_buy_goods = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public GroupBuyLaunchAdapter(Context context, ArrayList<LaunchGroupBuyGoods> list_launch_group_buy_goods) {
        this.context = context;
        this.list_launch_group_buy_goods = list_launch_group_buy_goods;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_group_goods_launch,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        Glide.with(context).load(list_launch_group_buy_goods.get(position).getListImg())
//                    .skipMemoryCache(true)
                .into(viewHolder.iv_group_goods_img);
        viewHolder.tv_group_goods_name.setText(list_launch_group_buy_goods.get(position).getName());
        viewHolder.tv_price.setText(list_launch_group_buy_goods.get(position).getGroupMemberPrice() + "");
        viewHolder.tv_original_price.setText(list_launch_group_buy_goods.get(position).getGroupPrice() + "");
        viewHolder.tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        viewHolder.ll_group_goods_item.setOnClickListener(
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
        return list_launch_group_buy_goods.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_group_goods_item;
        public ImageView iv_group_goods_img;
        public TextView tv_group_goods_name;
        public TextView tv_price;
        public TextView tv_original_price;
        public TextView tv_go_to_group_buy;

        public ViewHolder(View view){
            super(view);
            ll_group_goods_item = (LinearLayout) view.findViewById(R.id.ll_group_goods_item);
            iv_group_goods_img = (ImageView) view.findViewById(R.id.iv_group_goods_img);
            tv_group_goods_name = (TextView) view.findViewById(R.id.tv_group_goods_name);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
            tv_original_price = (TextView) view.findViewById(R.id.tv_original_price);
            tv_go_to_group_buy = (TextView) view.findViewById(R.id.tv_go_to_group_buy);
        }
    }

}





















