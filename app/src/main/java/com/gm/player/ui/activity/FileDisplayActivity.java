package com.gm.player.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.gm.player.R;
import com.gm.player.application.GmApplication;
import com.gm.player.application.MySp;
import com.gm.player.base.BaseActivity;
import com.gm.player.db.DownLoadFiles;
import com.gm.player.event.NotificatFinishEvent;
import com.gm.player.event.PlayerVideoEvent;
import com.gm.player.util.ThreadPoolManager;
import com.gm.player.util.ToNextActivity;
import com.gm.player.util.YcToast;
import com.gm.player.widget.TbsWordView;
import com.learning.downloader.event.DownloadProgressEvent;
import com.learning.downloader.event.DownloadSuccessEvent;
import com.tencent.smtt.sdk.TbsReaderView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import cn.com.kind.im.common.InfoMessageEvent;

import static cn.com.kind.im.common.InfoMessageEvent.VIDEO;
import static com.gm.player.event.PlayerVideoEvent.PAUSE;
import static com.gm.player.event.PlayerVideoEvent.RESET;

public class FileDisplayActivity extends BaseActivity implements TbsReaderView.ReaderCallback {
    @BindView(R.id.tbsView)
    TbsWordView tbsView;
    @BindView(R.id.multiple_actions)
    FloatingActionsMenu multipleActions;
    @BindView(R.id.button_remove)
    FloatingActionButton buttonRemove;
    @BindView(R.id.tvSize)
    TextView tvSize;
    @BindView(R.id.line_loading)
    LinearLayout lineLoading;
    private String path;
    private int defaultFlag = 0;
    private int defaultType=0;
    private int defaultSize=1;

    private int width=0;
    private int height=0;

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_files;
    }

    @Override
    protected void initViews() {
        mToolBarSet.hide();
        EventBus.getDefault().register(this);
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleActions.toggle();
                Back();
            }
        });
        getWindow().getDecorView().setRotation(MySp.getRotation());
        if(GmApplication.getInstance().isScreenFlag()){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void initData() {

        Intent intent=getIntent();
        if(intent!=null){
            path = getIntent().getStringExtra("path");
            defaultFlag = intent.getIntExtra("flag", defaultFlag);
            defaultType = intent.getIntExtra("type", defaultType);
            defaultSize=intent.getIntExtra("size", defaultSize);
            if(defaultFlag==0){
                if(defaultSize==1)
                if (isFileExists()) {
                     displayFile();
                }

                lineLoading.setVisibility(View.GONE);
                tbsView.setVisibility(View.VISIBLE);
            }else if(defaultFlag==1){
                EventBus.getDefault().post(new PlayerVideoEvent(PAUSE));
                lineLoading.setVisibility(View.VISIBLE);
            }
        }
        //2、通过Resources获取
        DisplayMetrics dm = getResources().getDisplayMetrics();
        height = dm.heightPixels;
        width = dm.widthPixels;
        Log.d(TAG, "initData: -----"+height);
    }

    //显示Word
    private void displayFile() {
        tbsView.setOnGetFilePathListener(new TbsWordView.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(TbsWordView mSuperFileView2) {
                getFilePathAndShowFile(mSuperFileView2);
            }
        });
        tbsView.show();
    }

    private void getFilePathAndShowFile(TbsWordView mSuperFileView2) {
        mSuperFileView2.displayFile(new File(path));
    }

    private boolean isFileExists() {
        return new File(path).exists();
    }

    private void Back() {
        finish();
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageFinishEvent(NotificatFinishEvent event) {
        tbsView.onStopDisplay();
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
//
//    private  void ToAnimActivity(int type){
//        finish();
//        switch (type) {
//            case 0:
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                break;
//            case 1:
//                overridePendingTransition(R.anim.alpha_rotate,
//                        R.anim.my_alpha_action);
//                break;
//            case 2:
//                overridePendingTransition(R.anim.alpha_scale_rotate,
//                        R.anim.my_alpha_action);
//                break;
//            case 3:
//                overridePendingTransition(
//                        R.anim.alpha_scale_translate_rotate,
//                        R.anim.my_alpha_action);
//                break;
//            case 4:
//                overridePendingTransition(R.anim.alpha_scale_translate,
//                        R.anim.my_alpha_action);
//                break;
//            case 5:
//                overridePendingTransition(R.anim.alpha_scale,
//                        R.anim.my_alpha_action);
//                break;
//            case 6:
//                overridePendingTransition(R.anim.alpha_translate_rotate,
//                        R.anim.my_alpha_action);
//                break;
//            case 7:
//                overridePendingTransition(R.anim.alpha_translate,
//                        R.anim.my_alpha_action);
//                break;
//            case 8:
//                overridePendingTransition(R.anim.my_rotate_action,
//                        R.anim.my_alpha_action);
//                break;
//            case 9:
//                overridePendingTransition(R.anim.my_scale_action,
//                        R.anim.my_alpha_action);
//                break;
//            case 10:
//                overridePendingTransition(R.anim.my_translate_action,
//                        R.anim.my_alpha_action);
//                break;
//            case 11:
//                overridePendingTransition(R.anim.myanimation_simple,
//                        R.anim.my_alpha_action);
//                break;
//            case 12:
//                overridePendingTransition(R.anim.myown_design,
//                        R.anim.my_alpha_action);
//                break;
//            case 13:
//                overridePendingTransition(R.anim.scale_rotate,
//                        R.anim.my_alpha_action);
//                break;
//            case 14:
//                overridePendingTransition(R.anim.scale_translate_rotate,
//                        R.anim.my_alpha_action);
//                break;
//            case 15:
//                overridePendingTransition(R.anim.scale_translate,
//                        R.anim.my_alpha_action);
//                break;
//            case 16:
//                overridePendingTransition(R.anim.translate_rotate,
//                        R.anim.my_alpha_action);
//                break;
//            case 17:
//                overridePendingTransition(R.anim.hyperspace_in,
//                        R.anim.hyperspace_out);
//                break;
//            case 18:
//                overridePendingTransition(R.anim.shake,
//                        R.anim.my_alpha_action);
//                break;
//            case 19:
//                overridePendingTransition(R.anim.push_left_in,
//                        R.anim.push_left_out);
//                break;
//            case 20:
//                overridePendingTransition(R.anim.push_up_in,
//                        R.anim.push_up_out);
//                break;
//            case 21:
//                overridePendingTransition(R.anim.slide_left,
//                        R.anim.slide_right);
//                break;
//            case 22:
//                overridePendingTransition(R.anim.slide_top_to_bottom,
//                        R.anim.my_alpha_action);
//                break;
//            case 23:
//                overridePendingTransition(R.anim.wave_scale,
//                        R.anim.my_alpha_action);
//                break;
//        }
//    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageFinishEvent(DownloadSuccessEvent event) {
        if (event.type.equals("0")) {
            lineLoading.setVisibility(View.GONE);
            tbsView.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new PlayerVideoEvent(RESET));
            displayFile();
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
                ThreadPoolManager.getInstance().execute(new runAddPage());
            }
            else if (event.message.equals("volDes")) {
                ThreadPoolManager.getInstance().execute(new runDesPage());
            }
        }
    }


    private class runAddPage implements Runnable{
        @Override
        public void run() {
            //利用ProcessBuilder执行shell命令
            String[] order = {
                    "input",
                    "swipe",
                    "" + 100,
                    "" + 1000,
                    "" + 100,
                    "" + 10
            };
            try {
                new ProcessBuilder(order).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class runDesPage implements Runnable{
        @Override
        public void run() {
            //利用ProcessBuilder执行shell命令
            String[] order = {
                    "input",
                    "swipe",
                    "" + 100,
                    "" + 10,
                    "" + 100,
                    "" + 1000
            };
            try {
                new ProcessBuilder(order).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
