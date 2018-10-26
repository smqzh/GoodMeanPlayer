package cn.com.kind.im.framework.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;

import cn.com.kind.im.utils.ByteBufUtils;
import cn.com.kind.im.utils.SerializeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Administrator on 2018/1/3 0003.
 */

public class LogoutMsg extends BaseMsg {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] serilize() throws UnsupportedEncodingException {
        ByteBuf byteBuf= ByteBufUtils.newBuffer();
        byteBuf.writeInt(this.username.getBytes("UTF-8").length);
        byteBuf.writeBytes(this.username.getBytes("UTF-8"));
        return byteBuf.array();
    }

    public void deserilize(byte[] bs) throws IOException {
        ByteBuf byteBuf= Unpooled.wrappedBuffer(bs).order(ByteOrder.LITTLE_ENDIAN);
        this.username= SerializeUtils.readStrIntLen(byteBuf);
    }


}
