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

/**
 * Created by jianghejie on 15/11/26.
 */
public class VipPackageAdapter extends RecyclerView.Adapter<VipPackageAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<ModuleVipPackageItem> list_module_vip_package = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public VipPackageAdapter(Context context, ArrayList<ModuleVipPackageItem> list_module_vip_package) {
        this.list_module_vip_package = list_module_vip_package;
        this.context = context;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_module_vip_package,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
//        viewHolder.tv_vip_package_date.setText(list_module_vip_package.get(position).getVipPackageDate());
        viewHolder.tv_vip_package_price.setText(list_module_vip_package.get(position).getMoney());
        viewHolder.tv_vip_package_type.setText(list_module_vip_package.get(position).getName());
        if (list_module_vip_package.get(position).isChecked()){
            viewHolder.tv_vip_package_price.setTextColor(context.getResources().getColor(R.color.text_red));
//            viewHolder.tv_vip_package_type.setTextColor(context.getResources().getColor(R.color.bg_red));
        } else {
            viewHolder.tv_vip_package_price.setTextColor(context.getResources().getColor(R.color.text_black));
//            viewHolder.tv_vip_package_type.setTextColor(context.getResources().getColor(R.color.text_black));
        }
        viewHolder.ll_vip_package.setOnClickListener(
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
        return list_module_vip_package.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_vip_package;
        public TextView tv_vip_package_date;
        public TextView tv_vip_package_price;
        public TextView tv_vip_package_type;
        public ViewHolder(View view){
            super(view);
            ll_vip_package = (LinearLayout) view.findViewById(R.id.ll_vip_package);
            tv_vip_package_date = (TextView) view.findViewById(R.id.tv_vip_package_date);
            tv_vip_package_price = (TextView) view.findViewById(R.id.tv_vip_package_price);
            tv_vip_package_type = (TextView) view.findViewById(R.id.tv_vip_package_type);
        }
    }
}





















