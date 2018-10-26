package com.learning.downloader.db;

import com.learning.downloader.domain.DownloadInfo;
import com.learning.downloader.domain.DownloadThreadInfo;

import java.util.List;


/**
 * Created by renpingqing on 17/1/23.
 */

public interface DownloadDBController {

  List<DownloadInfo> findAllDownloading();

  List<DownloadInfo> findAllDownloaded();
  List<DownloadInfo> findAllDownloadFailure();

  DownloadInfo findDownloadedInfoById(int id);

  void pauseAllDownloading();

  void createOrUpdate(DownloadInfo downloadInfo);

  void createOrUpdate(DownloadThreadInfo downloadThreadInfo);

  void delete(DownloadInfo downloadInfo);

  void delete(DownloadThreadInfo download);
}
