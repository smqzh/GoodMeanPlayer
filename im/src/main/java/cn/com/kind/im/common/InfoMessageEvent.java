package cn.com.kind.im.common;

public class InfoMessageEvent {


    public static String VIDEO="video";
    public static String IMAGE="image";
    public static String WEB="web";
    public static String OFFICE="office";
    public static String FILES="file";
    public static String ZUHE="zuhe";
    public static String HTTPURL="http";
    public static String DELETE="delete";

    public String message;
    public String type;//type为video时
    public String defaultTime;//轮播时间
    public String isdefault;//是否为默认轮播文件 0则为是,1则为否
    public InfoMessageEvent(String type, String message, String defaultTime, String isdefault) {
        this.message = message;
        this.type=type;
        this.defaultTime=defaultTime;
        this.isdefault=isdefault;
    }

    public InfoMessageEvent(String type, String message) {
        this.message = message;
        this.type = type;
    }

    public InfoMessageEvent(String type, String message, String defaultTime) {
        this.message = message;
        this.type = type;
        this.defaultTime = defaultTime;
    }
}
