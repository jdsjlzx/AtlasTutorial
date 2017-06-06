package com.xylife.secondbundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class SecondBundleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_bundle);

        // 加载远程bundle，LoadingBundle打包的时候不编译进apk中
        findViewById(R.id.remote_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(v.getContext(), "com.xylife.remotebundle.RemoteBundleActivity");
                startActivity(intent);
            }
        });

        Toast.makeText(this,"SecondBundleActivity999", Toast.LENGTH_SHORT).show();
    }
}