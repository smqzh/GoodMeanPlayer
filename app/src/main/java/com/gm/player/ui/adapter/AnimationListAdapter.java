package com.gm.player.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gm.player.R;
import com.gm.player.bean.LoopBean;
import com.gm.player.db.DownLoadFiles;
import com.gm.player.util.Constant;
import com.gm.player.util.FileUtil;
import com.learning.downloader.callback.DownloadManager;
import com.learning.downloader.domain.DownloadInfo;
import com.learning.downloader.event.DownloadProgressEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.widget.VideoView;

import static android.support.constraint.Constraints.TAG;
import static com.gm.player.util.Tools.isBuildN;

public class AnimationListAdapter extends BaseAdapter {

    private List<LoopBean>  mList;
    private ViewHolder viewHolder;
    private LoopBean loopBean;
    private String Spath;
    private Context mContext;
    private DownloadManager downloadManager;
    public AnimationListAdapter(Context mContext,List<LoopBean> lb,DownloadManager downloadManager){
        EventBus.getDefault().register(this);
        this.mContext=mContext;
        this.mList=lb;
        this.downloadManager=downloadManager;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public LoopBean getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LoopBean lb=mList.get(position);
        if(convertView == null) {
            holder = new ViewHolder();
            Log.d(TAG, "getView: ----type"+lb.getType()+"--url"+lb.getUrl());
            if(lb.getType()==0){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_controll_img, null);
                holder.lin_img = (ImageView)convertView.findViewById(R.id.lin_img);
                holder.tvSize = (TextView) convertView.findViewById(R.id.tvSize);
                holder.line_loading = (LinearLayout) convertView.findViewById(R.id.line_loading);
            }else if(lb.getType()==1){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_controll_web, null);
                holder.lin_web = (WebView)convertView.findViewById(R.id.lin_web);
            }
            else if(lb.getType()==2){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_controll_video, null);
                holder.lin_video = (VideoView)convertView.findViewById(R.id.lin_video);
                holder.tvSize = (TextView) convertView.findViewById(R.id.tvSize);
                holder.line_loading = (LinearLayout) convertView.findViewById(R.id.line_loading);
            }else{
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_controll_web, null);
//                holder.lin_web = (WebView)convertView.findViewById(R.id.lin_web);
            }
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }
        viewHolder=holder;
        loopBean=lb;
        Loop(holder,lb,convertView);
        return convertView;
    }


    private String fileIdIndex="";
    private void initDownload(String path) {
        DownLoadFiles data = DownLoadFiles.queryFilesBy("fpath", path);
        fileIdIndex=data.getFileId();
    }
    public void Loop(ViewHolder vh,LoopBean lb,View vi){

        String url=lb.getUrl();
        if(lb.getType()==0){ //图片
            DownLoadFiles df= DownLoadFiles.queryFilesBy("url",url);
            String path="";
            if(df==null){//如果不存在
                path=downloadFile(df,url);
                if( vh.line_loading!=null) {
                    vh.line_loading.setVisibility(View.VISIBLE);
                }
                Spath=path;
                initDownload(path);
            }else{//存在
                path=df.getFpath();
                if( vh.line_loading!=null){
                    vh.line_loading.setVisibility(View.GONE);
                }
                if(vh.lin_img!=null){
                    Resources resources = mContext.getResources();
                    DisplayMetrics dm = resources.getDisplayMetrics();
                    int width = dm.widthPixels;
                    int height = dm.heightPixels;
                    Glide.with(mContext).load(path).error(R.drawable.bg_loading).override(width,height).into(vh.lin_img);
                }
            }

        }else if(lb.getType()==1){//网页
            if(vh.lin_web!=null) {
                WebSettings webSettings = vh.lin_web.getSettings();
                webSettings.setUseWideViewPort(true);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setJavaScriptEnabled(true);
                webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
                webSettings.setDomStorageEnabled(true);
                webSettings.setDatabaseEnabled(true);
                webSettings.setPluginState(WebSettings.PluginState.ON);
//        webSettings.setUseWideViewPort(true); // 关键点
                webSettings.setAllowFileAccess(true); // 允许访问文件
                webSettings.setSupportZoom(true); // 支持缩放
//        webSettings.setLoadWithOverviewMode(true);
                webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 加载缓存内容
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
//允许JS互调。可以使用网页上的功能代码了
                vh.lin_web.getSettings().setJavaScriptEnabled(true);
                vh.lin_web.loadUrl(url);
            }
        }
        else if(lb.getType()==2){//视频
            DownLoadFiles df= DownLoadFiles.queryFilesBy("url",url);
            String path="";
            if(df==null){//如果不存在
                path=downloadFile(df,url);
                if(vh.line_loading!=null){
                    vh.line_loading.setVisibility(View.VISIBLE);
                }
                Spath=path;
                initDownload(path);
            }else{//存在
                path=df.getFpath();
                if(vh.line_loading!=null) {
                    vh.line_loading.setVisibility(View.GONE);
                }
                if( vh.lin_video!=null){
                    vh.lin_video.setVideoPath(path);
                }
            }
        }else{//ppt


        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadProgressEvent event) {
        if(event.fileid.equals(fileIdIndex)) {
            int percent=(int)(event.progress*100.0/event.size);
            if(viewHolder.tvSize!=null) {
                viewHolder.tvSize.setText("正在下载" + percent + "% ");
                if (percent == 100) {
                    DownLoadFiles df = DownLoadFiles.queryFilesBy("fileId", event.fileid);
                    if(  viewHolder.line_loading!=null) {
                        viewHolder.line_loading.setVisibility(View.GONE);
                    }
                    if (FileUtil.isPicTure(df.getUrl())) {
                        Glide.with(mContext).load(Spath).error(R.drawable.bg_loading).into(viewHolder.lin_img);
                    } else if (FileUtil.isVideo(df.getUrl())) {
                        viewHolder.lin_video.setVideoPath(Spath);
                    }
                }
            }
        }
    }

    class ViewHolder{
        public ImageView lin_img;
        public VideoView lin_video;
        public WebView  lin_web;
        public LinearLayout line_loading;
        public TextView tvSize;
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

    public void onDestroy(){
        EventBus.getDefault().unregister(this);
    }

}
