package ys.app.feed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ys.app.feed.R;
import ys.app.feed.bean.ReceivingAddress;


public class ReceivingAddressAdapter extends BaseAdapter implements View.OnClickListener {
    private ArrayList<ReceivingAddress> receivingAddress_list;
    private Context mContext;
    private InnerItemOnclickListener innerItemOnclickListener;

    public ReceivingAddressAdapter(Context mContext, ArrayList<ReceivingAddress> receivingAddress_list) {
        this.mContext = mContext;
        this.receivingAddress_list = receivingAddress_list;
    }

    @Override
    public int getCount() {
        return receivingAddress_list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_receiving_address, parent, false);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_phone_num = (TextView) convertView.findViewById(R.id.tv_phone_num);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);

            holder.ll_set_default = (LinearLayout) convertView.findViewById(R.id.ll_set_default);
            holder.iv_set_default = (ImageView) convertView.findViewById(R.id.iv_set_default);

            holder.tv_edit = (TextView) convertView.findViewById(R.id.tv_edit);
            holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String address = receivingAddress_list.get(position).getCity() + receivingAddress_list.get(position).getArea()
                + receivingAddress_list.get(position).getDetailAddress();
        holder.tv_name.setText(receivingAddress_list.get(position).getName());
        holder.tv_phone_num.setText(receivingAddress_list.get(position).getPhone());
        holder.tv_address.setText(address);

        if (receivingAddress_list.get(position).getIsDefault() == 1) {
            holder.iv_set_default.setImageResource(R.mipmap.set_default);
        } else {
            holder.iv_set_default.setImageResource(R.mipmap.set_not_default);
        }

        holder.ll_set_default.setOnClickListener(this);
        holder.tv_edit.setOnClickListener(this);
        holder.tv_delete.setOnClickListener(this);

        holder.ll_set_default.setTag(position);
        holder.tv_edit.setTag(position);
        holder.tv_delete.setTag(position);

        return convertView;
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_phone_num;
        TextView tv_address;

        LinearLayout ll_set_default;
        ImageView iv_set_default;

        TextView tv_edit;
        TextView tv_delete;
    }

    public interface InnerItemOnclickListener {
        void itemClick(View v);
    }

    public void setOnInnerItemOnClickListener(InnerItemOnclickListener innerItemOnclickListener) {
        this.innerItemOnclickListener = innerItemOnclickListener;
    }

    @Override
    public void onClick(View v) {
        innerItemOnclickListener.itemClick(v);
    }
}