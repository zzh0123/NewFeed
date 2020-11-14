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
import ys.app.feed.bean.PromotionItem;
import ys.app.feed.bean.ReceivingAddress;


public class PromotionAdapter extends BaseAdapter {
    private ArrayList<String> list_promotionItem;
    private Context mContext;

    public PromotionAdapter(Context mContext, ArrayList<String> list_promotionItem) {
        this.mContext = mContext;
        this.list_promotionItem = list_promotionItem;
    }

    @Override
    public int getCount() {
        return list_promotionItem.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_promotion, parent, false);
            holder = new ViewHolder();
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String content = list_promotionItem.get(position);
        holder.tv_content.setText(content);

        return convertView;
    }

    private class ViewHolder {
        TextView tv_content;
    }
}