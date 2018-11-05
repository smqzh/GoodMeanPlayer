package com.gm.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gm.player.application.Constant;
import com.gm.player.application.MySp;
import com.gm.player.util.StringUtil;
import com.gm.player.util.ThreadPoolManager;
import com.gm.player.util.Tools;
import com.gm.player.util.YcToast;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;

import cn.com.kind.im.common.InfoMessageEvent;
import cn.com.kind.im.core.SocketClient;
import cn.com.kind.im.framework.HeartEngine;
import cn.com.kind.im.helper.SocketClientAddress;
import cn.com.kind.im.helper.SocketClientDelegate;
import cn.com.kind.im.helper.SocketClientReceivingDelegate;
import cn.com.kind.im.helper.SocketClientSendingDelegate;
import cn.com.kind.im.helper.SocketPacket;
import cn.com.kind.im.helper.SocketResponsePacket;


/**
 * Created by Administrator on 2018/1/2 0002.
 */

public class SuperSocket extends Service implements HeartEngine {

    private String TAG=this.getClass().getName();
    private SocketClient socket;

    public String from;
    private boolean flagBrocast=true;

    //是否有网络标志
    public boolean mark=true;
    // private NetReceiver netChange;
    //判断是否连接
    public boolean isConn=true;
    String Code="UTF-8";

    public boolean isFlag=true;

    private runSocket runSocket;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SocketBinders();
    }

    //发送心跳包
    public void sendHeartBeat(){
      //  Log.d(TAG, "sendHeartBeat: -----发送心跳包");
        socket.getHeartBeatHelper().setHeartBeatInterval(8 * 1000);
        socket.getHeartBeatHelper().setSendHeartBeatEnabled(true); // 设置允许自动发送心跳包，此值默认为false
        String num= MySp.getDeviceNumber()+"TT~TT0TT~TT300TT~TT心跳包XX~XX";
        byte[] data=num.getBytes();
        socket.getHeartBeatHelper().setDefaultSendData(data);
        socket.sendHeartPackage();

    }


    public void initRegister() {
        String data=MySp.getDeviceNumber()+"TT~TT0TT~TT10TT~TT连接服务器XX~XX"+MySp.getDeviceNumber()+"TT~TT0TT~TT301TT~TT请求FTP配置XX~XX";
        sendMessage(data);
    }

    //发送聊天信息
    public void sendMessage( String msg) {
        byte[] data=msg.getBytes();
        sendDataMessage(data);
    }



    //********************************************************以下为私有内容********************************************
    //发送消息
    private void sendDataMessage(byte[] bs){
        if(isConn) {
            sendMessage(bs);
        }
    }

    //网络断开
    public void shutDown(){ release();}

    //网络重新连接
    public void reSetService(){
        initSocket();
    }


    public class SocketBinders extends Binder {
        /*返回SocketService 在需要的地方可以通过ServiceConnection获取到SocketService  */
        public SuperSocket getService() {
            return SuperSocket.this;
        }
    }

    public void resetConnect(){
        isConn=false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initSocket();
            }
        },5000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*拿到传递过来的ip和端口号*/
      //  ip = MySp.getServerIP();
      //  port =  MySp.getRemotePort();
        /*初始化socket*/
        initSocket();
        return super.onStartCommand(intent, flags, startId);
    }

    private class runSocket implements Runnable{

        private boolean flag;
        private SocketClient sc;

        public SocketClient getSc() {
            return sc;
        }

        public void setSc(SocketClient sc) {
            this.sc = sc;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        @Override
        public void run()  {
            relaseSocket(sc,flag);
        }
    }


    private void initSocket(){
        connectSocket();
        registerSocket();
    }

    public void relaseSocket(SocketClient client,boolean flag){
        if(client!=null){
            client.disconnect();
        }
        if(!flag){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initSocket();
        }
    }


    /*
     *  线程连接Socket
     * */
    private void connectSocket(){
        Log.d(TAG, "connectSocket: ----连接服务端");
        SocketClientAddress address=new SocketClientAddress( MySp.getServerIP(),MySp.getRemotePort());
        socket=new SocketClient(address);
        socket.connect();
        // socket.set(1000 * 60);
        socket.getAddress().setConnectionTimeout(15 * 1000); // 连接超时时长，单位毫秒
        isConn=true;
    }


    //注册回调信息
    private void registerSocket(){

     //   Log.d(TAG, "registerSocket: ------注册回调信息");
        socket.registerSocketClientDelegate(new SocketClientDelegate() {
            /**
             * 连接上远程端时的回调
             */
            @Override
            public void onConnected(SocketClient client) {
                socket=client;
                sendHeartBeat();
                initRegister();
                Log.d(TAG, "onConnected: ----连接成功");
              //  YcToast.get().toast("连接成功");
            }

            /**
             * 与远程端断开连接时的回调
             */
            @Override
            public void onDisconnected(SocketClient client) {
                isFlag=false;
                   Log.d(TAG, "onDisconnected: ----已离线");
                //  socket.disconnect();
                if(isConn) {
                    if (runSocket != null) {
                        ThreadPoolManager.getInstance().remove(runSocket);
                    }
                    runSocket = new runSocket();
                    runSocket.setFlag(isFlag);
                    runSocket.setSc(client);
                    ThreadPoolManager.getInstance().execute(runSocket);
                }else{
                    client.disconnect();
                }
            }

            /**
             * 接收到数据包时的回调
             */
            @Override
            public void onResponse(final SocketClient client, @NonNull SocketResponsePacket responsePacket) {
                byte[] data = responsePacket.getData(); // 获取接收的byte数组，不为null
                // String message = responsePacket.getMessage(); // 获取按默认设置的编码转化的String，可能为null

            }
        });


        //发送状态回调配置
        socket.registerSocketClientSendingDelegate(new SocketClientSendingDelegate() {
            /**
             * 数据包开始发送时的回调
             */
            @Override
            public void onSendPacketBegin(SocketClient client, SocketPacket packet) {
                //  Log.d(TAG, "onSendPacketBegin: ----------start");
            }

            /**
             * 数据包取消发送时的回调
             * 取消发送回调有以下情况：
             * 1. 手动cancel仍在排队，还未发送过的packet
             * 2. 断开连接时，正在发送的packet和所有在排队的packet都会被取消
             */
            @Override
            public void onSendPacketCancel(SocketClient client, SocketPacket packet) {

            }

            /**
             * 数据包发送的进度回调
             * progress值为[0.0f, 1.0f]
             * 通常配合分段发送使用
             * 可用于显示文件等大数据的发送进度
             */
            @Override
            public void onSendingPacketInProgress(SocketClient client, SocketPacket packet, float progress, int sendedLength) {
            }

            /**
             * 数据包完成发送时的回调
             */
            @Override
            public void onSendPacketEnd(SocketClient client, SocketPacket packet) {
                //  Log.d(TAG, "onSendPacketEnd: ------end");

            }
        });


        //接收回调配置
        socket.registerSocketClientReceiveDelegate(new SocketClientReceivingDelegate() {

            /**
             * 开始接受一个新的数据包时的回调
             */
            @Override
            public void onReceivePacketBegin(SocketClient client, SocketResponsePacket packet) {
                //    Log.d(TAG, "onReceivePacketBegin: --------start");
            }

            /**
             * 完成接受一个新的数据包时的回调
             */
            @Override
            public void onReceivePacketEnd(SocketClient client, SocketResponsePacket packet) {
             //   Log.d(TAG, "onReceivePacketEnd: -----end");
                Message msg=new Message();
                msg.obj=packet.getData();
                msg.what=0;
                mHandler.sendMessage(msg);
            }

            /**
             * 取消接受一个新的数据包时的回调
             * 在断开连接时会触发
             */
            @Override
            public void onReceivePacketCancel(SocketClient client, SocketResponsePacket packet) {

            }

            /**
             * 接受一个新的数据包的进度回调
             * progress值为[0.0f, 1.0f]
             * 仅作用于ReadStrategy为AutoReadByLength的自动读取
             * 因AutoReadByLength可以首先接受到剩下的数据包长度
             */
            @Override
            public void onReceivingPacketInProgress(SocketClient client, SocketResponsePacket packet, float progress, int receivedLength) {


            }
        });
    }

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0:
                    dispatcherTask((byte[])msg.obj);
                    break;
            }
        }
    };






    // 发送数据任务
    private  void sendMessage(byte[] message) {
        if (socket != null &&socket.isConnected()) {
            sendData(message);
        }
    }

    /**
     *  发送数据的方法
     * */
    private  void sendData(byte[] bs){
        socket.sendData(bs);
    }

    private void dispatcherTask(byte[] data){
        String msg= null;
        try {
            msg = new String(data,Code);
            if(!StringUtil.isEmptyorNull(msg)){
                String[] tt=msg.split("TT~TT");
                int length=tt.length;
                if(length==5){
                    String[] ht=tt[4].split(",");
                    String obj="";
                    if(ht.length==1){
                        obj=tt[4].replaceAll("XX~XX","");
                    }else{
                        obj=tt[4].replaceAll(",XX~XX","");
                    }
                    if(!obj.equals("")){
                        InfoMessageEvent info=null;
                     //   Log.d(TAG, "dispatcherTask: ------tt3"+tt[3]+"-----obj"+obj);
                        if(tt[3].equals("Default")||tt[3].equals("default")){
                            info=new InfoMessageEvent("zuhe",obj,"0","0");
                        }else{
                            info=new InfoMessageEvent("zuhe",obj,"0","1");
                        }

                        EventBus.getDefault().post(info);
                    }
                }
                else if(length==4){
                    if(tt[0].equals(MySp.getDeviceNumber())){
                        return;
                    }
                    InfoMessageEvent info=null;
                    if(Tools.isDigit(tt[2])){
                        if(tt[2].equals("8062")) {
                            info = new InfoMessageEvent("video", "restart");
                            EventBus.getDefault().post(info);
                        }
                        else if(tt[2].equals("403")){
                            info = new InfoMessageEvent("video", "noVol");//静音
                            EventBus.getDefault().post(info);
                        }
                        else if(tt[2].equals("404")){
                            info = new InfoMessageEvent("video", "autoVol");//关闭静音
                            EventBus.getDefault().post(info);
                        }
                        else if(tt[2].equals("402")){
                            info = new InfoMessageEvent("video", "volDes");//-音
                            EventBus.getDefault().post(info);
                        }
                        else if(tt[2].equals("401")){
                            info = new InfoMessageEvent("video", "volAdd");//+音
                            EventBus.getDefault().post(info);
                        }else if(tt[2].equals("6002")) {
                            info = new InfoMessageEvent("video", "start");
                            EventBus.getDefault().post(info);
                        }else if(tt[2].equals("6001")) {
                            info = new InfoMessageEvent("video", "pause");
                            EventBus.getDefault().post(info);
                        }

                    }
                } else if(length==7){
                    InfoMessageEvent info=null;
                    if(Tools.isDigit(tt[2])){
                        if(tt[5].equals("6002")) {
                            info = new InfoMessageEvent("video", "start");
                            EventBus.getDefault().post(info);
                        }else if(tt[5].equals("6001")) {
                            info = new InfoMessageEvent("video", "pause");
                            EventBus.getDefault().post(info);
                        }
                        else if(tt[2].equals("8059")) {
                            info = new InfoMessageEvent("video", "volAdd");//+音
                            EventBus.getDefault().post(info);
                        }else if(tt[2].equals("8060")) {
                            info = new InfoMessageEvent("video", "volDes");//+音
                            EventBus.getDefault().post(info);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//
//    private class NetReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {//如果没有网络
//                //    Log.d(TAG, "onReceive: service--------没有网络");
//                release();
//                mark = false;
//            } else if (mobNetInfo.isConnected() || wifiNetInfo.isConnected()) {//如果有网络
//                //  Log.d(TAG, "onReceive: service---------有网络");
//                if (!mark) {
//                    initSocket();
//                }
//                mark = true;
//            }
//        }
//    }

    /*
     * 释放资源
     * */
    public void release(){
        isConn=false;
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }

    }

    /*
     * 销毁
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // if(flagBrocast){
        //   unregisterReceiver(netChange);
        // }
        release();
    }

}
