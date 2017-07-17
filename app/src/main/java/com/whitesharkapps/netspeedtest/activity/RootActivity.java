package com.whitesharkapps.netspeedtest.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.whitesharkapps.netspeedtest.R;
import com.whitesharkapps.netspeedtest.fragment.ClearFragment;
import com.whitesharkapps.netspeedtest.fragment.NormalFragment;
import com.whitesharkapps.netspeedtest.fragment.NowFragment;
import com.whitesharkapps.netspeedtest.service.RTxBytes_Service;
import com.whitesharkapps.netspeedtest.service.Traffic_Service;



public class RootActivity extends AppCompatActivity {
    private Fragment normalFragment;
    private Fragment clearFragment ;
    private Fragment nowFragment;

    private FragmentManager manager = getSupportFragmentManager();
    private Fragment mContent = new Fragment();

    public void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = manager.beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.content, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = manager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_normalspeed:
                    switchContent(mContent,normalFragment);
                    mContent = normalFragment;
                    return true;
                case R.id.navigation_appsclear:
                    switchContent(mContent,clearFragment);
                    mContent = clearFragment;
                    return true;
                case R.id.navigation_nowspeed:
                    switchContent(mContent,nowFragment);
                    mContent = nowFragment;
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.home_bottom_checked));
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_root);

        normalFragment = new NormalFragment();
        clearFragment = new ClearFragment();
        nowFragment = new NowFragment();
        switchContent(mContent,normalFragment);
        mContent = normalFragment;

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_normalspeed);

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };

        int[] colors = new int[]{getResources().getColor(R.color.home_bottom_normal),
                getResources().getColor(R.color.home_bottom_checked)
        };
        ColorStateList csl = new ColorStateList(states, colors);
        navigation.setItemTextColor(csl);
        navigation.setItemIconTintList(csl);

        Intent intent1 = new Intent(RootActivity.this, RTxBytes_Service.class);
        startService(intent1);
        Intent intent2 = new Intent(RootActivity.this,Traffic_Service.class);
        startService(intent2);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switch (intent.getFlags()){
            case 100:
                switchContent(mContent,clearFragment);
                break;
            default:
                break;
        }
    }
}

