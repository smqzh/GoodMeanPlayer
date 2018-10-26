package com.learning.downloader.event;

public class DownloadSuccessEvent {
    public String type;//0 代表成功 1代表失败
     public String fileId;
    public DownloadSuccessEvent(String type,String fileId) {
        this.type = type;
        this.fileId=fileId;
    }
}
