package ys.app.feed.orderdetail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import me.shaohui.bottomdialog.BottomDialog;
import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.adapter.ModuleAdapter;
import ys.app.feed.bean.ModuleItem;

public class GroupBuyOrderDetailActivity1 extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    private RecyclerView mRecyclerView;
    private ModuleAdapter adapter_module;
    private ArrayList<ModuleItem> list_module = new ArrayList<ModuleItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_buy_order_detail1);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        // 圆圈分类
        setModuleList();
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 4);
        StaggeredGridLayoutManager sg_layoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_person);
        mRecyclerView.setLayoutManager(sg_layoutManager);
        adapter_module = new ModuleAdapter(list_module);
        mRecyclerView.setAdapter(adapter_module);
    }

    private void setModuleList() {
        list_module.add(new ModuleItem("友料圈", R.mipmap.head));
        list_module.add(new ModuleItem("友料圈", R.mipmap.group_buy_empty));
        list_module.add(new ModuleItem("友料圈", R.mipmap.group_buy_empty));

        list_module.add(new ModuleItem("", R.mipmap.head));
        list_module.add(new ModuleItem("", R.mipmap.group_buy_empty));
        list_module.add(new ModuleItem("", R.mipmap.group_buy_empty));
        list_module.add(new ModuleItem("", R.mipmap.group_buy_empty));

//        list_module.add(new ModuleItem("慧合产品", R.mipmap.huihe_product));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete_order: // 删除订单
//                deleteOrder();
                break;
            case R.id.tv_share_order: // 分享订单
//                if (TextUtils.isEmpty(url)){
//                    ToastUtils.showShort(GroupBuyOrderDetailActivity1.this, "分享链接为空！");
//                    return;
//                }
                // 分享
                BottomDialog.create(getSupportFragmentManager())
                        .setViewListener(new BottomDialog.ViewListener() {
                            @Override
                            public void bindView(View v) {
//                                initViewBottom(v);
                            }
                        })
                        .setLayoutRes(R.layout.layout_share)
                        .setDimAmount(0.9f)
                        .setTag("BottomDialog")
                        .show();
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }
}
