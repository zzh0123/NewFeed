package ys.app.feed.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.bean.MyCollectionItem;
import ys.app.feed.bean.MyInviteItem;

/**
 * Created by jianghejie on 15/11/26.
 */
public class MyCollectAdapter extends RecyclerView.Adapter<MyCollectAdapter.ViewHolder>{
    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<MyCollectionItem> list_myCollectionItem = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public MyCollectAdapter(Context context, ArrayList<MyCollectionItem> list_myCollectionItem) {
        this.context = context;
        this.list_myCollectionItem = list_myCollectionItem;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_my_collect,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        Glide.with(context).load(list_myCollectionItem.get(position).getImgUrl())
//                    .skipMemoryCache(true)
                .into(viewHolder.iv_goods_img);
        viewHolder.tv_goods_name.setText(list_myCollectionItem.get(position).getName());
        viewHolder.tv_cancle_collect.setOnClickListener(
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
        return list_myCollectionItem.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_goods_img; // 商品图片
        public TextView tv_goods_name; // 商品名称
        public TextView tv_cancle_collect; // 取消收藏按钮

        public ViewHolder(View view){
            super(view);
            iv_goods_img = (ImageView) view.findViewById(R.id.iv_goods_img);
            tv_goods_name = (TextView) view.findViewById(R.id.tv_goods_name);
            tv_cancle_collect = (TextView) view.findViewById(R.id.tv_cancle_collect);
        }
    }

}





















