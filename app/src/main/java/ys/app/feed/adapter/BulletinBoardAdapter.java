package ys.app.feed.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.iwgang.countdownview.CountdownView;
import me.bakumon.library.adapter.BulletinAdapter;
import ys.app.feed.R;
import ys.app.feed.bean.GroupDataItem;
import ys.app.feed.widget.CircleImageView;

/**
 * SaleAdapter
 * Created by bakumon on 17-3-31.
 */

public class BulletinBoardAdapter extends BulletinAdapter<GroupDataItem> {

    private Context context;
    public BulletinBoardAdapter(Context context, List<GroupDataItem> data) {
        super(context, data);
        this.context = context;
    }

    @Override
    public View getView(int position) {
        View view = getRootView(R.layout.item_bulletin_board);
        CircleImageView iv_head = (CircleImageView) view.findViewById(R.id.iv_head);
        TextView tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        TextView tv_group_buy_type = (TextView) view.findViewById(R.id.tv_group_buy_type);
        TextView tv_lack_count = (TextView) view.findViewById(R.id.tv_lack_count);
        CountdownView cv_countdownView = (CountdownView) view.findViewById(R.id.cv_countdownView);

        Glide.with(context)
                .load(mData.get(position).getHeadPortrait())
                .into(iv_head);
        tv_user_name.setText(mData.get(position).getName());
        tv_group_buy_type.setText(mData.get(position).getGroupScale());
        tv_lack_count.setText(mData.get(position).getSurplusCount() + "");
        long time = (long) ((mData.get(position).getSecond()) * 1000);
        Log.i("--time---", "--time--" + time);
        cv_countdownView.start(time);

        return view;
    }
}
