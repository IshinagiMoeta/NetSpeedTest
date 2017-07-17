package com.whitesharkapps.netspeedtest.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.whitesharkapps.netspeedtest.R;
import com.whitesharkapps.netspeedtest.adapter.AppAdapter;
import com.whitesharkapps.netspeedtest.info.AppInfo;
import com.whitesharkapps.netspeedtest.info.SwitchEnum;
import com.whitesharkapps.netspeedtest.service.Traffic_Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClearFragment extends Fragment {

    private ListView listView;
    AppAdapter appAdapter;
    ArrayList<AppInfo> list;

    private Handler mHandler;
    final static private int per = 5;
    private Runnable mRunnable = new Runnable() {
        // 每3秒钟获取一次数据，求平均，以减少读取系统文件次数，减少资源消耗
        @Override
        public void run() {
            refresh();
            mHandler.postDelayed(mRunnable, per * 1000);
        }
    };

    public ClearFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clear, container, false);
        listView = view.findViewById(R.id.app_listview);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        list = new ArrayList<AppInfo>();
        refresh();
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable,0);
        appAdapter = new AppAdapter(list, getContext());
        listView.setAdapter(appAdapter);

    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    public void refresh() {
        list.clear();
        PackageManager pm = getContext().getPackageManager();
        //获取每个包内的androidmanifest.xml信息，它的权限等等
        List<PackageInfo> pinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        //遍历每个应用包信息
        for (PackageInfo info : pinfos) {
            //请求每个程序包对应的androidManifest.xml里面的权限
            String[] premissions = info.requestedPermissions;
            if (premissions != null && premissions.length > 0) {
                //找出需要网络服务的应用程序
                for (String premission : premissions) {
                    if ("android.permission.INTERNET".equals(premission)) {
                        //获取每个应用程序在操作系统内的进程id
                        int uId = info.applicationInfo.uid;
                        //如果返回-1，代表不支持使用该方法，注意必须是2.2以上的
                        long rx = TrafficStats.getUidRxBytes(uId);
                        //如果返回-1，代表不支持使用该方法，注意必须是2.2以上的
                        long tx = TrafficStats.getUidTxBytes(uId);

                        if (rx == -1){
                            rx = 0;
                        }
                        if (tx == -1){
                            tx = 0;
                        }

                        AppInfo appInfo = new AppInfo();
                        Drawable image = pm.getApplicationIcon(info.applicationInfo);
                        appInfo.setImage(image);
                        String name = pm.getApplicationLabel(info.applicationInfo).toString();
                        appInfo.setAppName(name);
                        appInfo.setAppUid(uId);
                        appInfo.setInfo(info);
                        appInfo.setRxBytes(rx);
                        appInfo.setTxBytes(tx);
                        list.add(appInfo);

                    }
                }
            }
        }
        Collections.sort(list, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo lhs, AppInfo rhs) {
                return (int)((rhs.getRxBytesLong()+rhs.getTxBytesLong())-(lhs.getRxBytesLong()+lhs.getTxBytesLong()));
            }
        });
    }

}
