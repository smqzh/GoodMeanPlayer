package com.gm.player.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.gm.player.R;
import com.gm.player.db.DownLoadFiles;
import com.gm.player.event.NotificatFinishEvent;
import com.gm.player.ui.activity.FileDisplayActivity;
import com.gm.player.ui.activity.ImageActivity;
import com.gm.player.ui.activity.IndexActivity;
import com.gm.player.ui.activity.VideoActivity;
import com.gm.player.ui.activity.WebActivity;
import com.learning.downloader.callback.DownloadManager;
import com.learning.downloader.domain.DownloadInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.support.constraint.Constraints.TAG;
import static com.gm.player.util.Tools.isBuildN;

public class PlayerControll {

    private String zuhe;
    private Context mContext;
    private String[] zurl=null;
    private int[] ztime=null;
    private int zsize=0;
    private int index=0;
    private Handler mHandler;
    private DownloadManager downloadManager;
    private exuteProject exute;
    private int type=0;//效果默认为0
    public PlayerControll(String zuhe,Context ctx,DownloadManager dm){
        this.zuhe=zuhe;
        this.mContext=ctx;
        downloadManager=dm;
        mHandler=new Handler();
    }

    public void run(){
        index=0;
        String zh[]=zuhe.split(";");
        zsize=zh.length;
        zurl=new String[zsize];
        ztime=new int[zsize];
        for(int i=0;i<zsize;i++){
            int len=zh[i].lastIndexOf("_");
            String ur=zh[i].substring(0,len);
            String urtime=zh[i].substring(len+1);
            zurl[i]=ur;
            if(Tools.isDigit(urtime)) {
                ztime[i] = Integer.parseInt(urtime);
            }else{
                zurl[i]="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1532586385364&di=b084d7bda3dfbcfc8fe8b9d8d40705af&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0119af5652db686ac7251c941ddd4d.png";
                ztime[i]=10;
            }
        }
        if(zsize>1){
            exute=new exuteProject();
            toNextActivity(zurl[index],ztime[index]);
            mHandler.postDelayed(exute,(ztime[index]+2)*1000);
        }else{
                toNextActivity(zurl[index],ztime[index]);
        }
    }

    private class exuteProject implements Runnable{
        @Override
        public void run() {
            index++;
            index=index%zsize;
            toNextActivity(zurl[index],ztime[index]);
            mHandler.postDelayed(exute,(ztime[index]+2)*1000);
        }
    }

    private   Intent intent;
    private void toNextActivity(String url,int time){
        EventBus.getDefault().post(new NotificatFinishEvent());
        intent=null;
        if(FileUtil.isPicTure(url)){
            intent=new Intent(mContext, ImageActivity.class);
            DownLoadFiles df= DownLoadFiles.queryFilesBy("url",url);
            String path="";
            if(df==null){//如果不存在
                path=downloadFile(df,url);
                intent.putExtra("flag",1);
            }else{//存在
                if(df.getStatus()!=2){
                    intent.putExtra("flag",1);
                    path=downloadFile(df,url);
                }else if(df.getStatus()==2) {
                    path = df.getFpath();
                }
            }
            intent.putExtra("path",path);
            intent.putExtra("time",time);
        }
        else if(FileUtil.isVideo(url)){
            DownLoadFiles df= DownLoadFiles.queryFilesBy("url",url);
            String path="";
            intent=new Intent(mContext, VideoActivity.class);
            if(df==null){//如果不存在
                path=downloadFile(df,url);
                intent.putExtra("flag",1);
            }else{//存在
                if(df.getStatus()!=2){
                    intent.putExtra("flag",1);
                    path=downloadFile(df,url);
                }else if(df.getStatus()==2) {
                    path = df.getFpath();
                }
            }
            intent.putExtra("path",path);
        }else if(FileUtil.isOffice(url)){
            intent=new Intent(mContext, FileDisplayActivity.class);
            DownLoadFiles df= DownLoadFiles.queryFilesBy("url",url);
            String path="";
            if(df==null){//如果不存在
                path=downloadFile(df,url);
                intent.putExtra("flag",1);
            }else{//存在
                if(df.getStatus()!=2){
                    intent.putExtra("flag",1);
                    path=downloadFile(df,url);
                }else if(df.getStatus()==2) {
                    path = df.getFpath();
                }
            }
            intent.putExtra("path",path);
            intent.putExtra("time",time);

        }else {
            intent=new Intent(mContext, WebActivity.class);
            intent.putExtra("path",url);
            intent.putExtra("time",time);
        }
        IndexActivity indexActivity=(IndexActivity)mContext;
         type = new Random().nextInt(23);
         intent.putExtra("type",type);
      //  if (Tools.isDigit(event.defaultTime)) {
       //     type = Integer.parseInt(event.defaultTime);
      //  }
       ToAnimActivity(intent,indexActivity,type);
    }

    private  void ToAnimActivity(Intent intent,IndexActivity ay,int type){
              ay.startActivity(intent);
        switch (type) {
            case 0:
                ay.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case 1:
                ay.overridePendingTransition(R.anim.alpha_rotate,
                        R.anim.my_alpha_action);
                break;
            case 2:
                ay.overridePendingTransition(R.anim.alpha_scale_rotate,
                        R.anim.my_alpha_action);
                break;
            case 3:
                ay.overridePendingTransition(
                        R.anim.alpha_scale_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 4:
                ay.overridePendingTransition(R.anim.alpha_scale_translate,
                        R.anim.my_alpha_action);
                break;
            case 5:
                ay.overridePendingTransition(R.anim.alpha_scale,
                        R.anim.my_alpha_action);
                break;
            case 6:
                ay.overridePendingTransition(R.anim.alpha_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 7:
                ay.overridePendingTransition(R.anim.alpha_translate,
                        R.anim.my_alpha_action);
                break;
            case 8:
                ay.overridePendingTransition(R.anim.my_rotate_action,
                        R.anim.my_alpha_action);
                break;
            case 9:
                ay.overridePendingTransition(R.anim.my_scale_action,
                        R.anim.my_alpha_action);
                break;
            case 10:
                ay.overridePendingTransition(R.anim.my_translate_action,
                        R.anim.my_alpha_action);
                break;
            case 11:
                ay.overridePendingTransition(R.anim.myanimation_simple,
                        R.anim.my_alpha_action);
                break;
            case 12:
                ay.overridePendingTransition(R.anim.myown_design,
                        R.anim.my_alpha_action);
                break;
            case 13:
                ay.overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            case 14:
                ay.overridePendingTransition(R.anim.scale_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 15:
                ay.overridePendingTransition(R.anim.scale_translate,
                        R.anim.my_alpha_action);
                break;
            case 16:
                ay.overridePendingTransition(R.anim.translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 17:
                ay.overridePendingTransition(R.anim.hyperspace_in,
                        R.anim.hyperspace_out);
                break;
            case 18:
                ay.overridePendingTransition(R.anim.shake,
                        R.anim.my_alpha_action);
                break;
            case 19:
                ay.overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case 20:
                ay.overridePendingTransition(R.anim.push_up_in,
                        R.anim.push_up_out);
                break;
            case 21:
                ay.overridePendingTransition(R.anim.slide_left,
                        R.anim.slide_right);
                break;
            case 22:
                ay.overridePendingTransition(R.anim.slide_top_to_bottom,
                        R.anim.my_alpha_action);
                break;
            case 23:
                ay.overridePendingTransition(R.anim.wave_scale,
                        R.anim.my_alpha_action);
                break;
        }
    }

    //添加到下载列表中
    private void addTask(DownLoadFiles df){
        DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(df.getUrl())
                .setPath(df.getFpath())
                .setFiled(df.getFileId())
                .build();
        downloadManager.download(downloadInfo);
    }

    private String downloadFile(DownLoadFiles df,String url){
        List<DownLoadFiles> dList=new ArrayList<>();
        String fname = FileUtil.getFileName(url);
        String fpath = "";
        if(FileUtil.isPicTure(url)){
            fpath = Constant.picturepath + fname;
        }
        else if(FileUtil.isVideo(url)){
            fpath = Constant.videopath + fname;
        }
        else if(FileUtil.isOffice(url)){
            fpath = Constant.officepath + fname;
        }
        isBuildN();
        FileUtil.createFlePath(mContext,fpath);
        df = new DownLoadFiles();
        df.setToDefault("down");
        df.setToDefault("status");
        df.setToDefault("downloadError");
        df.setFileId("gm" + System.currentTimeMillis());
        df.setFname(fname);
        df.setFpath(fpath);
        df.setUrl(url);
        dList.add(df);
        DownLoadFiles.insertFiles(dList);
        addTask(df);
        return fpath;
    }


    public void cancel(){
        if(mHandler!=null){
            mHandler.removeCallbacks(exute);
            exute=null;
            mHandler=null;
        }
    }

    public void pause(){
        synchronized (this) {
            if(mHandler!=null){
            mHandler.removeCallbacks(exute);
            }
        }
    }

    public void restart(){
      synchronized (this) {
           if(mHandler!=null){
          mHandler.postDelayed(exute, (ztime[index] + 2) * 1000);
          }
      }
    }

}
