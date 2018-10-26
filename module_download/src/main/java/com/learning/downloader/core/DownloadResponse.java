package com.learning.downloader.core;


import com.learning.downloader.domain.DownloadInfo;
import com.learning.downloader.exception.DownloadException;

/**
 * Created by renpingqing on 17/1/22.
 */

public interface DownloadResponse {

  void onStatusChanged(DownloadInfo downloadInfo);

  void handleException(DownloadException exception);
}
