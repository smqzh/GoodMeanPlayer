package com.learning.downloader.event;

public class DownloadProgressEvent {

    public String fileid;
    public long progress;
    public long size;
    public DownloadProgressEvent(String fileid,long progress,long size) {
        this.fileid = fileid;
        this.progress=progress;
        this.size=size;
    }

}
