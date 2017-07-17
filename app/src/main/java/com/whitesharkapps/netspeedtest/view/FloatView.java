package com.whitesharkapps.netspeedtest.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.whitesharkapps.netspeedtest.R;
import com.whitesharkapps.netspeedtest.service.Traffic_Service;

public class FloatView extends View {

    WindowManager mWManger;
    WindowManager.LayoutParams mWManParams;

    public View view;

    public boolean isShow = false;

    //初始位置
    private float startX;
    private float startY;

    //坐标
    private float x;
    private float y;

    //
    private float mTouchSatrtX;
    private float mTouchStartY;

    public TextView tv_show;

    Context mContext;

    public FloatView(Context context) {
        super(context);
        this.mContext = context;
    }
    public FloatView(Context context, AttributeSet attrs)     //Constructor that is called when inflating a view from XML
    {
        super(context, attrs);
        this.mContext = context;
    }
    public FloatView(Context context, AttributeSet attrs, int defStyle)     //Perform inflation from XML and apply a class-specific base style
    {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    public void offShow() {
//        Intent intent = new Intent(mContext,Traffic_Service.class);
//        mContext.stopService(intent);
        view.setVisibility(View.GONE);

        mWManger.updateViewLayout(view, mWManParams);
        isShow = false;
    }

    public void onShow() {
//        Intent intent = new Intent(mContext,Traffic_Service.class);
//        mContext.startService(intent);
        view.setVisibility(View.VISIBLE);

        mWManger.updateViewLayout(view, mWManParams);
        isShow = true;
    }

    public boolean isShow(){
        return isShow;
    }
    /**
     * 初始化mWManger,mWManParams
     */
    public void show(){
        isShow = true;
        mWManger = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWManParams = new WindowManager.LayoutParams();

        //设置LayoutParams的参数
        mWManParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;//设置系统级窗口
        mWManParams.flags |= 8;
        //调整悬浮窗到左上角
        mWManParams.gravity = Gravity.TOP|Gravity.LEFT;



        //悬浮窗的长宽数据
        mWManParams.width = 250;
        mWManParams.height = 250;

        //以屏幕左上角为源点，设置x，y
        mWManParams.x = 5;
        mWManParams.y = 5;

        mWManParams.format = -3;//透明

        //加载悬浮窗布局文件
        view = LayoutInflater.from(mContext).inflate(R.layout.traffic_view, null);


        mWManger.addView(view, mWManParams);


        view.setOnTouchListener(new OnTouchListener() {
            /**
             * 改变悬浮窗位置
             */
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //获取相对屏幕的位置，即以屏幕左上角为原点
                x = event.getRawX();
                y = event.getRawY() - 25;//25为系统状态栏的高度
                //Log.e("初始位置", x+"======="+y);

                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = x;
                        startY = y;

                        //获取相对View的坐标,以view的左上角为原点
                        mTouchSatrtX = event.getX();
                        mTouchStartY = event.getY();

                        //Log.e("相对view的位置", mTouchSatrtX+"--------"+mTouchStartY);

                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatePosition();

                        break;

                    case MotionEvent.ACTION_UP:
                        updatePosition();

                        mTouchSatrtX = mTouchStartY =0;

                        break;
                }



                return true;
            }
        });

        /**
         * 关闭悬浮窗图标点击事件
         */
        tv_show = (TextView) view.findViewById(R.id.tv_show);



    }

    /**
     * 更新悬浮窗的位置
     *
     */
    public void updatePosition(){
        mWManParams.x = (int) (x - mTouchSatrtX);
        mWManParams.y = (int) (y - mTouchStartY);

        mWManger.updateViewLayout(view, mWManParams);
    }
    /**
     * 控制关闭悬浮窗按钮的显示与隐藏
     */
}
