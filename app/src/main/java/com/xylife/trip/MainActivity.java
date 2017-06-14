package com.xylife.trip;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.taobao.android.ActivityGroupDelegate;
import com.xylife.trip.update.Updater;

public class MainActivity extends AppCompatActivity {

    private ActivityGroupDelegate mActivityDelegate;
    private ViewGroup mActivityGroupContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((BottomNavigationView)findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mActivityDelegate = new ActivityGroupDelegate(this,savedInstanceState);
        mActivityGroupContainer = (ViewGroup) findViewById(R.id.content);
        //默认路由到第一个bundle的Activity
        switchToActivity("home", "com.xylife.firstbundle.FirstBundleActivity");

        findViewById(R.id.remote_view).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Updater.update(getBaseContext());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }.execute();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    final int menuId = item.getItemId();
                    if (menuId == R.id.navigation_home) {
                        switchToActivity("home", "com.xylife.firstbundle.FirstBundleActivity");
                    }

                    if (menuId == R.id.navigation_dashboard) {
                        switchToActivity("my", "com.xylife.secondbundle.SecondBundleActivity");
                    }

                    if (menuId == R.id.navigation_notifications) {
                        switchToActivity("message", "com.xylife.thirdbundle.ThirdBundleActivity");
                        //switchToActivity("message", "com.xylife.firstbundle.UserInfoActivity");
                    }

                    return true;
                }
            };


    private void switchToActivity(String key, String activityname) {
        Intent intent = new Intent();
        intent.setClassName(getBaseContext(), activityname);
        mActivityDelegate.startChildActivity(mActivityGroupContainer, key, intent);
    }
}
