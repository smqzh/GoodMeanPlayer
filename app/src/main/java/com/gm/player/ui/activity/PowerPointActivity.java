package com.gm.player.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gm.player.R;
import com.gm.player.application.GmApplication;
import com.gm.player.base.BaseActivity;
import com.gm.player.db.DownLoadFiles;
import com.gm.player.event.NotificatFinishEvent;
import com.gm.player.event.PlayerVideoEvent;
import com.gm.player.util.ThreadPoolManager;
import com.gm.player.util.ToNextActivity;
import com.gm.player.util.YcToast;
import com.learning.downloader.event.DownloadProgressEvent;
import com.learning.downloader.event.DownloadSuccessEvent;
import com.olivephone.office.TempFileManager;
import com.olivephone.office.powerpoint.DocumentSession;
import com.olivephone.office.powerpoint.DocumentSessionBuilder;
import com.olivephone.office.powerpoint.DocumentSessionStatusListener;
import com.olivephone.office.powerpoint.IMessageProvider;
import com.olivephone.office.powerpoint.ISystemColorProvider;
import com.olivephone.office.powerpoint.android.AndroidMessageProvider;
import com.olivephone.office.powerpoint.android.AndroidSystemColorProvider;
import com.olivephone.office.powerpoint.android.AndroidTempFileStorageProvider;
import com.olivephone.office.powerpoint.view.PersentationView;
import com.olivephone.office.powerpoint.view.SlideShowNavigator;
import com.olivephone.office.powerpoint.view.SlideView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import cn.com.kind.im.common.InfoMessageEvent;

import static cn.com.kind.im.common.InfoMessageEvent.VIDEO;
import static com.gm.player.event.PlayerVideoEvent.PAUSE;
import static com.gm.player.event.PlayerVideoEvent.RESET;

public class PowerPointActivity extends BaseActivity implements
        DocumentSessionStatusListener {
    @BindView(R.id.scale)
    SeekBar scale;
    @BindView(R.id.control_box)
    LinearLayout controlBox;
    @BindView(R.id.content)
    PersentationView content;

    @BindView(R.id.tvSize)
    TextView tvSize;
    @BindView(R.id.line_loading)
    LinearLayout lineLoading;
    //	private PersentationView content;
    private DocumentSession session;
    private SlideShowNavigator navitator;

    private int currentSlideNumber;

    private String path;
    private int defaultFlag = 0;
    private int defaultType=0;
    private int defaultSize=1;
    @Override
    protected int attachLayoutRes() {
        return R.layout.powerpoint_main;
    }

    @Override
    protected void initViews() {
        mToolBarSet.hide();
        if(GmApplication.getInstance().isScreenFlag()){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        EventBus.getDefault().register(this);
        this.scale
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {

                        if (progress < 1) {
                            progress = 1;
                        }
                        Log.d(TAG, "onProgressChanged: -------"+progress);
                        PowerPointActivity.this.content
                                .notifyScale(progress / 250.0);
                    }
                });

    }

    @Override
    protected void initData() {
       /* PowerPointActivity.this.content
                .notifyScale(72 / 250.0);*/
        Intent intent=getIntent();
        if(intent!=null){
            path = getIntent().getStringExtra("path");
            defaultFlag = intent.getIntExtra("flag", defaultFlag);
            defaultType = intent.getIntExtra("type", defaultType);
            defaultSize=intent.getIntExtra("size", defaultSize);
            if(defaultFlag==0){
                if(defaultSize==1){
                    EventBus.getDefault().post(new PlayerVideoEvent(PAUSE));
                }
                openFile(path);
                lineLoading.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
            }else if(defaultFlag==1){
                EventBus.getDefault().post(new PlayerVideoEvent(PAUSE));
                lineLoading.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.content.setContentView(null);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageFinishEvent(NotificatFinishEvent event) {
        if(event==null){
            ToNextActivity.ToFinishAcy((Activity)mContext,defaultType);
        }else{
            ToNextActivity._ToNext(event,mContext);
            ToNextActivity.ToFinishAcy((Activity)mContext,defaultType);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onDestroy() {
        if (this.session != null) {
            this.session.endSession();
        }
        super.onDestroy();
    }


    private void openFile(String filePath){
        try {
            Context context = PowerPointActivity.this.getApplicationContext();
            IMessageProvider msgProvider = new AndroidMessageProvider(context);
            TempFileManager tmpFileManager = new TempFileManager(
                    new AndroidTempFileStorageProvider(context));
            ISystemColorProvider sysColorProvider = new AndroidSystemColorProvider();

            session = new DocumentSessionBuilder(new File(filePath))
                    .setMessageProvider(msgProvider)
                    .setTempFileManager(tmpFileManager)
                    .setSystemColorProvider(sysColorProvider)
                    .setSessionStatusListener(this).build();
            session.startSession();
            PowerPointActivity.this.content
                    .notifyScale(72 / 250.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Toast.makeText(this,
        // "(" + event.getRawX() + "," + event.getRawY() + ")",
        // Toast.LENGTH_SHORT).show();
        return super.onTouchEvent(event);
    }

    @Override
    public void onSessionStarted() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               /* Toast.makeText(PowerPointActivity.this, "onSessionStarted",
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    @Override
    public void onDocumentReady() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               /* Toast.makeText(PowerPointActivity.this, "onDocumentReady",
                        Toast.LENGTH_SHORT).show();*/
                PowerPointActivity.this.navitator = new SlideShowNavigator(
                        PowerPointActivity.this.session.getPPTContext());
                PowerPointActivity.this.currentSlideNumber = PowerPointActivity.this.navitator
                        .getFirstSlideNumber() - 1;
                PowerPointActivity.this.next();
            }
        });
    }

    @Override
    public void onDocumentException(Exception e) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PowerPointActivity.this, "onDocumentException",
                        Toast.LENGTH_SHORT).show();
                PowerPointActivity.this.finish();
            }
        });
    }

    @Override
    public void onSessionEnded() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PowerPointActivity.this, "onSessionEnded",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateTo(int slideNumber) {
        SlideView slideShow = this.navitator.navigateToSlide(
                this.content.getGraphicsContext(), slideNumber);
        this.content.setContentView(slideShow);
    }

    private void next() {
        if (this.navitator != null) {
            if (this.navitator.getFirstSlideNumber()
                    + this.navitator.getSlideCount() - 1 > this.currentSlideNumber) {
                this.navigateTo(++this.currentSlideNumber);
            } else {
                Toast.makeText(this, "Next page", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void prev() {
        if (this.navitator != null) {
            if (this.navitator.getFirstSlideNumber() < this.currentSlideNumber) {
                this.navigateTo(--this.currentSlideNumber);
            } else {
                Toast.makeText(this, "Pre page", Toast.LENGTH_SHORT).show();
            }
        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageFinishEvent(DownloadSuccessEvent event) {
        if (event.type.equals("0")) {
            lineLoading.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new PlayerVideoEvent(RESET));
            openFile(path);
            DownLoadFiles df = DownLoadFiles.queryFilesBy("fpath", path);
            DownLoadFiles.updateBykey("status",2,df.getFileId());
        } else {
            lineLoading.setVisibility(View.GONE);
            YcToast.get().toast("PPT下载失败,任务继续");
            finish();
            EventBus.getDefault().post(new PlayerVideoEvent(RESET));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadProgressEvent event) {
        int percent=(int)(event.progress*100.0/event.size);
        tvSize.setText("正在下载"+percent+"% ");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InfoMessageEvent event) {
        if(event.type.equals(VIDEO)){
            if (event.message.equals("volAdd")) {
                next();
            }
            else if (event.message.equals("volDes")) {
                prev();
            }
        }
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
