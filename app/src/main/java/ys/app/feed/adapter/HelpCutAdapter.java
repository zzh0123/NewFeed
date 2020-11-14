package ys.app.feed.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.bean.HelpCutItem;
import ys.app.feed.bean.MyInviteItem;
import ys.app.feed.utils.DateUtils;
import ys.app.feed.widget.CircleImageView;

/**
 * Created by jianghejie on 15/11/26.
 */
public class HelpCutAdapter extends RecyclerView.Adapter<HelpCutAdapter.ViewHolder>{
    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<HelpCutItem> list_helpCutItem  = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public HelpCutAdapter(Context context, ArrayList<HelpCutItem> list_helpCutItem ) {
        this.context = context;
        this.list_helpCutItem = list_helpCutItem;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_help_cut,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        Glide.with(context).load(list_helpCutItem.get(position).getHeadPortrait())
//                    .skipMemoryCache(true)
                .into(viewHolder.iv_head);

        viewHolder.tv_user_name.setText(list_helpCutItem.get(position).getName());
        viewHolder.tv_user_vip_type.setText(list_helpCutItem.get(position).getUserTypeStr());

        Long cutDate = list_helpCutItem.get(position).getCutDate();
        viewHolder.tv_help_cut_date.setText(DateUtils.stampToDate_Day(cutDate + ""));
        viewHolder.tv_help_cut_money.setText(list_helpCutItem.get(position).getCutMoney() + "");
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return list_helpCutItem.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView iv_head; // 头像
        public TextView tv_user_name; // 用户名
        public TextView tv_user_vip_type; // 会员类型

        public TextView tv_help_cut_date; // 帮砍时间
        public TextView tv_help_cut_money; // 帮砍金额

        public ViewHolder(View view){
            super(view);
            iv_head = (CircleImageView) view.findViewById(R.id.iv_head);
            tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            tv_user_vip_type = (TextView) view.findViewById(R.id.tv_user_vip_type);
            tv_help_cut_date = (TextView) view.findViewById(R.id.tv_help_cut_date);
            tv_help_cut_money = (TextView) view.findViewById(R.id.tv_help_cut_money);
        }
    }

}





















