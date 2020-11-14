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
import ys.app.feed.bean.HalfPriceGoodsItem;
import ys.app.feed.bean.ScoreExchangeGoodsItem;

/**
 * Created by jianghejie on 15/11/26.
 */
public class ScoreExchangeAdapter extends RecyclerView.Adapter<ScoreExchangeAdapter.ViewHolder>{
    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<ScoreExchangeGoodsItem> list_scoreExchangeGoodsItem = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public ScoreExchangeAdapter(Context context, ArrayList<ScoreExchangeGoodsItem> list_scoreExchangeGoodsItem) {
        this.context = context;
        this.list_scoreExchangeGoodsItem = list_scoreExchangeGoodsItem;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_goods_score_exchange,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        Glide.with(context).load(list_scoreExchangeGoodsItem.get(position).getListImg())
//                    .skipMemoryCache(true)
                .into(viewHolder.iv_group_goods_img);
        viewHolder.tv_group_goods_name.setText(list_scoreExchangeGoodsItem.get(position).getName());
        viewHolder.tv_group_goods_type.setText(list_scoreExchangeGoodsItem.get(position).getSpec());
        viewHolder.tv_price.setText(list_scoreExchangeGoodsItem.get(position).getPrice() + "");
        viewHolder.tv_original_price.setText(list_scoreExchangeGoodsItem.get(position).getMemberPrice() + "");
        viewHolder.tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        viewHolder.ll_score_exchange_goods_item.setOnClickListener(
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
        return list_scoreExchangeGoodsItem.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_score_exchange_goods_item;
        public ImageView iv_group_goods_img;
        public TextView tv_group_goods_name;
        public TextView tv_group_goods_type;
        public TextView tv_price;
        public TextView tv_original_price;
        public TextView tv_go_to_exchange;

        public ViewHolder(View view){
            super(view);
            ll_score_exchange_goods_item = (LinearLayout) view.findViewById(R.id.ll_score_exchange_goods_item);
            iv_group_goods_img = (ImageView) view.findViewById(R.id.iv_group_goods_img);
            tv_group_goods_name = (TextView) view.findViewById(R.id.tv_group_goods_name);
            tv_group_goods_type = (TextView) view.findViewById(R.id.tv_group_goods_type);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
            tv_original_price = (TextView) view.findViewById(R.id.tv_original_price);
            tv_go_to_exchange = (TextView) view.findViewById(R.id.tv_go_to_exchange);
        }
    }

}





















