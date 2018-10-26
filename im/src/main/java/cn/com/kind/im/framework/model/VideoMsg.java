package cn.com.kind.im.framework.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;

import cn.com.kind.im.utils.ByteBufUtils;
import cn.com.kind.im.utils.SerializeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Administrator on 2018/2/9 0009.
 */

public class VideoMsg {

    private String content;
    private String pushUrl;
    private String receiveUrl;

    public VideoMsg() {
    }

    public String getContent() {
        return content;
    }

    public VideoMsg setContent(String content) {
        this.content = content;
        return this;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public VideoMsg setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
        return this;
    }

    public String getReceiveUrl() {
        return receiveUrl;
    }

    public VideoMsg setReceiveUrl(String receiveUrl) {
        this.receiveUrl = receiveUrl;
        return this;
    }

    public byte[] serilize() throws UnsupportedEncodingException {

        ByteBuf byteBuf= ByteBufUtils.newBuffer();

        byteBuf.writeInt(this.pushUrl.getBytes("UTF-8").length);
        byteBuf.writeBytes(this.pushUrl.getBytes("UTF-8"));
        byteBuf.writeInt(this.receiveUrl.getBytes("UTF-8").length);
        byteBuf.writeBytes(this.receiveUrl.getBytes("UTF-8"));
        byteBuf.writeInt(this.content.getBytes("UTF-8").length);
        byteBuf.writeBytes(this.content.getBytes("UTF-8"));
        return byteBuf.array();
    }

    public void deserilize(byte[] bytes) throws IOException {
        ByteBuf byteBuf= Unpooled.wrappedBuffer(bytes).order(ByteOrder.LITTLE_ENDIAN);;
        this.pushUrl= SerializeUtils.readStrIntLen(byteBuf);
        this.receiveUrl= SerializeUtils.readStrIntLen(byteBuf);
        this.content= SerializeUtils.readStrIntLen(byteBuf);
    }

}
