package com.gm.player.bean;

public class FileInfo {

    private String url;
    private String uName;
    private int type; //0是图片，1是word,2是PPT，3是xls，4是PDF，5是视频

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public FileInfo(String url, String uName, int type) {
        this.url = url;
        this.uName = uName;
        this.type = type;
    }

}
