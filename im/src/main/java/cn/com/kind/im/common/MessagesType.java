package cn.com.kind.im.common;

/**
 * Created by Administrator on 2017/12/29 0029.
 */

public enum MessagesType {

    SHORT_MESSAGE(100),   //短信消息
     VOICE_MESSAGE(101),  //语音消息
    IMAGE_MESSAGE(101),   //图片消息
    FILE_MESSAGE(200),     //文件消息
    LOCATION_MESSAGE(300), //定位消息
    NO_MESSAGE(400),     //没有消息
    QUERY_USER(500);     //查询用户

    private int type;

    private MessagesType(int type) {
        this.type = type;
    }

    public final int getType() {
        return this.type;
    }

    public static MessagesType getMessageTypeByCode(int code) {
        MessagesType[] arr$;
        int len$ = (arr$ = values()).length;

        for(int i$ = 0; i$ < len$; ++i$) {
            MessagesType type;
            if((type = arr$[i$]).getType() == code) {
                return type;
            }
        }

        return null;
    }


}
