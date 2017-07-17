package com.whitesharkapps.netspeedtest.info;

import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.text.DecimalFormat;

/**
 * Created by ketianxing on 2017/7/6.
 */

public class AppInfo {
    private String appName = "";
    private long appUid = 0;
    private long rxBytes = 0;
    private long txBytes = 0;
    private Drawable image = null;
    private PackageInfo info = null;
    DecimalFormat df = new DecimalFormat("0.00");

    public PackageInfo getInfo() {
        return info;
    }

    public void setInfo(PackageInfo info) {
        this.info = info;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public Drawable getImage() {
        return image;
    }

    public String getAppName() {
        return appName;
    }

    public long getAppUid() {
        return appUid;
    }

    public long getRxBytesLong() {
        return rxBytes;
    }

    public long getTxBytesLong() {
        return txBytes;
    }

    public String getTotalBytes(){ return speed((double)rxBytes+(double)txBytes); }

    public String getRxBytes() {
        return speed((double)rxBytes);
    }

    public String getTxBytes() {
        return speed((double)txBytes);
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppUid(long appUid) {
        this.appUid = appUid;
    }

    public void setRxBytes(long rxBytes) {
        this.rxBytes = rxBytes;
    }

    public void setTxBytes(long txBytes) {
        this.txBytes = txBytes;
    }

    private String speed(double len) {
        String size = len + "B";
        if (len > 1024) {
            len = len / 1024;
            size = df.format(len) + "KB";
        }
        if (len > 1024) {
            len = len / 1024;
            size = df.format(len) + "MB";
        }
        if (len > 1024) {
            len = len / 1024;
            size = df.format(len) + "GB";
        }
        return size;
    }
}
