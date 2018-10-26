package cn.com.kind.im.common;

/**
 * Created by Administrator on 2017/12/29 0029.
 */

public enum  ClientType {
    DOT_NET(0),
    SILVER_LIGHT(1),
    WINDOWS_PHONE(2),
    FLASH(3),
    IOS(4),
    ANDROID(5),
    OTHER(6);

    private int type;

    private ClientType(int type) {
        this.type = type;
    }

    public final int getType() {
        return this.type;
    }
}
