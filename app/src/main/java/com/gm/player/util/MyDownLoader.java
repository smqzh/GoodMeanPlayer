package com.gm.player.util;

import android.content.Context;

import com.gm.player.db.DownLoadFiles;
import com.learning.downloader.callback.DownloadManager;
import com.learning.downloader.domain.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

import static com.gm.player.util.Tools.isBuildN;

public class MyDownLoader {

    private DownloadManager downloadManager;
    private Context mContext;

    public MyDownLoader(DownloadManager downloadManager, Context mContext) {
        this.downloadManager = downloadManager;
        this.mContext = mContext;
    }

    //添加到下载列表中
    private void addTask(DownLoadFiles df){
        DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(df.getUrl())
                .setPath(df.getFpath())
                .setFiled(df.getFileId())
                .build();
        downloadManager.download(downloadInfo);
    }

    public String downloadFile(DownLoadFiles df,String url){
        List<DownLoadFiles> dList=new ArrayList<>();
        String fname = FileUtil.getFileName(url);
        String fpath = "";
        if(FileUtil.isPicTure(url)){
            fpath = Constant.picturepath + fname;
        }
        else if(FileUtil.isVideo(url)){
            fpath = Constant.videopath + fname;
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
}
