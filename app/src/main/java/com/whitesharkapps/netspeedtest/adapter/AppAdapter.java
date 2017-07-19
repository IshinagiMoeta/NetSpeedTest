package com.whitesharkapps.netspeedtest.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.whitesharkapps.netspeedtest.R;
import com.whitesharkapps.netspeedtest.info.AppInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ketianxing on 2017/7/6.
 */

public class AppAdapter extends BaseAdapter {

    List<AppInfo> list = new ArrayList<>();
    Context context;
    private final int FIRST_TYPE = 0;
    private final int OTHERS_TYPE = 1;
    private int adnum = 0;


    public AppAdapter(List<AppInfo> list,Context context){
        this.list = list;
        this.context = context;
        adnum = list.size()/10;
    }

    @Override
    public int getCount() {
        return list.size()+adnum;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i%10!=0) {
            CommonViewHolder holder = CommonViewHolder.get(context, view, viewGroup, R.layout.app_listview_item, i);
            AppInfo info = list.get(i-(i/10));
            holder.setText(R.id.app_name, info.getAppName()).
                    setText(R.id.app_uid, String.valueOf(info.getAppUid())).
                    setText(R.id.app_totalRxBytes, info.getRxBytes()).
                    setText(R.id.app_totalTxBytes, info.getTxBytes()).
                    setImage(R.id.app_image, info.getImage());
            return holder.getConvertView();
        }else{
            View adview = View.inflate(context,R.layout.app_listview_ad,null);
            LinearLayout linear = adview.findViewById(R.id.banner_list);
            AdView adView = new AdView(context,context.getString(R.string.fbId_Banner), AdSize.BANNER_HEIGHT_50);
            linear.addView(adView);
            adView.loadAd();
            adview.setTag("ad");
            return adview;
        }
    }
}
