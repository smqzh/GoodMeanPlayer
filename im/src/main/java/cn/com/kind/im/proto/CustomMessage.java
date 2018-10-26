package cn.com.kind.im.proto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.Arrays;

import cn.com.kind.im.common.ClientType;
import cn.com.kind.im.utils.ByteBufUtils;
import cn.com.kind.im.utils.SerializeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Administrator on 2017/12/29 0029.
 */

public class CustomMessage {

    private String userId;
    private String targetId;
    private byte[]  content;
    private int messageType;
    private int clientType;
    private byte k;
    private String tag;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public byte getK() {
        return k;
    }

    public void setK(byte k) {
        this.k = k;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public CustomMessage(String userId, String targetId, byte[] content, int messageType) {
        this.userId = userId;
        this.targetId = targetId;
        this.content = content;
        this.messageType = messageType;
    }

    public CustomMessage() {
    }


    public CustomMessage(String userId, String targetId, byte[] content, int messageType, byte k) {
        this.userId = userId;
        this.targetId = targetId;
        this.content = content;
        this.messageType = messageType;
        this.k = k;
    }

    public CustomMessage(String userId, String targetId, byte[] content, int messageType,String tag) {
        this.userId = userId;
        this.targetId = targetId;
        this.content = content;
        this.messageType = messageType;
        this.tag = tag;
    }

    public byte[] serilize() throws UnsupportedEncodingException {
        ByteBuf buf= ByteBufUtils.newBuffer();
        buf.writeInt(userId.getBytes("UTF-8").length);
        buf.writeBytes(userId.getBytes("UTF-8"));
        buf.writeInt(targetId.getBytes("UTF-8").length);
        buf.writeBytes(targetId.getBytes("UTF-8"));
        if(content==null){
            buf.writeInt(0);
        }else{
            buf.writeInt(content.length);
            buf.writeBytes(content);
        }
        buf.writeInt(ClientType.ANDROID.getType());
        buf.writeInt(messageType);
        buf.writeInt(tag.getBytes("UTF-8").length);
        buf.writeBytes(tag.getBytes("UTF-8"));
        return buf.array();
    }

    private void deserilizes(ByteBuf byteBuf) throws IOException{
        this.userId= SerializeUtils.readStrIntLen(byteBuf);
        this.targetId= SerializeUtils.readStrIntLen(byteBuf);
        int size=byteBuf.readInt();
        if(size>0){
            this.content=SerializeUtils.readByLen(byteBuf,size);
        }else{
            this.content=null;
        }
        this.clientType=byteBuf.readInt();
        this.messageType=byteBuf.readInt();
        this.tag=SerializeUtils.readStrIntLen(byteBuf);
    }



    public static CustomMessage deserilize(byte[] by){
        ByteBuf byteBuf= Unpooled.wrappedBuffer(by).order(ByteOrder.LITTLE_ENDIAN);

        CustomMessage cm=new CustomMessage();
        try {
            cm.deserilizes(byteBuf);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cm;
    }

    @Override
    public String toString() {
        return "CustomMessage{" +
                "userId='" + userId + '\'' +
                ", targetId='" + targetId + '\'' +
                ", content=" + Arrays.toString(content) +
                ", messageType=" + messageType +
                ", clientType=" + clientType +
                ", k=" + k +
                ", tag='" + tag + '\'' +
                '}';
    }
}
