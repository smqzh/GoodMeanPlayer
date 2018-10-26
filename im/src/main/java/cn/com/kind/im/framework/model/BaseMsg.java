package cn.com.kind.im.framework.model;

/**
 * Created by Administrator on 2018/1/2 0002.
 */

public class BaseMsg {

    public String userId;
    public String targetId;

    public String getUserId() {
        return userId;
    }

    public BaseMsg setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getTargetId() {
        return targetId;
    }

    public BaseMsg setTargetId(String targetId) {
        this.targetId = targetId;
        return this;
    }



}
