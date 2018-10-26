package com.gm.player.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gm.player.R;
import com.gm.player.application.Constant;
import com.gm.player.application.GmApplication;
import com.gm.player.application.MySp;
import com.gm.player.base.BaseActivity;
import com.gm.player.db.DownLoadFiles;
import com.gm.player.event.ConnectSuccessEvent;
import com.gm.player.event.DefaultScreenEvent;
import com.gm.player.event.HeadNumberEvent;
import com.gm.player.event.NotificatFinishEvent;
import com.gm.player.event.PlayerVideoEvent;
import com.gm.player.service.SuperSocket;
import com.gm.player.socket.NettyClient;
import com.gm.player.socket.NettyListener;
import com.gm.player.ui.fragment.AboutFragment;
import com.gm.player.ui.fragment.BuildingFragment;
import com.gm.player.ui.fragment.ContentFragment;
import com.gm.player.ui.fragment.FilesFragment;
import com.gm.player.util.ActivityRotationController;
import com.gm.player.util.PController;
import com.gm.player.util.StringUtil;
import com.gm.player.util.ThreadPoolManager;
import com.gm.player.util.Tools;
import com.gm.player.util.YcToast;
import com.gm.player.widget.FocusLinearLayout;
import com.learning.downloader.FilesDownloadService;
import com.learning.downloader.callback.DownloadManager;
import com.learning.downloader.config.Config;
import com.learning.downloader.domain.DownloadInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.util.Const;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.kind.im.common.InfoMessageEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import static cn.com.kind.im.common.InfoMessageEvent.DELETE;
import static cn.com.kind.im.common.InfoMessageEvent.ZUHE;
import static com.gm.player.event.PlayerVideoEvent.PAUSE;
import static com.gm.player.event.PlayerVideoEvent.RESET;
import static com.gm.player.event.PlayerVideoEvent.RESETVIDEO;
import static com.gm.player.event.PlayerVideoEvent.START;
import static com.gm.player.ui.fragment.ContentFragment.ABOUT;
import static com.gm.player.ui.fragment.ContentFragment.BUILDING;
import static com.gm.player.ui.fragment.ContentFragment.FILES;
import static com.gm.player.ui.fragment.ContentFragment.INDEX;

public class IndexActivity extends BaseActivity{

    @BindView(R.id.index)
    FocusLinearLayout mIndex;
    @BindView(R.id.files)
    FocusLinearLayout mFiles;
    @BindView(R.id.setting)
    FocusLinearLayout mSetting;
    @BindView(R.id.about)
    FocusLinearLayout mAbout;
    @BindView(R.id.tvIndex)
    TextView tvIndex;
    @BindView(R.id.tvFiles)
    TextView tvFiles;
    @BindView(R.id.tvSetting)
    TextView tvSetting;
    @BindView(R.id.content_overlay)
    RelativeLayout contentOverlay;
    @BindView(R.id.ivIndex)
    ImageView ivIndex;
    @BindView(R.id.ivFiles)
    ImageView ivFiles;
    @BindView(R.id.ivSetting)
    ImageView ivSetting;
    @BindView(R.id.ivAbout)
    ImageView ivAbout;
    @BindView(R.id.tvAbout)
    TextView tvAbout;
    @BindView(R.id.line_bottom)
    LinearLayout lineBottom;
    private String TAG = this.getClass().getSimpleName();

    private ContentFragment contentFragment;
    private List<Fragment> fragmentList = new ArrayList<>();

    private DownloadManager downloadManager;
    private Handler mHandler;
    //  private RunLockScreen runLock;
    private initDefaultScreen initDefaultScreen;
    //   private PlayerControll mPc;
    private PController mPc;

    private TimeThread tt;
    /**
     * 记录用户首次点击返回键的时间
     */
    private long firstTime = 0;
    private final static int EXIT_INTERVAL = 2000;

    private FocusLinearLayout mLine[] = new FocusLinearLayout[4];
    private int currentIndex = 0;
    private String Code="UTF-8";
    private NettyClient nettyClient;//socket操作连接对象
  //  private ActivityRotationController activityRotationController;
    @Override
    protected int attachLayoutRes() {
        EventBus.getDefault().register(this);
        return R.layout.activity_index;
    }

    @Override
    protected void initViews() {
        initDownConfig();
        BindSocketService();
        mContext=this;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        contentFragment = ContentFragment.newInstance(INDEX);
        addFragment(contentFragment, INDEX);
        showFragment(contentFragment);
        initLineLayout(); //遥控设置
        // initSocketTcp();
        initScreen();
        getWindow().getDecorView().setRotation(MySp.getRotation());
    }

    private void initScreen() {
        if (MySp.getScreenState()== 0) {
            GmApplication.getInstance().setScreenFlag(true);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //设置为置屏幕为.竖屏
        } else {
            GmApplication.getInstance().setScreenFlag(false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void initData() {
        tt=new TimeThread();
        tt.start();
        Tools.isGrantExternalRW(this);
        //initListenPort();
        downloadManager = FilesDownloadService.getDownloadManager(getApplicationContext());
        //   runLock = new RunLockScreen();
        initDefaultScreen=new initDefaultScreen();
        mHandler = new Handler();
        mToolBarSet.setCenterTitle(INDEX);
        // activityRotationController=new ActivityRotationController(this);
    }

    private void sendMessage(String msg){
           /*
                    调用的发送。
                     */
        nettyClient.sendMsgToServer(msg, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {                //4
                    Log.d(TAG, "发送成功");
                } else {
                    Log.d(TAG, "发送失败");
                }
            }

        });
    }

    private void initLineLayout() {

        mIndex.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ivIndex.setImageDrawable(getResources().getDrawable(R.mipmap.icon_index_check));
                    tvIndex.setTextColor(getResources().getColor(R.color.black));
                } else {
                    ivIndex.setImageDrawable(getResources().getDrawable(R.mipmap.icon_index));
                    tvIndex.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        mFiles.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ivFiles.setImageDrawable(getResources().getDrawable(R.mipmap.icon_files_check));
                    tvFiles.setTextColor(getResources().getColor(R.color.black));
                } else {
                    ivFiles.setImageDrawable(getResources().getDrawable(R.mipmap.icon_files));
                    tvFiles.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        mSetting.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ivSetting.setImageDrawable(getResources().getDrawable(R.mipmap.icon_setting_check));
                    tvSetting.setTextColor(getResources().getColor(R.color.black));
                } else {
                    ivSetting.setImageDrawable(getResources().getDrawable(R.mipmap.icon_setting));
                    tvSetting.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        mAbout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ivAbout.setImageDrawable(getResources().getDrawable(R.mipmap.icon_about_check));
                    tvAbout.setTextColor(getResources().getColor(R.color.black));
                } else {
                    ivAbout.setImageDrawable(getResources().getDrawable(R.mipmap.icon_about));
                    tvAbout.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
        mLine[0] = mIndex;
        mLine[1] = mFiles;
        mLine[2] = mSetting;
        mLine[3] = mAbout;
        mLine[currentIndex].requestFocus();
    }

    /**
     * 配置下载的基础规则
     */
    private void initDownConfig() {
        try {

            Config config = new Config();
            config.setEachDownloadThread(5);
            config.setDownloadThread(5);
            config.setConnectTimeout(10000);
            config.setReadTimeout(10000);
            FilesDownloadService.getDownloadManager(getApplicationContext(), config);
        } catch (Exception e) {
            Log.d(TAG, "配置下载失败");
        }
    }

    private void initLineBottom() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Animation anim= AnimationUtils.loadAnimation(mContext,R.anim.fade_out);
//                lineBottom.setAnimation(anim);
                lineBottom.setVisibility(View.GONE);
            }
        },6*1000);
    }

    public void initDefaultPlayer() {
        if (mHandler != null) {
            if (!StringUtil.isEmptyorNull(MySp.getDefaultUrl())) {
                Log.d(TAG, "initDefaultPlayer: -----------发送屏保");
                mHandler.postDelayed(initDefaultScreen, MySp.getScreenTime() * 1000);
            }
        }
    }
   public void RotationView(int rotation){
      getWindow().getDecorView().setRotation(rotation);
   }

    public void StopDefaultScreen(){
        Log.d(TAG, "StopDefaultScreen: ----停止屏保");
        mHandler.removeCallbacks(initDefaultScreen);
    }


    private class initDefaultScreen implements Runnable{
        @Override
        public void run() {
            String url = MySp.getDefaultUrl();
            if (!StringUtil.isEmptyorNull(url)) {
                String str[] = url.split(";");
                EventBus.getDefault().post(new InfoMessageEvent(str[0], str[1], str[2], str[3]));
            }
        }
    }


    /*添加fragment*/
    private void addFragment(Fragment fragment, String tag) {
        /*判断该fragment是否已经被添加过  如果没有被添加  则添加*/
        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment, tag).commit();
            /*添加到 fragmentList*/
            fragmentList.add(fragment);
        }
    }

    /*显示fragment*/
    private void showFragment(Fragment fragment) {
        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                Log.d(TAG, "showFragment: ---" + frag.toString());
                /*先隐藏其他fragment*/
                getSupportFragmentManager().beginTransaction().hide(frag).commit();
            }
        }
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onMessageEvent(HeadNumberEvent event){
        mTvNumber.setText(event.number);
    }

    //监听服务端发送的信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InfoMessageEvent event) {
        if (mPc != null) {//如果不等于空，那就取消现有的任务
            mPc.cancel();
        }
        String isDefault=event.isdefault;
        if (!StringUtil.isEmptyorNull(isDefault)) {
            if (isDefault.equals("0")) {
                String url = event.type + ";" + event.message + ";" + event.defaultTime + ";" + event.isdefault;
                MySp.setDefaultUrl(url);
            }
        }

        if (event.type.equals(ZUHE)) {//组合播放   组合播放的时候，默认defaultTime为动画效果值的范围在0-23
            EventBus.getDefault().post(new NotificatFinishEvent());
            if(mPc!=null){
                mPc.cancel();
                mPc=null;
            }
            mPc = new PController(event.message, this,downloadManager,event.isdefault);//组合播放控制器
            mPc.run();
            //  toNextActivity(ZUHE,event.message,"");

        } else if (event.type.equals(DELETE)) {
            String url = event.message;
            DownLoadFiles df = DownLoadFiles.queryFilesBy("url", url);
            Log.d(TAG, "onMessageEvent: ----" + url);
            if (df != null) {
                boolean flag = DownLoadFiles.deleteBykey("url", url);
                if (flag) {
                    YcToast.get().toast("文件" + url + "已删除");
                    ThreadPoolManager.getInstance().execute(new DeleteRunTask(df.getFpath()));
                }
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PlayerVideoEvent event) {
        if (event.message.equals(PAUSE)) {
            if (mPc != null) {
                mPc.pause();
                Log.d(TAG, "onMessageEvent: -----任务暂停");
            }
        } else if (event.message.equals(RESET)) {
            if (mPc != null) {
                mPc.restart();
                Log.d(TAG, "onMessageEvent: -----任务恢复");
            }
        }else if (event.message.equals(RESETVIDEO)) {
            if (mPc != null) {
                mPc.restartVideo();
                Log.d(TAG, "onMessageEvent: -----视频结束，任务恢复");
            }
        }else if (event.message.equals(START)) {
            if (mPc != null) {
                mPc.start();
                Log.d(TAG, "onMessageEvent: -----视频结束，任务恢复");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DefaultScreenEvent event) {
        initDefaultPlayer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConnectSuccessEvent event) {
        initSend();
    }

    private void initSend(){
        String data=MySp.getDeviceNumber()+"TT~TT0TT~TT10TT~TT连接服务器XX~XX"+MySp.getDeviceNumber()+"TT~TT0TT~TT301TT~TT请求FTP配置XX~XX";
        sendMessage(data);
    }


    @OnClick({R.id.index, R.id.files, R.id.setting, R.id.about})
    public void onViewClicked(View view) {
        StopDefaultScreen();
        switch (view.getId()) {
            case R.id.index:
                // mIndex.setTranslationZ(10);
                mToolBarSet.setCenterTitle(INDEX);
                ContentFragment contentFragment = ContentFragment.newInstance(INDEX);
                addFragment(contentFragment, INDEX);
                showFragment(contentFragment);
                break;
            case R.id.files:
                mToolBarSet.setCenterTitle(FILES);
                FilesFragment filesFragment = FilesFragment.newInstance(FILES);
                addFragment(filesFragment, FILES);
                showFragment(filesFragment);
                break;
            case R.id.setting:
                mToolBarSet.setCenterTitle(BUILDING);
                BuildingFragment buildingFragment = BuildingFragment.newInstance(BUILDING);
                addFragment(buildingFragment, BUILDING);
                showFragment(buildingFragment);
                break;
            case R.id.about:
                mToolBarSet.setCenterTitle(ABOUT);
                AboutFragment aboutFragment = AboutFragment.newInstance(ABOUT);
                addFragment(aboutFragment, ABOUT);
                showFragment(aboutFragment);
                break;
        }
    }


    private class DeleteRunTask implements Runnable {

        private String path;

        public DeleteRunTask(String fpath) {
            this.path = fpath;
        }

        @Override
        public void run() {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }


    //添加到下载列表中
    private void addTask(DownLoadFiles df) {
        // URL url=new URL(Tools.encodeDownloadUrl(df.getUrl()));
        DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(df.getUrl())
                .setPath(df.getFpath())
                .setFiled(df.getFileId())
                .build();
        downloadManager.download(downloadInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mHandler!=null) {
            initDefaultPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null) {
            StopDefaultScreen();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindSocketService();
        EventBus.getDefault().unregister(this);
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
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > EXIT_INTERVAL) {
                // lineBottom.setVisibility(View.GONE);
                YcToast.get().toast(getResources().getString(R.string.lg_moreExit));
                firstTime = secondTime;
                return true;
            } else {
                finishAll();
            }
        }
        return super.onKeyUp(keyCode, event);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            //模拟器测试时键盘中的的Enter键，模拟ok键（推荐TV开发中使用蓝叠模拟器）
            case KeyEvent.KEYCODE_ENTER:
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                // YcToast.get().toast("你按下中间键");
                lineBottom.setVisibility(View.VISIBLE);
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                //  YcToast.get().toast("你按下下方向键");
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                //   YcToast.get().toast("你按下左方向键");
                currentIndex--;
                if(currentIndex<0){
                    currentIndex=3;
                }
                mLine[currentIndex].requestFocus();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //  YcToast.get().toast("你按下右方向键");
                currentIndex++;
                if(currentIndex>3){
                    currentIndex=0;
                }
                mLine[currentIndex].requestFocus();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                //  YcToast.get().toast("你按下上方向键");
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void finishAll() {
        if (mPc != null) {
            mPc.cancel();
        }
        if(tt!=null){
            timeFlag=false;
        }
        StopDefaultScreen();
        stopService(new Intent(mContext, SuperSocket.class));
        GmApplication.getInstance().getActivityManager().finishAllActivity();
        //  finish();
    }

    public void reConnect(){
        socketService.resetConnect();
    }

    private boolean timeFlag=true;
    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    mHandlers.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (timeFlag);
        }
    }


    //在主线程里面处理消息并更新UI界面
    private Handler mHandlers = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("HH:mm", sysTime);
                    mTvTime.setText(sysTimeStr); //更新时间
                    break;
                default:
                    break;
            }
        }
    };


}
