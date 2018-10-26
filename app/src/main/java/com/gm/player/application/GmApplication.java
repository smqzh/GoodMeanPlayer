package com.gm.player.application;

import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.gm.player.ui.activity.ErrorActivity;
import com.gm.player.ui.activity.WelcomeActivity;
import com.gm.player.util.YcSpUtil;
import com.gm.player.util.YcToast;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.smtt.sdk.QbSdk;

import org.litepal.LitePalApplication;

import java.io.File;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import io.vov.vitamio.Vitamio;

import static com.gm.player.util.Tools.isBuildN;

public class GmApplication extends LitePalApplication {

    private String TAG=this.getClass().getSimpleName();

    public static GmApplication instance;

    private boolean screenFlag=true;

    /**
     *  activity管理类
     */
    private  ActivityManager activityManager = null;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        Vitamio.isInitialized(getApplicationContext());//加载Vitamio
        initTbs();//加载腾讯tbs
        initYcSp();//加载sharePreference
        initYcToast();
        isBuildN();
        initCatchException();
        initLeakCanary();
        initActivityList();
    }

    private void initActivityList() {
        activityManager=ActivityManager.getInstance();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;
    }

    private void initLeakCanary() {

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
    public ActivityManager getActivityManager() {
        return activityManager;
    }
    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }
    private void initYcToast() {
        YcToast.get().init(getApplicationContext());
    }
    private void initYcSp() {
        YcSpUtil.getInstance().init(getApplicationContext());
    }
    private void initTbs(){
        checkTbsReaderTemp();
        // 初始化tbs腾讯浏览服务（https://x5.tencent.com/tbs/index.html）
        QbSdk.setDownloadWithoutWifi(true);
        boolean b = QbSdk.canLoadX5(this);
        Log.d(TAG, "initTbs: ----> QbSdk.canLoadX5 " + b);
        boolean b1 = QbSdk.canLoadX5FirstTimeThirdApp(this);
        Log.d(TAG, "initTbs: ----> QbSdk.canLoadX5FirstTimeThirdApp " + b1);
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.d(TAG, "initTbs: ----> onCoreInitFinished 内核加载完成");
            }
            @Override
            public void onViewInitFinished(boolean b) {
                Log.d(TAG, "initTbs: ----> onViewInitFinished " + b);
            }
        });
    }

    /**
     * 检查腾讯浏览服务需要的文件夹是否存在
     */
    private void checkTbsReaderTemp() {
        String url= Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp";
        File f = new File(url);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    private void initCatchException(){
        //全局异常处理
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .enabled(true)
                .showErrorDetails(true)
                .showRestartButton(true)
                .trackActivities(false)
                .minTimeBetweenCrashesMs(2000)
                .restartActivity(WelcomeActivity.class)
                .errorActivity(ErrorActivity.class)
                .apply();

    }
    public boolean isScreenFlag() {
        return screenFlag;
    }
    public void setScreenFlag(boolean screenFlag) {
        this.screenFlag = screenFlag;
    }

    public static GmApplication getInstance(){
        return instance;
    }
}
