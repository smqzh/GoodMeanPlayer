package cn.com.kind.im.utils;

import java.nio.ByteOrder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Administrator on 2017/12/29 0029.
 */

public class ByteBufUtils {

    public ByteBufUtils() {
    }

    public static ByteBuf newBuffer() {
        return Unpooled.buffer(128).order(ByteOrder.LITTLE_ENDIAN);
    }

    public static ByteBuf newBuffer(int buffSize) {
        return Unpooled.buffer(buffSize).order(ByteOrder.LITTLE_ENDIAN);
    }

    public static ByteBuf wrappedBuffer(ByteBuf... bufs) {
        return Unpooled.wrappedBuffer(bufs);
    }

    public static ByteBuf wrappedBuffer(byte[] bufs) {
        return Unpooled.wrappedBuffer(bufs).order(ByteOrder.LITTLE_ENDIAN);
    }

}
