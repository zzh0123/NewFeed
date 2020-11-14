package ys.app.feed.bean;


import com.hgdendi.expandablerecycleradapter.BaseExpandableRecyclerViewAdapter;

import java.io.Serializable;
import java.util.List;

public class GoodsTitle implements BaseExpandableRecyclerViewAdapter.BaseGroupBean<GoodsTitleSub>, Serializable {

    private List<GoodsTitleSub> mList;
    private String code; //
    private String name; //

//    GoodsTitle(@NonNull List<GoodsTitleSub> list, @NonNull String name) {
//        mList = list;
//        mName = name;

    public List<GoodsTitleSub> getmList() {
        return mList;
    }

    public void setmList(List<GoodsTitleSub> mList) {
        this.mList = mList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    }

    @Override
    public int getChildCount() {
        return mList.size();
    }

    @Override
    public boolean isExpandable() {
        return getChildCount() > 0;
    }


    @Override
    public GoodsTitleSub getChildAt(int index) {
        return mList.size() <= index ? null : mList.get(index);
    }
}
