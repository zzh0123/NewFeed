package ys.app.feed.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.bean.Score;
import ys.app.feed.utils.DateUtils;

/**
 * Created by jianghejie on 15/11/26.
 */
public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<Score> list_score = null;
    public Integer type;
    private ItemClickCallBack clickCallBack;

    public ScoreAdapter(ArrayList<Score> list_score, Integer type) {
        this.list_score = list_score;
        this.type = type;
    }
    public void setType(Integer type){
        this.type = type;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_score,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        String date = DateUtils.stampToDate(list_score.get(position).getCreateDate());
        viewHolder.tv_sign_date_title.setText(list_score.get(position).getMessage());
        viewHolder.tv_sign_date.setText(date);

        if (type == 0){
            viewHolder.tv_score_income_outcome.setText("+" + list_score.get(position).getScore()); // 积分收入
        } else {
            viewHolder.tv_score_income_outcome.setText("-" + list_score.get(position).getScore()); // 积分支出
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return list_score.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_sign_date_title;
        public TextView tv_sign_date;
        public TextView tv_score_income_outcome;
        public ViewHolder(View view){
            super(view);
            tv_sign_date_title = (TextView) view.findViewById(R.id.tv_sign_date_title);
            tv_sign_date = (TextView) view.findViewById(R.id.tv_sign_date);
            tv_score_income_outcome = (TextView) view.findViewById(R.id.tv_score_income_outcome);
        }
    }
}





















