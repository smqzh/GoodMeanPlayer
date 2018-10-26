package com.gm.player.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gm.player.R;
import com.gm.player.application.GmApplication;
import com.gm.player.application.MySp;
import com.gm.player.service.SuperSocket;
import com.gm.player.widget.ToolBarSet;

import butterknife.ButterKnife;

public abstract  class BaseActivity extends AppCompatActivity{

    public String TAG=this.getClass().getSimpleName();
    public TextView mTvCenterTitle;
    public TextView mTvTime;
    public TextView mTvNumber;
    public Toolbar mToolbar;
    public ToolBarSet mToolBarSet;
    public Context mContext;

    /**
     * 绑定Service
     */
    public ServiceConnection sc;
    public SuperSocket socketService;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(attachLayoutRes());
        ButterKnife.bind(this);
        init();
        GmApplication.getInstance().getActivityManager().pushActivity(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View view = getLayoutInflater().inflate(R.layout.base_head, null);
      //  view.setRotation(MySp.getRotation());
        mTvCenterTitle = (TextView) view.findViewById(R.id.tv_center_title);
        mTvTime = (TextView) view.findViewById(R.id.tvTime);
        mTvNumber = (TextView) view.findViewById(R.id.tvNumber);
        super.setContentView(view);
        initDefaultView(layoutResID);
    }

    private void initDefaultView(int layoutResId) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        View childView = LayoutInflater.from(this).inflate(layoutResId, null);
        container.addView(childView, 0);
    }
    protected  void init(){
        mContext=this;
        mToolBarSet = new ToolBarSet(mToolbar, mTvCenterTitle, this);
        mToolBarSet.setDisplayHomeAsUpEnabled(false);
      /*  Typeface typeface = Typeface.createFromAsset(getAssets(), "yrdzst.ttf");
        mTvNumber.setTypeface(typeface);
        mTvTime.setTypeface(typeface);*/
        mToolBarSet.setTitle("");
        mTvNumber.setText(MySp.getDeviceNumber());

        initViews();
        initData();
    }

    /**
     * 绑定布局文件
     *
     * @return 布局文件ID
     */
    @LayoutRes
    protected abstract int attachLayoutRes();

    /**
     * 初始化视图控件
     */
    protected abstract void initViews();

    /**
     * 初始化数据
     */
    protected abstract void initData();


    // 绑定Service
    public class MyConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            SuperSocket.SocketBinders binder = (SuperSocket.SocketBinders) iBinder;
            socketService = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            socketService=null;
        }
    }

    public  void BindSocketService() {
        /*通过binder拿到service*/
        sc = new MyConnection();
        Intent intent = new Intent(mContext, SuperSocket.class);
        mContext.bindService(intent, sc, BIND_AUTO_CREATE);
    }


    public void unBindSocketService(){
        unbindService(sc);
    }

}
