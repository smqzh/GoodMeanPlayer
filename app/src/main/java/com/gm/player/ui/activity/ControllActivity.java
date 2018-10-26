package com.gm.player.ui.activity;

import android.content.Intent;

import com.gm.player.R;
import com.gm.player.application.MySp;
import com.gm.player.base.BaseActivity;
import com.gm.player.bean.LoopBean;
import com.gm.player.event.NotificatFinishEvent;
import com.gm.player.ui.adapter.AnimationListAdapter;
import com.gm.player.util.FileUtil;
import com.gm.player.util.Tools;
import com.gm.player.widget.AnimationListView;
import com.gm.player.widget.BlindsView;
import com.learning.downloader.FilesDownloadService;
import com.learning.downloader.callback.DownloadManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import butterknife.BindView;

public class ControllActivity extends BaseActivity{


    private DownloadManager downloadManager;
    @BindView(R.id.blinds_animation)
    AnimationListView mBlindsAnimation;

    private android.os.Handler handler;
    private AnimationListAdapter mAdapter;
    private List<LoopBean> mList=new ArrayList<>();

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_controll;
    }


    @Override
    protected void initViews() {
        mToolBarSet.hide();
        getWindow().getDecorView().setRotation(MySp.getRotation());
        EventBus.getDefault().register(this);
        downloadManager= FilesDownloadService.getDownloadManager(getApplicationContext());
    }

    @Override
    protected void initData() {
        handler=new android.os.Handler();
        Intent intent = getIntent();
        if(intent!=null){
            String  path = intent.getStringExtra("path");
            mList= splitString(path);
            mAdapter=new AnimationListAdapter(this,mList,downloadManager);
            mBlindsAnimation.setAdapter(mAdapter);
            mBlindsAnimation.setAnimationClass(BlindsView.class);
            mBlindsAnimation.setIsVertical(false);
         //   handler.postDelayed(new runNext(),5000);
        }
    }

    private class runNext implements Runnable{
        @Override
        public void run() {

            handler.postDelayed(new runNext(),5000);
        }
    }

    private List<LoopBean> splitString(String path){
        List<LoopBean> list=new ArrayList<>();
        String zh[]=path.split(";");
        int size=zh.length;
        for(int i=0;i<size;i++){
            int len=zh[i].lastIndexOf("_");
            String ur=zh[i].substring(0,len);
            String urtime=zh[i].substring(len+1);
            int time=0;
            if(Tools.isDigit(urtime)) {
                time = Integer.parseInt(urtime);
            }else{
                ur="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1532586385364&di=b084d7bda3dfbcfc8fe8b9d8d40705af&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0119af5652db686ac7251c941ddd4d.png";
                time=10;
            }
            int type;
            if(FileUtil.isVideo(ur)){
                type=2;
            }else if(FileUtil.isPicTure(ur)){
                type=0;
            }else if(FileUtil.isOffice(ur)){
                type=3;
            }else{
                type=1;
            }
            list.add(new LoopBean(type,ur,time));
        }
        return list;

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageFinishEvent(NotificatFinishEvent event) {
        if(mAdapter!=null){
            mAdapter.onDestroy();
        }
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
