package cn.com.kind.im.framework.model;


import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import cn.com.kind.im.utils.ByteBufUtils;
import cn.com.kind.im.utils.SerializeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ChatMsg {

    private int ChatBoxContentLen;
    private Map<Integer, Integer> EmotionMap;
    private Map<Integer, byte[]> ForeignImageMap;
    private int R;
    private int G;
    private int B;
    private int FontLen = -1;
    private String Text;
    private byte[] TextBytes;

    public ChatMsg() {
        this.EmotionMap = new HashMap<>();
        this.ForeignImageMap = new HashMap<>();
    }

    public ChatMsg(Map<Integer, Integer> emotionMap, Map<Integer, byte[]> foreignImageMap, String text) {
        this.EmotionMap = emotionMap;
        this.ForeignImageMap = foreignImageMap;
        this.Text = text;
        try {
            this.TextBytes = text.getBytes("utf-8");
        } catch (Exception ee) {

        }
    }

    public Map<Integer, Integer> getEmotionMap() {
        return EmotionMap;
    }

    public void setEmotionMap(Map<Integer, Integer> emotionMap) {
        EmotionMap = emotionMap;
    }

    public Map<Integer, byte[]> getForeignImageMap() {
        return ForeignImageMap;
    }

    public void setForeignImageMap(Map<Integer, byte[]> foreignImageMap) {
        ForeignImageMap = foreignImageMap;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
        try {
            this.TextBytes = text.getBytes("utf-8");
        } catch (Exception ee) {

        }
    }

    public void deserialize(byte[] bytes) throws IOException {
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes).order(ByteOrder.LITTLE_ENDIAN);

        this.ChatBoxContentLen = buffer.readInt();
        this.R = buffer.readByte();
        this.G = buffer.readByte();
        this.B = buffer.readByte();
        int emotionCount = buffer.readInt();
        EmotionMap = new HashMap<Integer, Integer>();
        if (emotionCount > 0) {
            for (int i = 0; i < emotionCount; i++) {
                this.EmotionMap.put(buffer.readInt(), buffer.readInt());
            }
        }

        this.FontLen = buffer.readInt();
        if (this.FontLen > 0) {
            byte[] fontBytes = new byte[this.FontLen];
            buffer.readBytes(fontBytes);
        }

        this.ForeignImageMap = new HashMap<Integer, byte[]>();
        int foreignImageCount = buffer.readInt();
        if (foreignImageCount > 0) {
            for (int i = 0; i < foreignImageCount; i++) {
                int key = buffer.readInt();
                int valueLen = buffer.readInt();
                byte[] value = new byte[valueLen + 1];//第一个字节表示其是否为gif
                buffer.readBytes(value);
                this.ForeignImageMap.put(key, value);
            }
        }

        this.Text = SerializeUtils.readStrIntLen(buffer);
    }

    public byte[] serialize() throws Exception {
        ByteBuf body = ByteBufUtils.newBuffer();

        body.writeInt(ChatBoxContentLen);
        body.writeByte(this.R);
        body.writeByte(this.G);
        body.writeByte(this.B);
        ChatBoxContentLen = 3;
        ChatBoxContentLen += 4;
        if (this.EmotionMap == null) {
            body.writeInt(0);
        } else {
            body.writeInt(EmotionMap.size());

            for (Map.Entry<Integer, Integer> entry : EmotionMap.entrySet()) {
                body.writeInt(entry.getKey());
                body.writeInt(entry.getValue());
            }
            ChatBoxContentLen += EmotionMap.size() * (4 + 4);
        }

        body.writeInt(this.FontLen);
        ChatBoxContentLen += 4;
        ChatBoxContentLen += 4;
        if (this.ForeignImageMap == null) {
            body.writeInt(0);
        } else {
            body.writeInt(ForeignImageMap.size());
            for (Map.Entry<Integer, byte[]> entry : ForeignImageMap.entrySet()) {

                body.writeInt(entry.getKey());
                body.writeInt(entry.getValue().length);
                body.writeByte(0);//是否是gif
                body.writeBytes(entry.getValue());

                ChatBoxContentLen += 4 + 4 + entry.getValue().length + 1;
            }
        }

        ChatBoxContentLen += 4;
        if (this.Text == null) {
            body.writeInt(-1);
        } else {
            body.writeInt(TextBytes.length);
            body.writeBytes(TextBytes);
            ChatBoxContentLen += TextBytes.length;
        }
        body.resetWriterIndex();
        body.writeInt(ChatBoxContentLen);

        return body.array();
    }
}
