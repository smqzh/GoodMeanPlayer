package com.gm.player.bean;

public class LoopBean {

      private int type;//0是图片，1是网页，2是视频，3是ppt
     private String url;
     private int time;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public LoopBean() {
    }

    public LoopBean(int type, String url, int time) {
        this.type = type;
        this.url = url;
        this.time = time;
    }

}
