package com.whitesharkapps.netspeedtest.model;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.whitesharkapps.netspeedtest.fragment.NormalFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ketianxing on 2017/7/6.
 */

public class NetSpeedCount {
    private Map<String, String> urlMap;
    private Handler handler = null;
    private NormalFragment.selfSpeedHandler selfHandler = null;
    private int num = 0;


    private static NetSpeedCount instance = null;

    public static NetSpeedCount getInstance(){
        if (instance == null){
            instance = new NetSpeedCount();
        }
        return instance;
    }

    public void startSpeed(NormalFragment.SpeedHandler handler){
        num = 0;
        this.handler = handler;
        this.urlMap = new HashMap<>();
        this.urlMap.put("tv_q", "http://www.google.com/");
        this.urlMap.put("tv_baidu", "http://www.apple.com/");
        this.urlMap.put("tv_apple", "http://www.sina.com/");
        this.urlMap.put("tv_google", "http://www.qq.com/");

        new MyThread().start();

    }

    public void selfStartSpeed(NormalFragment.selfSpeedHandler handler,String web){
        this.selfHandler = handler;
        this.urlMap = new HashMap<>();
        this.urlMap.put("self",web);
        new MySelfThread().start();
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            Iterator<Map.Entry<String, String>> it = urlMap.entrySet().iterator();
            while (it.hasNext()) {
                String speed = "访问失败";
                Map.Entry<String, String> e = it.next();
                String url = e.getValue();
                long time = 0;
                String result = "";
                try {
                    long start = System.currentTimeMillis();
                    Document doc = Jsoup.connect(url).get();
                    long end = System.currentTimeMillis();
                    time = end - start;

                    result = doc.body().html();
                } catch (Exception ex) {

                    ex.printStackTrace();
                }
                if (result.length() > 0) {
                    long len = result.getBytes().length;
                    speed = speed(time, len);
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("speed", speed);
                message.setData(bundle);
                message.what = num;
                num++;
                handler.sendMessage(message);
            }
        }

    }

    private class MySelfThread extends Thread {
        @Override
        public void run() {
            Iterator<Map.Entry<String, String>> it = urlMap.entrySet().iterator();
            while (it.hasNext()) {
                String speed = "访问失败";
                Map.Entry<String, String> e = it.next();
                String url = e.getValue();
                long time = 0;
                String result = "";
                try {
                    long start = System.currentTimeMillis();
                    Document doc = Jsoup.connect(url).get();
                    long end = System.currentTimeMillis();
                    time = end - start;

                    result = doc.body().html();
                } catch (Exception ex) {

                    ex.printStackTrace();
                }
                if (result.length() > 0) {
                    long len = result.getBytes().length;
                    speed = speed(time, len);
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("speed", speed);
                message.setData(bundle);
                message.what = 100;
                selfHandler.sendMessage(message);
            }
        }

    }

    private String speed(long time, long len) {
        String speed = "";
        if (time > 0) {
            long s = len * 1000 / time; // B/ms
            speed = s + "B/s";
            if (s > 1024) {
                s = s / 1024;
                speed = s + "KB/s";
            }
            if (s > 1024) {
                s = s / 1024;
                speed = s + "MB/s";
            }
            if (s > 1024) {
                s = s / 1024;
                speed = s + "GB/s";
            }
            String size = len + "B";
            if (len > 1024) {
                len = len / 1024;
                size = len + "KB";
            }
            if (len > 1024) {
                len = len / 1024;
                size = len + "MB";
            }
            String t = time + "ms";
            if (time > 1000) {
                time = time / 1000;
                t = time + "sec";

                if (time > 60) {
                    time = time / 60;
                    t = time + "min";
                }
            }
            speed = speed + "(" + size + ", " + t + ")";
        }
        return speed;
    }
}
