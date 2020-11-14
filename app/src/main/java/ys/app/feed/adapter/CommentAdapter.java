package ys.app.feed.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.util.ArrayList;
import java.util.List;

import ys.app.feed.R;
import ys.app.feed.bean.Comment;
import ys.app.feed.bean.Score;
import ys.app.feed.utils.DateUtils;
import ys.app.feed.widget.CircleImageView;

/**
 * Created by jianghejie on 15/11/26.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    public void setClickCallBack(ItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface ItemClickCallBack{
        void onItemClick(int pos);
    }

    public ArrayList<Comment> list_comment = null;
    private ItemClickCallBack clickCallBack;
    private Context context;

    public CommentAdapter(Context context, ArrayList<Comment> list_comment) {
        this.context = context;
        this.list_comment = list_comment;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_comment,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        Glide.with(context).load(list_comment.get(position).getHeadPortrait())
//                    .skipMemoryCache(true)
                .into(viewHolder.iv_head);
        viewHolder.tv_user_name.setText(list_comment.get(position).getName());
        viewHolder.tv_comment.setText(list_comment.get(position).getContent());
//        String date = DateUtils.stampToDate(list_comment.get(position).getCreateDate() + "");

        List<String> list_imgesUrl = list_comment.get(position).getImgs();
        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
        if (list_imgesUrl != null && list_imgesUrl.size() > 0){
            for (int i=0; i<list_imgesUrl.size(); i++){
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(list_imgesUrl.get(i));
                info.setBigImageUrl(list_imgesUrl.get(i));
                imageInfo.add(info);
            }
        }

        viewHolder.nineGrid.setAdapter(new NineGridViewClickAdapter(context, imageInfo));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return list_comment.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView iv_head;
        public TextView tv_user_name;
        public TextView tv_comment;
        public NineGridView nineGrid;

        public ViewHolder(View view){
            super(view);
            iv_head = (CircleImageView) view.findViewById(R.id.iv_head);
            tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            tv_comment = (TextView) view.findViewById(R.id.tv_comment);
            nineGrid = (NineGridView) view.findViewById(R.id.nineGrid);
        }
    }
}





















