package com.gm.player.ui.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.gm.player.R;
import com.gm.player.application.GmApplication;
import com.gm.player.application.MySp;
import com.gm.player.base.BaseActivity;
import com.gm.player.event.NotificatFinishEvent;
import com.gm.player.event.PlayerVideoEvent;
import com.gm.player.util.ToNextActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

import static com.gm.player.event.PlayerVideoEvent.PAUSE;

public class WebActivity extends BaseActivity {

    @BindView(R.id.wv_web)
    WebView wv;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    private int usize;
    private String[] urls=null;
    private int index=0;
    private int defaultTime=10;
    private int defaultType=0;
    private int defaultRateTime=18*1000;
    private ObjectAnimator icon_anim;
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_web;
    }

    @Override
    protected void initViews() {
        mToolBarSet.hide();
        EventBus.getDefault().register(this);
        if(GmApplication.getInstance().isScreenFlag()){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        getWindow().getDecorView().setRotation(MySp.getRotation());
    }

    @Override
    protected void initData() {
        Intent intent=  getIntent();
        if(intent!=null){
            String url=intent.getStringExtra("path");
            Log.d(TAG, "initData: ----------"+url);
            if(url.length()>=8) {
                String sb = url.substring(0, 7).toLowerCase();
                String sp = url.substring(0, 8).toLowerCase();
                if (!sb.equals("http://")) {
                   if(!sp.equals("https://")){
                       url = "http://" + url;
                   }
                }
            }
            defaultTime=intent.getIntExtra("time",defaultTime);
            defaultType=intent.getIntExtra("type", defaultType);

            wv.setWebViewClient(new WebViewClient());
            WebSettings webSettings = wv.getSettings();
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            webSettings.setDomStorageEnabled(true);
            webSettings.setDatabaseEnabled(true);
            webSettings.setPluginState(WebSettings.PluginState.ON);
//        webSettings.setUseWideViewPort(true); // 关键点
            webSettings.setAllowFileAccess(true); // 允许访问文件
          webSettings.setSupportZoom(true); // 支持缩放
//        webSettings.setLoadWithOverviewMode(true);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 加载缓存内容
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
//允许JS互调。可以使用网页上的功能代码了
            wv.getSettings().setJavaScriptEnabled(true);
            wv.loadUrl(url);
        }
    }

    private void initAnimation() {
         icon_anim = ObjectAnimator.ofFloat(ivLogo, "rotationY", 0.0F, 359.0F);

        icon_anim.setDuration(2000);
        icon_anim.setInterpolator(new DecelerateInterpolator()); //设置匀速旋转，不卡顿
        icon_anim.start();
      //  mHandler.postDelayed(new RunHandlerTime(),defaultRateTime);
    }


    private class RunHandlerTime implements Runnable{
        @Override
        public void run() {
            icon_anim.start();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NotificatFinishEvent event){
        wv.destroy();
        if(event==null){
            ToNextActivity.ToFinishAcy((Activity)mContext,defaultType);
        }else{
            ToNextActivity._ToNext(event,mContext);
            ToNextActivity.ToFinishAcy((Activity)mContext,defaultType);
        }
    }

    private void BackHome(){

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    /**
     * 返回按钮的监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            GmApplication.getInstance().getActivityManager().finishAllActivityExceptOne(IndexActivity.class);
            EventBus.getDefault().post(new PlayerVideoEvent(PAUSE));
        }
        return super.onKeyUp(keyCode, event);
    }
}
