package ys.app.feed.adapter;

import android.content.Context;
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
import ys.app.feed.bean.AllGoodsItem;
import ys.app.feed.bean.NormalOrderStatusItem;

/**
 * Created by jianghejie on 15/11/26.
 */
public class NormalOrderStatusAdapter extends RecyclerView.Adapter<NormalOrderStatusAdapter.ViewHolder>{
    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<NormalOrderStatusItem> list_orderStatusTitle = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public NormalOrderStatusAdapter(Context context, ArrayList<NormalOrderStatusItem> list_orderStatusTitle) {
        this.context = context;
        this.list_orderStatusTitle = list_orderStatusTitle;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_normal_order_status,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        viewHolder.tv_status_name.setText(list_orderStatusTitle.get(position).getStatus_name());
        if (list_orderStatusTitle.get(position).isSelected()){
            viewHolder.tv_status_name.setTextColor(context.getResources().getColor(R.color.text_green));
        } else {
            viewHolder.tv_status_name.setTextColor(context.getResources().getColor(R.color.text_black));
        }
        viewHolder.tv_status_name.setOnClickListener(
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
        return list_orderStatusTitle.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_status_name;

        public ViewHolder(View view){
            super(view);
            tv_status_name = (TextView) view.findViewById(R.id.tv_status_name);
        }
    }

}





















