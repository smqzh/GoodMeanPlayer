package com.learning.downloader.core.thread;


import android.os.Process;
import android.util.Log;

import com.learning.downloader.config.Config;
import com.learning.downloader.core.DownloadResponse;
import com.learning.downloader.domain.DownloadInfo;
import com.learning.downloader.domain.DownloadThreadInfo;
import com.learning.downloader.event.DownloadProgressEvent;
import com.learning.downloader.event.DownloadSuccessEvent;
import com.learning.downloader.exception.DownloadException;
import com.learning.downloader.exception.DownloadPauseException;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by renpingqing on 17/1/22.
 */

public class DownloadThread implements Runnable {

    public static final String TAG = "DownloadThread";

    private final DownloadThreadInfo downloadThreadInfo;
    private final DownloadResponse downloadResponse;
    private final Config config;
    private final DownloadInfo downloadInfo;
    private final DownloadProgressListener downloadProgressListener;
    private long lastProgress;
    private InputStream inputStream;
    private int retryDownloadCount = 0;

    public DownloadThread(DownloadThreadInfo downloadThreadInfo, DownloadResponse downloadResponse,
                          Config config,
                          DownloadInfo downloadInfo, DownloadProgressListener downloadProgressListener) {
        this.downloadThreadInfo = downloadThreadInfo;
        this.downloadResponse = downloadResponse;
        this.config = config;
        this.downloadInfo = downloadInfo;
        this.lastProgress = downloadThreadInfo.getProgress();
        this.downloadProgressListener = downloadProgressListener;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
//    while (!(downloadInfo.isPause() || downloadThreadInfo.isThreadDownloadSuccess())) {

        checkPause();
        try {
            executeDownload();
        } catch (DownloadException e) {

//        if (retryDownloadCount >= config.getRetryDownloadCount()) {
            downloadInfo.setStatus(DownloadInfo.STATUS_ERROR);
            downloadInfo.setException(e);
            downloadResponse.onStatusChanged(downloadInfo);
            downloadResponse.handleException(e);
//        }
//
//        retryDownloadCount++;
        }
//    checkPause();
//    }
    }

    private void executeDownload() {
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL(downloadThreadInfo.getUri());
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(config.getConnectTimeout());
            httpConnection.setReadTimeout(config.getReadTimeout());
            httpConnection.setRequestMethod(config.getMethod());
            long lastStart = downloadThreadInfo.getStart() + lastProgress;
            if (downloadInfo.isSupportRanges()) {
                httpConnection.setRequestProperty("Range",
                        "bytes=" + lastStart + "-" + downloadThreadInfo.getEnd());
            }
            final int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_PARTIAL
                    || responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
                RandomAccessFile raf =null;
                try {
                     raf = new RandomAccessFile(downloadInfo.getPath(), "rwd");
                    raf.seek(lastStart);
                }catch (IOException e){
                    raf.close();
                }
                byte[] bf = new byte[1024 * 4];//TODO 缓冲区大小
                int len = -1;
                int offset = 0;
                while (true) {
                    synchronized (this) {
                        checkPause();
                    }
                    len = inputStream.read(bf);
                    if (len == -1) {
                        break;
                    }
                    raf.write(bf, 0, len);
                    offset += len;
                    downloadThreadInfo.setProgress(lastProgress + offset);
                    downloadProgressListener.onProgress();
                    EventBus.getDefault().post(new DownloadProgressEvent(downloadInfo.getFileid(),downloadInfo.getProgress(),downloadInfo.getSize()));
//                    Log.d(TAG,
//                            "downloadInfo:" + downloadInfo.getId() + " thread:" + downloadThreadInfo.getThreadId()+"数据下载量---："+offset);
                }
                raf.close();
                inputStream.close();

                //downloadInfo success
                downloadProgressListener.onDownloadSuccess();
            } else {
               // EventBus.getDefault().post(new DownloadSuccessEvent("1",downloadInfo.getUri()));
                throw new DownloadException(DownloadException.EXCEPTION_SERVER_SUPPORT_CODE,
                        "UnSupported response code:" + responseCode);
            }
            checkPause();
        } catch (ProtocolException e) {
            throw new DownloadException(DownloadException.EXCEPTION_PROTOCOL, "Protocol error", e);
        } catch (IOException e) {
            throw new DownloadException(DownloadException.EXCEPTION_IO_EXCEPTION, "IO error", e);
        } catch (DownloadPauseException e) {
            //TODO process pause logic
        } catch (Exception e) {
            throw new DownloadException(DownloadException.EXCEPTION_OTHER, "other error", e);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }

    private void checkPause() {
        if (downloadInfo.isPause()) {
         //   EventBus.getDefault().post(new DownloadSuccessEvent("1",downloadInfo.getUri()));
            throw new DownloadPauseException(DownloadException.EXCEPTION_PAUSE);
        }
    }

    /**
     * Download thread progress listener.
     */
    public interface DownloadProgressListener {

        void onProgress();

        void onDownloadSuccess();
    }


}
