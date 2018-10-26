package com.gm.player.db;

import android.content.ContentValues;

import org.apache.poi.poifs.filesystem.DocumentNode;
import org.litepal.crud.DataSupport;

import java.util.List;

public class DownLoadFiles extends DataSupport {

    private String fileId;
    private String url;
    private String fname;
    private String fpath;
    private int status; //0未开始，1下载中，2下载成功，3下载失败
    private boolean down; //是否下载
    private boolean downloadError; //是否下载出错
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFpath() {
        return fpath;
    }

    public void setFpath(String fpath) {
        this.fpath = fpath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isDownloadError() {
        return downloadError;
    }

    public void setDownloadError(boolean downloadError) {
        this.downloadError = downloadError;
    }

    public DownLoadFiles() {
    }

    public DownLoadFiles(String fileId, String url, String fname, String fpath, int status, boolean down, boolean downloadError) {
        this.fileId = fileId;
        this.url = url;
        this.fname = fname;
        this.fpath = fpath;
        this.status = status;
        this.down = down;
        this.downloadError = downloadError;
    }

    //保存所有下载文件
    public static void insertFiles(List<DownLoadFiles> files){
        DataSupport.saveAll(files);
    }


    public static List<DownLoadFiles> queryFiles(String key, String value){
        return   DataSupport.where(key+"= ?",value).find(DownLoadFiles.class);
    }
    public static DownLoadFiles queryFilesBy(String key, String value){
        return   DataSupport.where(key+"= ?",value).findFirst(DownLoadFiles.class);
    }
    public static List<DownLoadFiles> queryFiles(String key1, String value1,String key2, String value2){
        return   DataSupport.where(key1+"= ? and "+key2+" = ?",value1,value2).find(DownLoadFiles.class);
    }
    public static List<DownLoadFiles> queryFiles(){
        return  DataSupport.findAll(DownLoadFiles.class);
    }

    public static boolean queryBoolean(String key, String value){
        DownLoadFiles df= queryFilesBy(key,value);
        if(df!=null){
            return true;
        }else{
            return false;
        }
    }

    public static boolean deleteBykey(String key,String value){
        int fla=  DataSupport.deleteAll(DownLoadFiles.class,key+" = ?",value);
        if(fla>0){
            return true;
        }else {
            return false;
        }
    }

    public static boolean deleteBykey(long id){
        int fla=  DataSupport.delete(DownLoadFiles.class,id);
        if(fla>0){
            return true;
        }else {
            return false;
        }
    }


    public static boolean updateBykey(String key,int val,String fileId){
        ContentValues values = new ContentValues();
        values.put(key, val);
        int fla=DataSupport.updateAll(DownLoadFiles.class, values, "fileId = ?", fileId);
        if(fla>0){
            return true;
        }else {
            return false;
        }
    }

}
