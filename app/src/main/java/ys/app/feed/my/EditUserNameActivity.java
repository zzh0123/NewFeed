package ys.app.feed.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.utils.SPUtils;
import ys.app.feed.utils.ToastUtils;
import ys.app.feed.widget.ClearEditText;

public class EditUserNameActivity extends Activity implements View.OnClickListener{

    private LinearLayout ll_back; // 返回

    private ClearEditText et_user_name; // 修改用户名
    private TextView tv_save; // 保 存

    private String name = "";  // 用户名
    private String name_new = "";  // 新用户名

    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_name);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        intent = getIntent();
        name = intent.getStringExtra("name");
        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        et_user_name = (ClearEditText) findViewById(R.id.et_user_name);
        tv_save = (TextView) findViewById(R.id.tv_save);
        ll_back.setOnClickListener(this);
        tv_save.setOnClickListener(this);

        et_user_name.setText(name);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save: // 保存
                if (TextUtils.equals(name, et_user_name.getText().toString().trim())){
                    ToastUtils.showShort(this, "名称与之前的相同");
                    return;
                } else {
                    name_new = et_user_name.getText().toString().trim();
                    intent.putExtra("name_new", name_new);
                    setResult(1, intent);
                    finish();
                }
                break;
            case R.id.ll_back: // 返回
                finish();
                break;
        }
    }

}
