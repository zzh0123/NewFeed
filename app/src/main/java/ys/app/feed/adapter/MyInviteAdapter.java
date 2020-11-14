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
import ys.app.feed.bean.MyInviteItem;
import ys.app.feed.bean.RecommendGoodsItem;
import ys.app.feed.utils.DateUtils;

/**
 * Created by jianghejie on 15/11/26.
 */
public class MyInviteAdapter extends RecyclerView.Adapter<MyInviteAdapter.ViewHolder>{
    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<MyInviteItem> list_myInviteItem = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public MyInviteAdapter(Context context, ArrayList<MyInviteItem> list_myInviteItem) {
        this.context = context;
        this.list_myInviteItem = list_myInviteItem;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_my_invite,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        viewHolder.tv_phone_num.setText(list_myInviteItem.get(position).getBeInvitedPhone());
        String create_date = DateUtils.stampToDate_Day(list_myInviteItem.get(position).getCreateDate() + "");
        viewHolder.tv_createDate.setText(create_date);

        Integer state = list_myInviteItem.get(position).getState() ; // 0.未注册|1.已注册
        if (state == 0){
            viewHolder.tv_state.setText("未注册");
        } else {
            viewHolder.tv_state.setText("已注册");
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return list_myInviteItem.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_phone_num; // 手机号
        public TextView tv_createDate; // 创建时间
        public TextView tv_state; // 已注册

        public ViewHolder(View view){
            super(view);
            tv_phone_num = (TextView) view.findViewById(R.id.tv_phone_num);
            tv_createDate = (TextView) view.findViewById(R.id.tv_createDate);
            tv_state = (TextView) view.findViewById(R.id.tv_state);
        }
    }

}





















