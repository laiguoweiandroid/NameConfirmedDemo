package com.example.nameconfirmeddemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nameconfirmeddemo.util.DataUtil;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btnPostPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());//打印开
        }else{
            Timber.plant(new DataUtil.CrashReportingTree());//打印关
        }
        initViews();
        setListeners();

    }

    /**
     * 初始化布局
     */
    private void initViews() {
        btnPostPic = (Button) findViewById(R.id.post_picture);
    }

    /**
     * 设置控件监听
     */
    private void setListeners() {
        btnPostPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.实名认证
                SwitchToConfirm();
            }
        });
    }

    private void SwitchToConfirm() {
        Intent intent =new Intent(this,ConfirmActivity.class);
        startActivityForResult(intent,1);
    }

}
