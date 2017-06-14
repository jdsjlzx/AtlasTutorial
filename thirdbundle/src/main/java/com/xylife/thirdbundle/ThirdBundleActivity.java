package com.xylife.thirdbundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ThirdBundleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_bundle);

        //在ThirdBundle里面调用FirstBundle中的activity
        //注意: 官方不推荐bundle间直接依赖。破坏了独立性。推荐封装成service来暴露调用。
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(ThirdBundleActivity.this, "com.xylife.firstbundle.UserInfoActivity");
                startActivity(intent);
            }
        });
    }
}
