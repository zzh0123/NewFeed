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
import ys.app.feed.bean.ModuleItem;
import ys.app.feed.widget.CircleImageView;

/**
 * Created by jianghejie on 15/11/26.
 */
public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<ModuleItem> list_module = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public ModuleAdapter(ArrayList<ModuleItem> list_module) {
        this.list_module = list_module;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_module,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        viewHolder.iv_module.setImageResource(list_module.get(position).getModuleImage());
        viewHolder.tv_module.setText(list_module.get(position).getModuleName());

        viewHolder.iv_module.setOnClickListener(
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
        return list_module.size();
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





















