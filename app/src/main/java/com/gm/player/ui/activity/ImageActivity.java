package com.gm.player.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gm.player.R;
import com.gm.player.application.GmApplication;
import com.gm.player.application.MySp;
import com.gm.player.base.BaseActivity;
import com.gm.player.db.DownLoadFiles;
import com.gm.player.event.NotificatFinishEvent;
import com.gm.player.event.PlayerVideoEvent;
import com.gm.player.util.GlideImageLoader;
import com.gm.player.util.MyDownLoader;
import com.gm.player.util.ToNextActivity;
import com.gm.player.util.YcToast;
import com.learning.downloader.FilesDownloadService;
import com.learning.downloader.callback.DownloadManager;
import com.learning.downloader.event.DownloadProgressEvent;
import com.learning.downloader.event.DownloadSuccessEvent;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.kind.im.common.InfoMessageEvent;

import static cn.com.kind.im.common.InfoMessageEvent.IMAGE;
import static com.gm.player.event.PlayerVideoEvent.PAUSE;
import static com.gm.player.event.PlayerVideoEvent.RESET;

public class ImageActivity extends BaseActivity {
    @BindView(R.id.banner)
    ImageView mBanner;
    @BindView(R.id.tvSize)
    TextView tvSize;
    @BindView(R.id.line_loading)
    LinearLayout lineLoading;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    private int defaultTime = 5;

    private int defaultRateTime=18*1000;
    private Handler myHandler;
    private  ObjectAnimator icon_anim;
    private int defaultFlag = 0;
    private int defaultType=0;
    private String img = "";
    private int defaultSize=1;
    private String isDefault="1";
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_image;
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
        Intent intent = getIntent();
        myHandler=new Handler();
        if (intent != null) {
            img= intent.getStringExtra("path");
            defaultTime = intent.getIntExtra("time", defaultTime);
            defaultFlag = intent.getIntExtra("flag", defaultFlag);
            defaultType=intent.getIntExtra("type", defaultType);
            if (defaultFlag == 0) {
                mBanner.setVisibility(View.VISIBLE);
                setBean();
            } else if(defaultFlag == 1){
                EventBus.getDefault().post(new PlayerVideoEvent(PAUSE));
                lineLoading.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initAnimation() {
        icon_anim = ObjectAnimator.ofFloat(ivLogo, "rotationY", 0.0F, 359.0F);
        icon_anim.setDuration(2000);
        icon_anim.setInterpolator(new DecelerateInterpolator()); //设置匀速旋转，不卡顿
        icon_anim.start();
        myHandler.postDelayed(new RunHandlerTime(),defaultRateTime);
    }




    private class RunHandlerTime implements Runnable{
        @Override
        public void run() {
            icon_anim.start();
            myHandler.postDelayed(new RunHandlerTime(),defaultRateTime);
        }
    }

    private void setBean(){
        Glide.with(this).load(img).animate(R.anim.fade_in).into(mBanner);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InfoMessageEvent event) {
        if (event.type.equals(IMAGE)) {
            if (event.message.equals("finish")) {
                finish();
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageFinishEvent(NotificatFinishEvent event) {
        Log.d(TAG, "onMessageFinishEvent: -------结束浏览");
        mBanner.destroyDrawingCache();
        if(event==null){
            ToNextActivity.ToFinishAcy((Activity)mContext,defaultType);
        }else{
            ToNextActivity._ToNext(event,mContext);
            ToNextActivity.ToFinishAcy((Activity)mContext,defaultType);
        }
        //ToAnimActivity(defaultType);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private  void ToAnimActivity(int type){
        finish();
        switch (type) {
            case 0:
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case 1:
                overridePendingTransition(R.anim.alpha_rotate,
                        R.anim.my_alpha_action);
                break;
            case 2:
                overridePendingTransition(R.anim.alpha_scale_rotate,
                        R.anim.my_alpha_action);
                break;
            case 3:
                overridePendingTransition(
                        R.anim.alpha_scale_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 4:
                overridePendingTransition(R.anim.alpha_scale_translate,
                        R.anim.my_alpha_action);
                break;
            case 5:
                overridePendingTransition(R.anim.alpha_scale,
                        R.anim.my_alpha_action);
                break;
            case 6:
                overridePendingTransition(R.anim.alpha_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 7:
                overridePendingTransition(R.anim.alpha_translate,
                        R.anim.my_alpha_action);
                break;
            case 8:
                overridePendingTransition(R.anim.my_rotate_action,
                        R.anim.my_alpha_action);
                break;
            case 9:
                overridePendingTransition(R.anim.my_scale_action,
                        R.anim.my_alpha_action);
                break;
            case 10:
                overridePendingTransition(R.anim.my_translate_action,
                        R.anim.my_alpha_action);
                break;
            case 11:
                overridePendingTransition(R.anim.myanimation_simple,
                        R.anim.my_alpha_action);
                break;
            case 12:
                overridePendingTransition(R.anim.myown_design,
                        R.anim.my_alpha_action);
                break;
            case 13:
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            case 14:
                overridePendingTransition(R.anim.scale_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 15:
                overridePendingTransition(R.anim.scale_translate,
                        R.anim.my_alpha_action);
                break;
            case 16:
                overridePendingTransition(R.anim.translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 17:
                overridePendingTransition(R.anim.hyperspace_in,
                        R.anim.hyperspace_out);
                break;
            case 18:
                overridePendingTransition(R.anim.shake,
                        R.anim.my_alpha_action);
                break;
            case 19:
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case 20:
                overridePendingTransition(R.anim.push_up_in,
                        R.anim.push_up_out);
                break;
            case 21:
                overridePendingTransition(R.anim.slide_left,
                        R.anim.slide_right);
                break;
            case 22:
                overridePendingTransition(R.anim.slide_top_to_bottom,
                        R.anim.my_alpha_action);
                break;
            case 23:
                overridePendingTransition(R.anim.wave_scale,
                        R.anim.my_alpha_action);
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageFinishEvent(DownloadSuccessEvent event) {
        if (event.type.equals("0")) {
            lineLoading.setVisibility(View.GONE);
            EventBus.getDefault().post(new PlayerVideoEvent(RESET));
            setBean();
            mBanner.setVisibility(View.VISIBLE);
            DownLoadFiles df = DownLoadFiles.queryFilesBy("fpath", img);
            DownLoadFiles.updateBykey("status",2,df.getFileId());
        } else {
            lineLoading.setVisibility(View.GONE);
            YcToast.get().toast("图片下载失败,任务继续");
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageProgressEvent(DownloadProgressEvent event) {
            int percent = (int) (event.progress * 100.0 / event.size);
            tvSize.setText("正在下载" + percent + "% ");
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
