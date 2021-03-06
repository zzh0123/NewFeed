package ys.app.feed.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.bean.MessageItem;
import ys.app.feed.utils.DateUtils;

/**
 * Created by jianghejie on 15/11/26.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<MessageItem> list_message = null;
    private ItemClickCallBack clickCallBack;

    public MessageAdapter(ArrayList<MessageItem> list_message) {
        this.list_message = list_message;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_message,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        String date = DateUtils.stampToDate(list_message.get(position).getCreateDate());
//        viewHolder.tv_message_date.setText(date);
        viewHolder.tv_message_date.setText(date);
        viewHolder.tv_message_content.setText(list_message.get(position).getContent());

        viewHolder.ll_message.setOnClickListener(
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
        return list_message.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_message;
        public TextView tv_message_date;
        public TextView tv_message_content;
        public ViewHolder(View view){
            super(view);
            ll_message = (LinearLayout) view.findViewById(R.id.ll_message);
            tv_message_date = (TextView) view.findViewById(R.id.tv_message_date);
            tv_message_content = (TextView) view.findViewById(R.id.tv_message_content);
        }
    }
}





















