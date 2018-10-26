package com.gm.player.ui.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gm.player.R;

import io.vov.vitamio.utils.StringUtils;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by 鹤 on 2015/10/29.
 */
public class MyMediaController extends MediaController {

    private static final int HIDEFRAM = 0;
    private static final int SHOW_PROGRESS = 2;

    private GestureDetector mGestureDetector;
    private ImageButton img_back;//返回键
    private TextView textViewTime;//时间提示
    private VideoView videoView;
    private Activity activity;
    private Context context;
    private int controllerWidth = 0;//设置mediaController高度为了使横屏时top显示在屏幕顶端

    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private TextView mOperationTv;
    private AudioManager mAudioManager;

    private ImageView ivLogo;
    private SeekBar seekBarProgress;
    private boolean progress_turn;
    private int progress;

    private boolean mDragging;
    private MediaPlayerControl player;
    //最大声音
    private int mMaxVolume;
    // 当前声音
    private int mVolume = -1;
    //当前亮度
    private float mBrightness = -1f;

    private int defaultTime=20*1000;
    private  ObjectAnimator icon_anim;

    //返回监听
    private OnClickListener backListener = new OnClickListener() {
        public void onClick(View v) {
            if(activity != null){
                activity.finish();
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    };


    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case HIDEFRAM:
                    mVolumeBrightnessLayout.setVisibility(View.GONE);
                    mOperationTv.setVisibility(View.GONE);
                    break;
            }
        }
    };


    //videoview 用于对视频进行控制的等，activity为了退出
    public MyMediaController(Context context, VideoView videoView , Activity activity) {
        super(context);
        this.context = context;
        this.videoView = videoView;
        this.activity = activity;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        controllerWidth = wm.getDefaultDisplay().getWidth();
        mGestureDetector = new GestureDetector(context, new MyGestureListener());
    }

    @Override
    protected View makeControllerView() {
        View v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(getResources().getIdentifier("layout_mediaer", "layout", getContext().getPackageName()), this);
        v.setMinimumHeight(controllerWidth);
        //TOP

        img_back = (ImageButton) v.findViewById(R.id.mediacontroller_top_back);
        img_back.setOnClickListener(backListener);
        textViewTime = (TextView)v.findViewById(R.id.mediacontroller_time);
        seekBarProgress = (SeekBar)v.findViewById(R.id.mediacontroller_seekbar);
        ivLogo=(ImageView)v.findViewById(R.id.iv_logo);
        //mid
        mVolumeBrightnessLayout = (RelativeLayout)v.findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView)v.findViewById(R.id.operation_bg);
        mOperationTv = (TextView) v.findViewById(R.id.operation_tv);
        mOperationTv.setVisibility(View.GONE);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
      //  Log.d(TAG, "makeControllerView: -----"+mMaxVolume);
        show();
        icon_anim = ObjectAnimator.ofFloat(ivLogo, "rotationY", 0.0F, 359.0F);
        icon_anim.setDuration(2000);
        icon_anim.setInterpolator(new DecelerateInterpolator()); //设置匀速旋转，不卡顿
//        myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                toggleMediaControlsVisiblity();
//            }
//        },2000);
        // initAnimation();
        return v;
    }

    private void initAnimation() {
        icon_anim.start();
        myHandler.postDelayed(new RunHandlerTime(),defaultTime);
    }

    private class RunHandlerTime implements Runnable{
        @Override
        public void run() {
            icon_anim.start();
            myHandler.postDelayed(new RunHandlerTime(),defaultTime);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        System.out.println("MYApp-MyMediaController-dispatchKeyEvent");
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) return true;
        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                if (progress_turn) {
                    onFinishSeekBar();
                    progress_turn = false;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;
        // 隐藏
        myHandler.removeMessages(HIDEFRAM);
        myHandler.sendEmptyMessageDelayed(HIDEFRAM, 1);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //当收拾结束，并且是单击结束时，控制器隐藏/显示
            toggleMediaControlsVisiblity();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            progress = getProgress();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float beginX = e1.getX();
            float endX = e2.getX();
            float beginY = e1.getY();
            float endY = e2.getY();

            Display disp = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            disp.getSize(size);
            int windowWidth = size.x;
            int windowHeight = size.y;
            if (Math.abs(endX - beginX) < Math.abs(beginY - endY)) {//上下滑动
                if (beginX > windowWidth * 3.0 / 4.0) {// 右边滑动 屏幕3/5
                    onVolumeSlide((beginY - endY) / windowHeight);
                } else if (beginX < windowWidth * 1.0 / 4.0) {// 左边滑动 屏幕2/5
                    onBrightnessSlide((beginY - endY) / windowHeight);
                }
            }else {
                onSeekTo((endX - beginX) / 20);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
        //双击暂停或开始
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            playOrPause();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    /**
     * 滑动改变播放进度
     *
     * @param percent
     */
    private void onSeekTo(float percent) {
        //计算并显示 前进后退
        if (!progress_turn) {
            onStartSeekBar();
            progress_turn = true;
        }
        int change = (int) (percent);
        if (change > 0) {
            mOperationBg.setImageResource(R.drawable.right);
        } else {
            mOperationBg.setImageResource(R.drawable.left);
        }
        mOperationTv.setVisibility(View.VISIBLE);

        mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        if (progress + change > 0) {
            if ((progress + change < 1000))
                mOperationTv.setText(setSeekBarChange(progress + change) + "/" + StringUtils.generateTime(videoView.getDuration()));
            else
                mOperationTv.setText(setSeekBarChange(1000) + "/" + StringUtils.generateTime(videoView.getDuration()));
        } else {
            mOperationTv.setText(setSeekBarChange(0) + "/" + StringUtils.generateTime(videoView.getDuration()));

        }
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // 显示
//            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
            mOperationTv.setVisibility(VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;
        if (index >= 10) {
            mOperationBg.setImageResource(R.drawable.volmn_100);
        } else if (index >= 5 && index < 10) {
            mOperationBg.setImageResource(R.drawable.volmn_60);
        } else if (index > 0 && index < 5) {
            mOperationBg.setImageResource(R.drawable.volmn_30);
        } else {
            mOperationBg.setImageResource(R.drawable.volmn_no);
        }
        //DecimalFormat    df   = new DecimalFormat("######0.00");
        mOperationTv.setText((int) (((double) index / mMaxVolume)*100)+"%");
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    }



    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = activity.getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            //mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
            mOperationTv.setVisibility(VISIBLE);

        }



        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        activity.getWindow().setAttributes(lpa);

        mOperationTv.setText((int) (lpa.screenBrightness * 100) + "%");
        if (lpa.screenBrightness * 100 >= 90) {
            mOperationBg.setImageResource(R.drawable.light_100);
        } else if (lpa.screenBrightness * 100 >= 80 && lpa.screenBrightness * 100 < 90) {
            mOperationBg.setImageResource(R.drawable.light_90);
        } else if (lpa.screenBrightness * 100 >= 70 && lpa.screenBrightness * 100 < 80) {
            mOperationBg.setImageResource(R.drawable.light_80);
        } else if (lpa.screenBrightness * 100 >= 60 && lpa.screenBrightness * 100 < 70) {
            mOperationBg.setImageResource(R.drawable.light_70);
        } else if (lpa.screenBrightness * 100 >= 50 && lpa.screenBrightness * 100 < 60) {
            mOperationBg.setImageResource(R.drawable.light_60);
        } else if (lpa.screenBrightness * 100 >= 40 && lpa.screenBrightness * 100 < 50) {
            mOperationBg.setImageResource(R.drawable.light_50);
        } else if (lpa.screenBrightness * 100 >= 30 && lpa.screenBrightness * 100 < 40) {
            mOperationBg.setImageResource(R.drawable.light_40);
        } else if (lpa.screenBrightness * 100 >= 20 && lpa.screenBrightness * 100 < 20) {
            mOperationBg.setImageResource(R.drawable.light_30);
        } else if (lpa.screenBrightness * 100 >= 10 && lpa.screenBrightness * 100 < 20) {
            mOperationBg.setImageResource(R.drawable.light_20);
        }


    }
/**
 * *********************************************************Pad控制*******************************
* */

    public void  nextAdd(){
        onSeekTo(50);
    }

    public void  backDes(){
        onSeekTo(-50);
    }

     int volCount=2;
    public void  volAdd(){

        if(volCount<=10) {
            volCount++;
            int index=(int)((0.1*volCount)*mMaxVolume);
            initVol(index);
        }
    }

    public void  volDes(){
        if(volCount>=1) {
            volCount--;
            int index=(int)((0.1*volCount)*mMaxVolume);
            initVol(index);
        }
    }
    public void  NoVol(){
            int index=(int)(0*mMaxVolume);
            initVol(index);
    }
    public void  AutoVol(){
        int index=(int)((0.1*volCount)*mMaxVolume);
        initVol(index);
    }

    private void initVol(int index){
        mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        mOperationTv.setVisibility(VISIBLE);
        if (index >= 10) {
            mOperationBg.setImageResource(R.drawable.volmn_100);
        } else if (index >= 5 && index < 10) {
            mOperationBg.setImageResource(R.drawable.volmn_60);
        } else if (index > 0 && index < 5) {
            mOperationBg.setImageResource(R.drawable.volmn_30);
        } else {
            mOperationBg.setImageResource(R.drawable.volmn_no);
        }
        //DecimalFormat    df   = new DecimalFormat("######0.00");
        mOperationTv.setText((int) (((double) index / mMaxVolume)*100)+"%");
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    }

    int lightBright=5;
    public void  lightAdd(){
        if(lightBright<=10) {
            lightBright++;
            initLight(0.1f*lightBright);
        }
    }

    public void  lightDes(){
        if(lightBright>=1){
            lightBright--;
            initLight(0.1f*lightBright);
        }
    }

    private void initLight(float index){

        mBrightness = activity.getWindow().getAttributes().screenBrightness;
        if (mBrightness <= 0.00f)
            mBrightness = 0.50f;
        if (mBrightness < 0.01f)
            mBrightness = 0.01f;

        // 显示
        //mOperationBg.setImageResource(R.drawable.video_brightness_bg);
        mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        mOperationTv.setVisibility(VISIBLE);


        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = index;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        activity.getWindow().setAttributes(lpa);

        mOperationTv.setText((int) (lpa.screenBrightness * 100) + "%");
        if (lpa.screenBrightness * 100 >= 90) {
            mOperationBg.setImageResource(R.drawable.light_100);
        } else if (lpa.screenBrightness * 100 >= 80 && lpa.screenBrightness * 100 < 90) {
            mOperationBg.setImageResource(R.drawable.light_90);
        } else if (lpa.screenBrightness * 100 >= 70 && lpa.screenBrightness * 100 < 80) {
            mOperationBg.setImageResource(R.drawable.light_80);
        } else if (lpa.screenBrightness * 100 >= 60 && lpa.screenBrightness * 100 < 70) {
            mOperationBg.setImageResource(R.drawable.light_70);
        } else if (lpa.screenBrightness * 100 >= 50 && lpa.screenBrightness * 100 < 60) {
            mOperationBg.setImageResource(R.drawable.light_60);
        } else if (lpa.screenBrightness * 100 >= 40 && lpa.screenBrightness * 100 < 50) {
            mOperationBg.setImageResource(R.drawable.light_50);
        } else if (lpa.screenBrightness * 100 >= 30 && lpa.screenBrightness * 100 < 40) {
            mOperationBg.setImageResource(R.drawable.light_40);
        } else if (lpa.screenBrightness * 100 >= 20 && lpa.screenBrightness * 100 < 20) {
            mOperationBg.setImageResource(R.drawable.light_30);
        } else if (lpa.screenBrightness * 100 >= 10 && lpa.screenBrightness * 100 < 20) {
            mOperationBg.setImageResource(R.drawable.light_20);
        }
    }


    public void setTime(String time){
        if (textViewTime != null)
            textViewTime.setText(time);
    }

    //隐藏/显示
    private void toggleMediaControlsVisiblity(){
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }
    //播放与暂停
    private void playOrPause(){
        if (videoView != null)
            if (videoView.isPlaying()) {
                videoView.pause();
            } else {
                videoView.start();
            }
    }



}
