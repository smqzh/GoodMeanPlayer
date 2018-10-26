package com.gm.player.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.gm.player.R;
import com.gm.player.application.MySp;
import com.gm.player.base.BaseActivity;
import com.gm.player.service.SuperSocket;
import com.gm.player.widget.ExplosionAnimator;
import com.gm.player.widget.ExplosionField;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.seekBar)
    DiscreteSeekBar seekBar;
    @BindView(R.id.tvPercent)
    TextView tvPercent;

    private int defaultTime = 200;
    private int percent = 0;
    private Handler handler;
    private ExplosionField mExplosionField;//粉碎动画执行方法类
    private runWaveLoading runWaveLoading;
     private Context mCtx;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_welcome);
        mCtx=this;
        ButterKnife.bind(this);
        getWindow().getDecorView().setRotation(MySp.getRotation());
        initData();

    }


    protected void initData() {
        handler = new Handler();
        runWaveLoading=new runWaveLoading();
        mExplosionField = ExplosionField.attach2Window(WelcomeActivity.this);
        animTion();
        handler.postDelayed(runWaveLoading, defaultTime);
        Intent intent = new Intent(this, SuperSocket.class);
        startService(intent);
    }

    private void animTion() {
        Animation t1 = new AlphaAnimation(0.4f, 1f);
        t1.setDuration(5000);
        t1.setFillAfter(true);
        ivLogo.setAnimation(t1);
        ivLogo.setVisibility(View.VISIBLE);
    }


    private class runWaveLoading implements Runnable {
        @Override
        public void run() {
            percent=percent+(int)(Math.random()*10);
            if (percent <100) {
                tvPercent.setText("正在加载"+percent+"%...");
                seekBar.setProgress(percent);
            }
            if (percent >= 100) {
                tvPercent.setText("加载完成");
                seekBar.setProgress(100);
                handler.removeCallbacks(runWaveLoading);
                handler=null;
                //执行粉碎动画
                mExplosionField.explode(seekBar);
                seekBar.postDelayed(new Runnable() {
                    public void run() {
                       //  seekBar.setScaleX(2);
                       //   seekBar.setScaleY(2);
                        //  seekBar.setAlpha(1f);
                          seekBar.setVisibility(View.GONE);
                        Intent intent = new Intent(WelcomeActivity.this, IndexActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, ExplosionAnimator.DEFAULT_DURATION);
            }
            if(handler!=null) {
                handler.postDelayed(runWaveLoading, defaultTime);
            }
        }
    }

}
