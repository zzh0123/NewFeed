package ys.app.feed.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.bean.IntentionModuleItem;
import ys.app.feed.bean.ModuleItem;
import ys.app.feed.utils.LogUtils;

/**
 * Created by jianghejie on 15/11/26.
 */
public class IntentionModuleAdapter extends RecyclerView.Adapter<IntentionModuleAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<IntentionModuleItem> list_intention_module = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public IntentionModuleAdapter(Context context, ArrayList<IntentionModuleItem> list_intention_module) {
        this.context = context;
        this.list_intention_module = list_intention_module;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_module_intention,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        viewHolder.iv_module.setImageResource(list_intention_module.get(position).getModuleImage());
        viewHolder.tv_module.setText(list_intention_module.get(position).getModuleName());

        String intention = list_intention_module.get(position).getIntention();
        if (list_intention_module.get(position).isSelected()){
            switch (intention){
                case "011001":
                    viewHolder.iv_module.setImageResource(R.mipmap.img_pig_select);
                    viewHolder.tv_module.setTextColor(context.getResources().getColor(R.color.text_black));
                    break;
                case "011004":
                    viewHolder.iv_module.setImageResource(R.mipmap.img_chicken_select);
                    viewHolder.tv_module.setTextColor(context.getResources().getColor(R.color.text_black));
                    break;
                case "011002":
                    viewHolder.iv_module.setImageResource(R.mipmap.img_cattle_select);
                    viewHolder.tv_module.setTextColor(context.getResources().getColor(R.color.text_black));
                    break;
                case "011003":
                    viewHolder.iv_module.setImageResource(R.mipmap.img_sheep_select);
                    viewHolder.tv_module.setTextColor(context.getResources().getColor(R.color.text_black));
                    break;
                case "011005":
                    viewHolder.iv_module.setImageResource(R.mipmap.img_fox_select);
                    viewHolder.tv_module.setTextColor(context.getResources().getColor(R.color.text_black));
                    break;
            }
        }else {
            switch (intention) {
                case "011001":
                    viewHolder.iv_module.setImageResource(R.mipmap.img_pig_unselect);
                    viewHolder.tv_module.setTextColor(context.getResources().getColor(R.color.text_grey));
                    break;
                case "011004":
                    viewHolder.iv_module.setImageResource(R.mipmap.img_chicken_unselect);
                    viewHolder.tv_module.setTextColor(context.getResources().getColor(R.color.text_grey));
                    break;
                case "011002":
                    viewHolder.iv_module.setImageResource(R.mipmap.img_cattle_unselect);
                    viewHolder.tv_module.setTextColor(context.getResources().getColor(R.color.text_grey));
                    break;
                case "011003":
                    viewHolder.iv_module.setImageResource(R.mipmap.img_sheep_unselect);
                    viewHolder.tv_module.setTextColor(context.getResources().getColor(R.color.text_grey));
                    break;
                case "011005":
                    viewHolder.iv_module.setImageResource(R.mipmap.img_fox_unselect);
                    viewHolder.tv_module.setTextColor(context.getResources().getColor(R.color.text_grey));
                    break;
            }
        }


        viewHolder.ll_module.setOnClickListener(
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
        return list_intention_module.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_module;
        public ImageView iv_module;
        public TextView tv_module;
        public ViewHolder(View view){
            super(view);
            ll_module = (LinearLayout) view.findViewById(R.id.ll_module);
            iv_module = (ImageView) view.findViewById(R.id.iv_module);
            tv_module = (TextView) view.findViewById(R.id.tv_module);
        }
    }
}





















