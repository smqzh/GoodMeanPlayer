package com.gm.player.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gm.player.ui.activity.WelcomeActivity;

public class AutoStartBroadcastReceiver  extends BroadcastReceiver {
    static final String action_boot ="android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action_boot)){
            Intent sayHelloIntent=new Intent(context,WelcomeActivity.class);
            sayHelloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(sayHelloIntent);
        }
    }

}
