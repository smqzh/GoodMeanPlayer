package com.gm.player.util;

public class StringUtil {

    public static boolean isEmptyorNull(String str){
        if(str==null)
            return true;
        if(str.equals(""))
            return true;
        return false;
    }
}
