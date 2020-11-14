package ys.app.feed.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import ys.app.feed.MyApplication;
import ys.app.feed.R;
import ys.app.feed.widget.ClearEditText;

public class GetbackPasswordActivity extends Activity implements View.OnClickListener{

    private ClearEditText et_phone_num; // 输入手机号
    private Button bt_next_step; // 下一步
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getback_password);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        initView();
    }

    private void initView(){
        et_phone_num = (ClearEditText) findViewById(R.id.et_phone_num); // 输入手机号
        bt_next_step = (Button) findViewById(R.id.bt_next_step); // 下一步

        bt_next_step.setOnClickListener(this);

        et_phone_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_phone_num.getText().toString().trim().length() == 11){
                    bt_next_step.setClickable(true);
                    bt_next_step.setBackgroundResource(R.drawable.bt_rectangle_shape_green);
                } else {
                    bt_next_step.setClickable(false);
                    bt_next_step.setBackgroundResource(R.drawable.bt_rectangle_shape_grey);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_next_step: // 下一步
                Intent intent = new Intent(GetbackPasswordActivity.this, SetPasswordActivity.class);
                intent.putExtra("phone_num", et_phone_num.getText().toString().trim());
                startActivity(intent);
                finish();
                break;
        }
    }
}
