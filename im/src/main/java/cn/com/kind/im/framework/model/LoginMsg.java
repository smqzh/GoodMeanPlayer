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

public class LoginMsg extends BaseMsg {

    private String username;
    private String pwd;

    public String getUsername() {
        return username;
    }

    public LoginMsg setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPwd() {
        return pwd;
    }

    public LoginMsg setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }

    public byte[] serilize() throws UnsupportedEncodingException {
        ByteBuf byteBuf= ByteBufUtils.newBuffer();
        byteBuf.writeInt(this.username.getBytes("UTF-8").length);
        byteBuf.writeBytes(this.username.getBytes("UTF-8"));
        byteBuf.writeInt(this.pwd.getBytes("UTF-8").length);
        byteBuf.writeBytes(this.pwd.getBytes("UTF-8"));
        return byteBuf.array();
    }

    public LoginMsg deserilize(byte[] bs) throws IOException {
        ByteBuf byteBuf= Unpooled.wrappedBuffer(bs).order(ByteOrder.LITTLE_ENDIAN);
        this.username=SerializeUtils.readStrIntLen(byteBuf);
        this.pwd=SerializeUtils.readStrIntLen(byteBuf);
        return new LoginMsg().setUsername(username).setPwd(pwd);
    }

}
