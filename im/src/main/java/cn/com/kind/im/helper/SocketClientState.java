package cn.com.kind.im.helper;

import android.support.annotation.NonNull;

import cn.com.kind.im.core.SocketClient;


/**
 * SocketClientDelegate
 * Created by vilyever on 2016/5/30.
 * Feature:
 */
public interface SocketClientState {
    void onConnected(SocketClient client);
    void onDisconnected(SocketClient client);
    void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket);

    class SimpleSocketClientDelegate implements SocketClientState {
        @Override
        public void onConnected(SocketClient client) {

        }

        @Override
        public void onDisconnected(SocketClient client) {

        }

        @Override
        public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket) {

        }
    }
}
