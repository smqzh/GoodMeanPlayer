package com.gm.player.event;

public class NotificatFinishEvent {

       private String iType; //0 代表图片 1代表视频 2代表网页 3代表PPT
       private String path;
       private int flag;
       private int action;
       private int zsize;
       private int time;
       private String isDefault;

    public String getiType() {
        return iType;
    }

    public void setiType(String iType) {
        this.iType = iType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }


    public int getZsize() {
        return zsize;
    }

    public void setZsize(int zsize) {
        this.zsize = zsize;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public NotificatFinishEvent() {
    }


    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public NotificatFinishEvent(String iType, String path, int flag, int action, int zsize) {
        this.iType = iType;
        this.path = path;
        this.flag = flag;
        this.action = action;
        this.zsize = zsize;
    }

    public NotificatFinishEvent(String iType, String path, int flag, int action, int zsize, int time) {
        this.iType = iType;
        this.path = path;
        this.flag = flag;
        this.action = action;
        this.zsize = zsize;
        this.time = time;
    }

    public NotificatFinishEvent(String iType, String path, int flag, int action, int zsize, int time, String isDefault) {
        this.iType = iType;
        this.path = path;
        this.flag = flag;
        this.action = action;
        this.zsize = zsize;
        this.time = time;
        this.isDefault = isDefault;
    }
}
