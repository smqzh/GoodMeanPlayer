package cn.com.kind.im.utils;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

/**
 * Created by Administrator on 2017/12/29 0029.
 */

public class SerializeUtils {

    public SerializeUtils() {
    }

    public static void writeDataWithIntLen(ByteBuf buffer, byte[] data) {
        buffer.writeInt(data.length);
        buffer.writeBytes(data);
    }

    public static void writeDataWithByteLen(ByteBuf buffer, byte[] data) {
        buffer.writeByte(data.length);
        buffer.writeBytes(data);
    }

    public static void writeDataWithByteLenFillZeo(ByteBuf buffer, byte[] data, int len) {
        buffer.writeByte(data.length);
        buffer.writeBytes(data);

        for(int writed = data.length; writed < len; ++writed) {
            buffer.writeByte(0);
        }

    }

    public static String readStrIntLen(ByteBuf buffer) throws IOException {
        int len;
        if((len = buffer.readInt()) <= 0) {
            return "";
        } else {
            byte[] arrayOfByte = new byte[len];
            buffer.readBytes(arrayOfByte);
            return new String(arrayOfByte, "utf-8");
        }
    }

    public static String readStrByteLenFixLen(ByteBuf buffer, byte len) throws IOException {
        byte realLen;
        if((realLen = buffer.readByte()) <= 0) {
            return "";
        } else {
            byte[] data = new byte[len];
            buffer.readBytes(data);
            return new String(data, 0, realLen, "utf-8");
        }
    }

    public static byte[] readByLen(ByteBuf buffer, int len) throws IOException {

        byte[] data = new byte[len];
        buffer.readBytes(data);
        return data;

    }



    public static byte[] readByteByInt(ByteBuf buffer) throws IOException {
        int len;
        if((len = buffer.readInt()) <= 0) {
            return null;
        } else {
            byte[] arrayOfByte = new byte[len];
            buffer.readBytes(arrayOfByte);
            return arrayOfByte;
        }
    }
}
