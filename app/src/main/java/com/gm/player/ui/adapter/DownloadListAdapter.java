package com.gm.player.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gm.player.R;
import com.gm.player.db.DownLoadFiles;
import com.gm.player.util.FileUtil;
import com.gm.player.util.MyDownloadListener;
import com.gm.player.util.Tools;
import com.learning.downloader.FilesDownloadService;
import com.learning.downloader.FilesDownloadService;
import com.learning.downloader.callback.DownloadManager;
import com.learning.downloader.domain.DownloadInfo;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;

import io.vov.vitamio.utils.FileUtils;

import static com.learning.downloader.domain.DownloadInfo.STATUS_COMPLETED;
import static com.learning.downloader.domain.DownloadInfo.STATUS_REMOVED;
import static com.learning.downloader.domain.DownloadInfo.STATUS_WAIT;


/**
 * Created by renpingqing on 17/1/19.
 */
public class DownloadListAdapter extends
        BaseRecyclerViewAdapter<DownLoadFiles, DownloadListAdapter.ViewHolder> {

  private static final String TAG = "DownloadListAdapter";
  private final Context context;
  private final DownloadManager downloadManager;
//  private DBController dbController;
  public DownloadListAdapter(Context context) {
    super(context);
    this.context = context;
    downloadManager = FilesDownloadService.getDownloadManager(context.getApplicationContext());
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(context).inflate(
            R.layout.item_download_info, parent, false));
  }

  @Override
  public void onBindViewHolder(DownloadListAdapter.ViewHolder holder, final int position) {
    holder.bindData(getData(position), position, context);

    holder.itemView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (onItemClickListener != null) {
          onItemClickListener.onItemClick(position);
        }
      }
    });
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    private final ImageView iv_icon;
    private final TextView tv_size;
    private final TextView tv_status;
    private final ProgressBar pb;
    private final TextView tv_name;
    private final Button bt_action;
    private DownloadInfo downloadInfo;

    public ViewHolder(View view) {
      super(view);
      itemView.setClickable(true);
      iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
      tv_size = (TextView) view.findViewById(R.id.tv_size);
      tv_status = (TextView) view.findViewById(R.id.tv_status);
      pb = (ProgressBar) view.findViewById(R.id.pb);
      tv_name = (TextView) view.findViewById(R.id.tv_name);
      bt_action = (Button) view.findViewById(R.id.bt_action);
    }

    @SuppressWarnings("unchecked")
    public void bindData(final DownLoadFiles data, int position, final Context context) {
      Glide.with(context).load(R.drawable.icon_folder).into(iv_icon);
      int size=data.getFname().length();
      String str="";
      if(size>35){
        str=data.getFname().substring(0,35)+"...";
      }else{
        str=data.getFname();
      }
      tv_name.setText(str);
      try {
        URL url = new URL(data.getUrl());
        downloadInfo = downloadManager.getDownloadById(url.hashCode());
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
      if (downloadInfo != null) {
        downloadInfo
                .setDownloadListener(new MyDownloadListener(new SoftReference(ViewHolder.this)) {
                  //  Call interval about one second
                  @Override
                  public void onRefresh() {
                    if (getUserTag() != null && getUserTag().get() != null) {
                      ViewHolder viewHolder = (ViewHolder) getUserTag().get();
                      viewHolder.refresh(data);
                    }
                  }
                });
      }else {
//            Create new download task
        createFile(data);
      }
       refresh(data);
//      Download button
      bt_action.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (downloadInfo != null) {

            switch (downloadInfo.getStatus()) {
              case DownloadInfo.STATUS_NONE:
              case DownloadInfo.STATUS_PAUSED:
              case DownloadInfo.STATUS_ERROR:
                //resume downloadInfo
                downloadManager.resume(downloadInfo);
                break;

              case DownloadInfo.STATUS_DOWNLOADING:
              case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
              case STATUS_WAIT:
                //pause downloadInfo
                downloadManager.pause(downloadInfo);
                break;
              case STATUS_COMPLETED:
                downloadManager.remove(downloadInfo);
                break;
            }
          }
        }
      });
    }

    private void createFile(final DownLoadFiles data){
      String filePath=data.getFpath();
      File f = new File(filePath);
      if (!f.exists()) {
        try {
          f.createNewFile();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }

      String path = f.getAbsolutePath();
      try {
        URL url = new URL(data.getUrl());
        downloadInfo = new DownloadInfo.Builder().setUrl(url.toString())
                .setPath(path)
                .setFiled(data.getFileId())
                .build();
        downloadInfo
                .setDownloadListener(new MyDownloadListener(new SoftReference(ViewHolder.this)) {

                  @Override
                  public void onRefresh() {
                  //  notifyDownloadStatus();
                    if (getUserTag() != null && getUserTag().get() != null) {
                      ViewHolder viewHolder = (ViewHolder) getUserTag().get();
                      viewHolder.refresh(data);
                    }
                  }
                });
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
      downloadManager.download(downloadInfo);
    }


    private void refresh(DownLoadFiles data) {
      if (downloadInfo == null) {
        tv_size.setText("");
        pb.setProgress(0);
        bt_action.setText("下载");
        tv_status.setText("");
      } else {
        switch (downloadInfo.getStatus()) {
          case DownloadInfo.STATUS_NONE:
            bt_action.setText("下载");
            tv_status.setText("");
            break;
          case DownloadInfo.STATUS_PAUSED:
              bt_action.setText("继续");
              tv_status.setText("暂停");
              try {
                  pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
              } catch (Exception e) {
                  e.printStackTrace();
              }
              break;
          case DownloadInfo.STATUS_ERROR:
            bt_action.setText("继续");
            tv_status.setText("下载失败");
            try {
              pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
            } catch (Exception e) {
              e.printStackTrace();
            }
            tv_size.setText(FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                    .formatFileSize(downloadInfo.getSize()));
            break;
          case DownloadInfo.STATUS_DOWNLOADING:
          case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
            bt_action.setText("暂停");
            try {
              pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
            } catch (Exception e) {
              e.printStackTrace();
            }
            tv_size.setText(FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                    .formatFileSize(downloadInfo.getSize()));
            tv_status.setText("正在下载");
            break;
          case STATUS_COMPLETED:
            bt_action.setText("删除");
            try {
              pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
            } catch (Exception e) {
              e.printStackTrace();
            }
            tv_size.setText(FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                    .formatFileSize(downloadInfo.getSize()));
            tv_status.setText("下载成功");
            break;
          case STATUS_REMOVED:
            tv_size.setText("");
            pb.setProgress(0);
            bt_action.setText("下载");
            tv_status.setText("");
          case STATUS_WAIT:
            tv_size.setText("");
            pb.setProgress(0);
            bt_action.setText("暂停");
            tv_status.setText("等待下载");
            break;
        }
      }
    }
  }
}
