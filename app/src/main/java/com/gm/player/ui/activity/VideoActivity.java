package com.gm.player.ui.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gm.player.R;
import com.gm.player.application.GmApplication;
import com.gm.player.application.MySp;
import com.gm.player.base.BaseActivity;
import com.gm.player.bean.ScreenBean;
import com.gm.player.db.DownLoadFiles;
import com.gm.player.event.NotificatFinishEvent;
import com.gm.player.event.PlayerVideoEvent;
import com.gm.player.util.FileUtil;
import com.gm.player.util.ToNextActivity;
import com.gm.player.util.YcToast;
import com.learning.downloader.FilesDownloadService;
import com.learning.downloader.callback.DownloadManager;
import com.learning.downloader.event.DownloadProgressEvent;
import com.learning.downloader.event.DownloadSuccessEvent;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.kind.im.common.InfoMessageEvent;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

import static com.gm.player.event.PlayerVideoEvent.PAUSE;
import static com.gm.player.event.PlayerVideoEvent.RESET;
import static com.gm.player.event.PlayerVideoEvent.RESETVIDEO;
import static com.gm.player.event.PlayerVideoEvent.START;

public class VideoActivity extends BaseActivity {

    //    @BindView(R.id.iv_logo)
//    ImageView ivLogo;
    @BindView(R.id.avl_loading)
    AVLoadingIndicatorView avlLoading;
    @BindView(R.id.tvSize)
    TextView tvSize;
    @BindView(R.id.line_loading)
    LinearLayout lineLoading;
    private String TAG = this.getClass().getSimpleName();
    @BindView(R.id.videoplayer)
    VideoView mVideoView;
    private String path = "";
    private Context mContext;
    private String[] video = null;
    private int vsize = 0;
    private int index = 0;
    private int defaultFlag = 0;
    private MyMediaController myMediaController;
    private int defaultType=0;
    private int defaultSize=1;
    private String isDefault="1";
    private int defTime;
    @Override
    protected int attachLayoutRes() {

        if(MySp.getVideoNormal()==0) {
            return R.layout.activity_video;
        }else{
            return R.layout.activity_videonormal;
        }
    }

    @Override
    protected void initViews() {
        mContext = this;
        mToolBarSet.hide();
        getWindow().getDecorView().setRotation(MySp.getRotation());
        EventBus.getDefault().register(this);
        if(GmApplication.getInstance().isScreenFlag()){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        myMediaController = new MyMediaController(this, mVideoView, this);
        myMediaController.show(5000);
        mVideoView.setMediaController(myMediaController);
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);//低画质
        mVideoView.setBufferSize(1024*1024*2);
        mVideoView.requestFocus();
        if (intent != null) {
            path = intent.getStringExtra("path");
            defaultFlag = intent.getIntExtra("flag", defaultFlag);
            defaultType=intent.getIntExtra("type", defaultType);
            isDefault = intent.getStringExtra("isDefault");
            defaultSize=intent.getIntExtra("size", defaultSize);
            defTime=intent.getIntExtra("time", defTime);
            video = path.split(";");
            vsize = video.length;
            if (defaultFlag == 0) {
                if(defaultSize==1||defTime==0) {
                    EventBus.getDefault().post(new PlayerVideoEvent(PAUSE));
                }
                File file = new File(path);
                if (file.exists()) {
                    if (file.getTotalSpace() > 0) {
                        setPath(file);
                    }
                } else {
                    YcToast.get().toast("该文件不存在");
                    finish();
                }
            } else if (defaultFlag == 1) {
                EventBus.getDefault().post(new PlayerVideoEvent(PAUSE));
                lineLoading.setVisibility(View.VISIBLE);
            }
        }
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion: ------播放结束");
                if(defaultSize==1) {
                    if(isDefault.equals("0")) {
                        File file = new File(path);
                        if (file.exists()) {
                            setPath(file);
                            mp.seekTo(0);
                            mp.start();
                        } else {
                            YcToast.get().toast("该文件不存在");
                            finish();
                        }
                    }else{
                        finish();
                    }
                }else{
                    if(defTime==0) {
                        EventBus.getDefault().post(new PlayerVideoEvent(START));
                    }
                }

            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InfoMessageEvent event) {
        Log.d(TAG, "onMessageEvent: ------VideoActivity");
        if (event.type.equals(InfoMessageEvent.VIDEO)) {
            if (event.message.equals("pause")) {
                mVideoView.pause();
            } else if (event.message.equals("restart")) {
                mVideoView.reset();
            }  else if (event.message.equals("start")) {
                mVideoView.start();
            } else if (event.message.equals("nextAdd")) {
                myMediaController.nextAdd();
            } else if (event.message.equals("nextBack")) {
                myMediaController.backDes();
            }
            else if (event.message.equals("volAdd")) {
                myMediaController.volAdd();
            }
            else if (event.message.equals("volDes")) {
                myMediaController.volDes();
            }
            else if (event.message.equals("lightAdd")) {
                myMediaController.lightAdd();
            }
            else if (event.message.equals("lightDes")) {
                myMediaController.lightDes();
            }
            else if (event.message.equals("noVol")) {
                myMediaController.NoVol();
            }
            else if (event.message.equals("autoVol")) {
                myMediaController.AutoVol();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageFinishEvent(NotificatFinishEvent event) {
        mVideoView.stopPlayback();
        mVideoView.destroyDrawingCache();
        myMediaController.destroyDrawingCache();
        mVideoView=null;
        Log.d(TAG, "视频即将结束，销毁------1");
        if(event==null){
            Log.d(TAG, "视频即将结束，销毁------2");
            ToNextActivity.ToFinishAcy((Activity)mContext,defaultType);
        }else{
            Log.d(TAG, "视频即将结束，销毁------3");
            ToNextActivity._ToNext(event,mContext);
            ToNextActivity.ToFinishAcy((Activity)mContext,defaultType);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageFinishEvent(DownloadSuccessEvent event) {
        if (event.type.equals("0")) {
            lineLoading.setVisibility(View.GONE);
            EventBus.getDefault().post(new PlayerVideoEvent(RESET));
            File file = new File(path);
            setPath(file);
            DownLoadFiles df = DownLoadFiles.queryFilesBy("fpath", path);
            DownLoadFiles.updateBykey("status",2,df.getFileId());
        } else {
            lineLoading.setVisibility(View.GONE);
            YcToast.get().toast("视频下载失败,任务继续");
            finish();
            EventBus.getDefault().post(new PlayerVideoEvent(RESET));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadProgressEvent event) {
        int percent=(int)(event.progress*100.0/event.size);
        tvSize.setText("正在下载"+percent+"% ");
    }

    private void setPath(File file) {
        String path = file.getAbsolutePath();
        Log.d(TAG, "setPath: -----------" + path);
        mVideoView.setVideoPath(path);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mVideoView!=null) {
            mVideoView.stopPlayback();
            mVideoView.destroyDrawingCache();
        }
    }

  /*  @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("haha", "切换横屏");
            setFullScreen();
        }
    }

    private void setFullScreen() {
        Log.d("haha", "调用设置全屏");
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mVideoView.setLayoutParams(layoutParams1);
    }
*/

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
