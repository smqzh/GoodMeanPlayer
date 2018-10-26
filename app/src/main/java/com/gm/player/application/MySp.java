package com.gm.player.application;

import com.gm.player.util.YcSpUtil;

public class MySp {


    //本地端口
    public static void setLocalPort(int port){
        YcSpUtil.getInstance().putInt("LocalPort",port);
    }
    public static int getLocalPort(){
     return  YcSpUtil.getInstance().getInt("LocalPort",8801);
    }

    //远程端口
    public static void setRemotePort(int port){
        YcSpUtil.getInstance().putInt("RemotePort",port);
    }
    public static int getRemotePort(){
        return  YcSpUtil.getInstance().getInt("RemotePort",8999);
    }


    //服务器Ip地址
    public static void setServerIP(String  IP){
        YcSpUtil.getInstance().putString("ServerIp",IP);
    }
    public static String getServerIP(){
        return YcSpUtil.getInstance().getString("ServerIp","192.168.1.197");
    }

    //屏保类型
    public static void setScreenType(String  type){
        YcSpUtil.getInstance().putString("ScreenType",type);
    }
    public static String getScreenType(){
        return  YcSpUtil.getInstance().getString("ScreenType","图片");
    }

    //屏保时间
    public static void setScreenTime(int  time){
        YcSpUtil.getInstance().putInt("ScreenTime",time);
    }
    public static int getScreenTime(){
        return YcSpUtil.getInstance().getInt("ScreenTime",5);
    }

    //屏保文件
    public static void setScreenUrl(String  url){
        YcSpUtil.getInstance().putString("ScreenUrl",url);
    }
    public static String  getScreenUrl(){
        return   YcSpUtil.getInstance().getString("ScreenUrl");
    }


    public static void setDefaultUrl(String info){
        YcSpUtil.getInstance().putString("DefaultUrl",info);
    }

    public static String getDefaultUrl(){
        return   YcSpUtil.getInstance().getString("DefaultUrl");
    }

    //设备编号
    public static void setDeviceNumber(String  number){
        YcSpUtil.getInstance().putString("DeviceNumber",number);
    }

    public static String getDeviceNumber(){
        return  YcSpUtil.getInstance().getString("DeviceNumber","小米TV");
    }

    //视频是否拉伸
    public static void setVideoNormal(int  number){
        YcSpUtil.getInstance().putInt("VideoNormal",number);
    }

    public static int getVideoNormal(){
        return  YcSpUtil.getInstance().getInt("VideoNormal",0);
    }

    //横竖屏切换 0为横屏，1为竖屏
    public static void setScreenState(int  number){
        YcSpUtil.getInstance().putInt("SCREENSTATE",number);
    }

    public static int getScreenState(){
        return  YcSpUtil.getInstance().getInt("SCREENSTATE",0);
    }

    //横竖屏切换 0为横屏，1为竖屏
    public static void setRotation(int  number){
        YcSpUtil.getInstance().putInt("ROTATION",number);
    }

    public static int getRotation(){
        return  YcSpUtil.getInstance().getInt("ROTATION",0);
    }
}
