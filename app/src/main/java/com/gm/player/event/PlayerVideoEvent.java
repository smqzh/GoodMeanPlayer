package com.gm.player.event;

public class PlayerVideoEvent {

    public static String PAUSE="pause";
    public static String RESET="reset";
    public static String START="start";
    public static String RESETVIDEO="resetvideo";
    public String message;

    public PlayerVideoEvent(String message) {
        this.message = message;
    }
}
