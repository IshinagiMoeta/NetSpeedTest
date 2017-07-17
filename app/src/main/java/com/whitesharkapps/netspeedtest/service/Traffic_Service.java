package com.whitesharkapps.netspeedtest.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;

import com.whitesharkapps.netspeedtest.info.SwitchEnum;
import com.whitesharkapps.netspeedtest.view.FloatView;

import java.text.DecimalFormat;

public class Traffic_Service extends Service {

    public long rxBytes = 0;
    public long rxPackets = 0;
    public long txBytes = 0;
    public long txPackets = 0;
    public long totalRxBytes = 0;
    public long totalTxBytes = 0;
    public boolean isShowView = true;
    private Handler mHandler;
    private FloatView view;
//    每秒
    final static private int per = 3;
//    小数点格式
    DecimalFormat df = new DecimalFormat("0.00");
    /**
     * 定义线程周期性地获取网速
     */
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
                case SwitchEnum.floatSwitch_off:
                    view.onShow();
                    isShowView = true;
                    break;
                case SwitchEnum.floatSwitch_on:
                    view.offShow();
                    isShowView = false;
                    break;
                default:
                    break;
            }

        }
    }


    /**
     * 启动服务时就开始启动线程获取网速
     */
    @Override
    public void onStart(Intent intent, int startId) {
        if (Build.VERSION.SDK_INT >= 23) {
            if(!Settings.canDrawOverlays(this)) {
                Intent intents = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(intents);
                return;
            } else {
//                Android6.0以上
                mHandler.postDelayed(mRunnable, 0);
            }
        } else {
//            Android6.0以下，不用动态声明权限
            mHandler.postDelayed(mRunnable, 0);
        }
    }


    /**
     * 在服务结束时删除消息队列
     */
    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    ;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(SwitchEnum.floatSwitch_off);
        filter.addAction(SwitchEnum.floatSwitch_on);
        SwitchBroadCastReceiver broadcastReceiver = new SwitchBroadCastReceiver();
        registerReceiver(broadcastReceiver, filter);

        rxBytes = TrafficStats.getMobileRxBytes();
        rxPackets = TrafficStats.getMobileRxPackets();
        txBytes = TrafficStats.getMobileTxBytes();
        txPackets = TrafficStats.getMobileTxPackets();
        totalRxBytes = TrafficStats.getTotalRxBytes();
        totalTxBytes = TrafficStats.getTotalTxBytes();
        if(isShowView){
            view = new FloatView(this);
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if(!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(intent);
                return;
            } else {
//                Android6.0以上
                if (isShowView){
                    if (view!=null) {
                        view.show();
                    }
                    mHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (isShowView){
                                if (msg.what == 1) {
                                    String text;
                                    if ((double) (msg.obj) / per > 1024 * 1024) {
                                        text = " " +  df.format((double)msg.obj / (1024 * 1024 * per)) + "m";
                                    } else {
                                        text = " " +  df.format((double)msg.obj / (1024 * per)) + "k";
                                    }
                                    view.tv_show.setText(text);
                                }
                            }

                        }
                    };
                }
            }
        } else {
//            Android6.0以下，不用动态声明权限
            if (isShowView){
                if (view!=null) {
                    view.show();
                }
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (isShowView){
                            if (msg.what == 1) {
                                String text;
                                if ((double) (msg.obj) / per > 1024 * 1024) {
                                    text = " " +  df.format((double)msg.obj / (1024 * 1024 * per)) + "m";
                                } else {
                                    text = " " +  df.format((double)msg.obj / (1024 * per)) + "k";
                                }
                                view.tv_show.setText(text);
                            }
                        }
                    }
                };
            }
        }
    }

//    /**
//     * 实时读取系统流量文件，更新
//     */
    public void refresh() {
////         读取系统流量文件
//                //2g/3g接收的流量
//        Log.e("----2g/3g接收的流量",String.valueOf(TrafficStats.getMobileRxBytes() - rxBytes));
//        //2g/3g接收的包信息
//        Log.e("----2g/3g接收的包信息",String.valueOf(TrafficStats.getMobileRxPackets() - rxPackets));
//        //2g/3g上传的;流量
//        Log.e("----2g/3g上传的流量",String.valueOf(TrafficStats.getMobileTxBytes() - txBytes));
//        //2g/3g上传的包信息
//        Log.e("----2g/3g上传的包信息",String.valueOf(TrafficStats.getMobileTxPackets() - rxPackets));
//        //手机总共接收的流量
//        Log.e("----手机总共接收的流量",String.valueOf(TrafficStats.getTotalRxBytes() - totalRxBytes));
//        //手机总共上传的流量
//        Log.e("----手机总共上传的流量",String.valueOf(TrafficStats.getTotalTxBytes() - totalTxBytes)+"\n\n\n\n");
////         每秒下载的字节数

        double traffic_data = 0;
        traffic_data = (TrafficStats.getTotalRxBytes() - totalRxBytes)/per;
        rxBytes = TrafficStats.getMobileRxBytes();
        rxPackets = TrafficStats.getMobileRxPackets();
        txBytes = TrafficStats.getMobileTxBytes();
        txPackets = TrafficStats.getMobileTxPackets();
        totalRxBytes = TrafficStats.getTotalRxBytes();
        totalTxBytes = TrafficStats.getTotalTxBytes();

        Message msg = mHandler.obtainMessage();
        msg.what = 1;
        msg.obj = traffic_data;
        mHandler.sendMessage(msg);
    }
}