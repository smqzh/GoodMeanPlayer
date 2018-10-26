package cn.com.kind.im.framework.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;

import cn.com.kind.im.utils.ByteBufUtils;
import cn.com.kind.im.utils.SerializeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Administrator on 2018/1/2 0002.
 */

public class ReturnMsg extends BaseMsg {

    private int code;
    private String key;
    private String content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] serilize() throws UnsupportedEncodingException {

        ByteBuf byteBuf= ByteBufUtils.newBuffer();
        byteBuf.writeByte(this.code);
        byteBuf.writeInt(this.key.getBytes("UTF-8").length);
        byteBuf.writeBytes(this.key.getBytes("UTF-8"));
        byteBuf.writeInt(this.content.getBytes("UTF-8").length);
        byteBuf.writeBytes(this.content.getBytes("UTF-8"));
        return byteBuf.array();
    }


    public void deserilize(byte[] bytes) throws IOException {

        ByteBuf byteBuf= Unpooled.wrappedBuffer(bytes).order(ByteOrder.LITTLE_ENDIAN);;
        this.code=byteBuf.readByte();
        this.key= SerializeUtils.readStrIntLen(byteBuf);
        this.content= SerializeUtils.readStrIntLen(byteBuf);
    }




}
