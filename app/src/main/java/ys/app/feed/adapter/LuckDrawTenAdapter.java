package ys.app.feed.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.bean.GiftItem;
import ys.app.feed.bean.ModuleItem;
import ys.app.feed.widget.CircleImageView;

/**
 * Created by jianghejie on 15/11/26.
 */
public class LuckDrawTenAdapter extends RecyclerView.Adapter<LuckDrawTenAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<GiftItem> list_giftItem_ten = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public LuckDrawTenAdapter(ArrayList<GiftItem> list_giftItem_ten) {
        this.list_giftItem_ten = list_giftItem_ten;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_luck_draw_ten,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
//        viewHolder.iv_module.setImageResource(list_giftItem_ten.get(position).getModuleImage());
        viewHolder.tv_module.setText(list_giftItem_ten.get(position).getConfigName());

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return list_giftItem_ten.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView iv_module;
        public TextView tv_module;
        public ViewHolder(View view){
            super(view);
            iv_module = (CircleImageView) view.findViewById(R.id.iv_module);
            tv_module = (TextView) view.findViewById(R.id.tv_module);
        }
    }
}





















