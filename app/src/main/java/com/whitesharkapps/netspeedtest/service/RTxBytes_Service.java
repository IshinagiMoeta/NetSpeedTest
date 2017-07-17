package com.whitesharkapps.netspeedtest.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.whitesharkapps.netspeedtest.R;
import com.whitesharkapps.netspeedtest.activity.RootActivity;
import com.whitesharkapps.netspeedtest.info.AppInfo;
import com.whitesharkapps.netspeedtest.info.SwitchEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RTxBytes_Service extends Service {

    PackageManager pm;//获取系统应用包管理
    private Handler mHandler;
    final static private int per = 5;
    ArrayList<AppInfo> list = new ArrayList<AppInfo>();

    private boolean isShow = true;
    private RemoteViews notifyView;
    private NotificationCompat.Builder notifyBuilder;
    private NotificationManager manager;
    private Notification notification;

    private Runnable mRunnable = new Runnable() {
        // 每3秒钟获取一次数据，求平均，以减少读取系统文件次数，减少资源消耗
        @Override
        public void run() {
            refresh();
            mHandler.postDelayed(mRunnable, per * 1000);
        }
    };

    class SwitchBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case SwitchEnum.notifySwitch_off:
                    isShow = true;
                    manager.notify(100,notification);
                    break;
                case SwitchEnum.notifySwitch_on:
                    isShow = false;
                    manager.cancel(100);
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        mHandler.postDelayed(mRunnable, 0);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pm = getPackageManager();
        notifyBuilder = new NotificationCompat.Builder(getApplicationContext());
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notification = new Notification.Builder(this)

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Intent intent = new Intent();
                intent.setAction(SwitchEnum.listUpData);
                sendBroadcast(intent);

                if (isShow) {
                    refreshNotifyView();
                    manager.notify(100,notification);
                }
            }
        };

        if (isShow) {
            refresh();

            notifyView = new RemoteViews(getPackageName(),R.layout.notify_view);
            refreshNotifyView();

            notifyBuilder.setSmallIcon(R.mipmap.ic_launcher);
            notifyBuilder.setContent(notifyView);
            notification = notifyBuilder.build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            Intent intent = new Intent(this, RootActivity.class);
            intent.setFlags(100);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            notification.contentIntent = pIntent;
            manager.notify(100, notification);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(SwitchEnum.notifySwitch_off);
        filter.addAction(SwitchEnum.notifySwitch_on);
        SwitchBroadCastReceiver broadcastReceiver = new SwitchBroadCastReceiver();
        registerReceiver(broadcastReceiver, filter);
    }

    private void refreshNotifyView(){
        notifyView.setTextViewText(R.id.notify_app_name_1,list.get(0).getAppName());
        notifyView.setTextViewText(R.id.notify_app_name_2,list.get(1).getAppName());
        notifyView.setTextViewText(R.id.notify_app_name_3,list.get(2).getAppName());
        notifyView.setTextViewText(R.id.notify_app_bytes_1,list.get(0).getTotalBytes());
        notifyView.setTextViewText(R.id.notify_app_bytes_2,list.get(1).getTotalBytes());
        notifyView.setTextViewText(R.id.notify_app_bytes_3,list.get(2).getTotalBytes());
    }

    public void refresh() {
        list.clear();
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
//                return (int)(lhs.getRxBytesLong() - rhs.getRxBytesLong());
                return (int)((rhs.getRxBytesLong()+rhs.getTxBytesLong())-(lhs.getRxBytesLong()+lhs.getTxBytesLong()));
            }
        });

        Message msg = mHandler.obtainMessage();
        msg.what = 1;
        mHandler.sendMessage(msg);
    }
}
