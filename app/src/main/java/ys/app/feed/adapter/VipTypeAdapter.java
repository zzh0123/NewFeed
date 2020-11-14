package ys.app.feed.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.bean.ModuleVipPackageItem;
import ys.app.feed.bean.VipTypeItem;

/**
 * Created by jianghejie on 15/11/26.
 */
public class VipTypeAdapter extends RecyclerView.Adapter<VipTypeAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<VipTypeItem> list_vipTypeItem = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public VipTypeAdapter(Context context, ArrayList<VipTypeItem> list_vipTypeItem) {
        this.list_vipTypeItem = list_vipTypeItem;
        this.context = context;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_module_vip_type,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
//        viewHolder.tv_vip_package_date.setText(list_module_vip_package.get(position).getVipPackageDate());
        viewHolder.tv_vip_type.setText(list_vipTypeItem.get(position).getName());
//        String code = list_vipTypeItem.get(position).getCode();
        boolean isSelect = list_vipTypeItem.get(position).isSelected();
        if (isSelect){
            viewHolder.tv_vip_type.setBackgroundResource(R.drawable.bt_rectangle_shape_green);
        } else {
            viewHolder.tv_vip_type.setBackgroundResource(R.drawable.bt_rectangle_shape_grey);
        }

        viewHolder.tv_vip_type.setOnClickListener(
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
        return list_vipTypeItem.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_vip_type;
        public ViewHolder(View view){
            super(view);
            tv_vip_type = (TextView) view.findViewById(R.id.tv_vip_type);
        }
    }

}





















