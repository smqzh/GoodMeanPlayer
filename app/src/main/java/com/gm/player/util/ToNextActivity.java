package com.gm.player.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gm.player.R;
import com.gm.player.event.NotificatFinishEvent;
import com.gm.player.ui.activity.FileDisplayActivity;
import com.gm.player.ui.activity.ImageActivity;
import com.gm.player.ui.activity.PowerPointActivity;
import com.gm.player.ui.activity.VideoActivity;
import com.gm.player.ui.activity.WebActivity;

public class ToNextActivity {

    private static void _ToNextAcy(Intent intent, Activity ay, int type){
        ay.startActivity(intent);
        switch (type) {
            case 0:
                ay.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case 1:
                ay.overridePendingTransition(R.anim.alpha_rotate,
                        R.anim.my_alpha_action);
                break;
            case 2:
                ay.overridePendingTransition(R.anim.alpha_scale_rotate,
                        R.anim.my_alpha_action);
                break;
            case 3:
                ay.overridePendingTransition(
                        R.anim.alpha_scale_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 4:
                ay.overridePendingTransition(R.anim.alpha_scale_translate,
                        R.anim.my_alpha_action);
                break;
            case 5:
                ay.overridePendingTransition(R.anim.alpha_scale,
                        R.anim.my_alpha_action);
                break;
            case 6:
                ay.overridePendingTransition(R.anim.alpha_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 7:
                ay.overridePendingTransition(R.anim.alpha_translate,
                        R.anim.my_alpha_action);
                break;
            case 8:
                ay.overridePendingTransition(R.anim.my_rotate_action,
                        R.anim.my_alpha_action);
                break;
            case 9:
                ay.overridePendingTransition(R.anim.my_scale_action,
                        R.anim.my_alpha_action);
                break;
            case 10:
                ay.overridePendingTransition(R.anim.my_translate_action,
                        R.anim.my_alpha_action);
                break;
            case 11:
                ay.overridePendingTransition(R.anim.myanimation_simple,
                        R.anim.my_alpha_action);
                break;
            case 12:
                ay.overridePendingTransition(R.anim.myown_design,
                        R.anim.my_alpha_action);
                break;
            case 13:
                ay.overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            case 14:
                ay.overridePendingTransition(R.anim.scale_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 15:
                ay.overridePendingTransition(R.anim.scale_translate,
                        R.anim.my_alpha_action);
                break;
            case 16:
                ay.overridePendingTransition(R.anim.translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 17:
                ay.overridePendingTransition(R.anim.hyperspace_in,
                        R.anim.hyperspace_out);
                break;
            case 18:
                ay.overridePendingTransition(R.anim.shake,
                        R.anim.my_alpha_action);
                break;
            case 19:
                ay.overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case 20:
                ay.overridePendingTransition(R.anim.push_up_in,
                        R.anim.push_up_out);
                break;
            case 21:
                ay.overridePendingTransition(R.anim.slide_left,
                        R.anim.slide_right);
                break;
            case 22:
                ay.overridePendingTransition(R.anim.slide_top_to_bottom,
                        R.anim.my_alpha_action);
                break;
            case 23:
                ay.overridePendingTransition(R.anim.wave_scale,
                        R.anim.my_alpha_action);
                break;
        }
    }


    public  static void ToFinishAcy(Activity ay,int type){
        ay.finish();
        switch (type) {
            case 0:
                ay.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case 1:
                ay.overridePendingTransition(R.anim.alpha_rotate,
                        R.anim.my_alpha_action);
                break;
            case 2:
                ay.overridePendingTransition(R.anim.alpha_scale_rotate,
                        R.anim.my_alpha_action);
                break;
            case 3:
                ay. overridePendingTransition(
                        R.anim.alpha_scale_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 4:
                ay.overridePendingTransition(R.anim.alpha_scale_translate,
                        R.anim.my_alpha_action);
                break;
            case 5:
                ay. overridePendingTransition(R.anim.alpha_scale,
                        R.anim.my_alpha_action);
                break;
            case 6:
                ay. overridePendingTransition(R.anim.alpha_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 7:
                ay.overridePendingTransition(R.anim.alpha_translate,
                        R.anim.my_alpha_action);
                break;
            case 8:
                ay.overridePendingTransition(R.anim.my_rotate_action,
                        R.anim.my_alpha_action);
                break;
            case 9:
                ay.overridePendingTransition(R.anim.my_scale_action,
                        R.anim.my_alpha_action);
                break;
            case 10:
                ay.overridePendingTransition(R.anim.my_translate_action,
                        R.anim.my_alpha_action);
                break;
            case 11:
                ay.overridePendingTransition(R.anim.myanimation_simple,
                        R.anim.my_alpha_action);
                break;
            case 12:
                ay.overridePendingTransition(R.anim.myown_design,
                        R.anim.my_alpha_action);
                break;
            case 13:
                ay.overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            case 14:
                ay.overridePendingTransition(R.anim.scale_translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 15:
                ay.overridePendingTransition(R.anim.scale_translate,
                        R.anim.my_alpha_action);
                break;
            case 16:
                ay.overridePendingTransition(R.anim.translate_rotate,
                        R.anim.my_alpha_action);
                break;
            case 17:
                ay.overridePendingTransition(R.anim.hyperspace_in,
                        R.anim.hyperspace_out);
                break;
            case 18:
                ay.overridePendingTransition(R.anim.shake,
                        R.anim.my_alpha_action);
                break;
            case 19:
                ay.overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case 20:
                ay.overridePendingTransition(R.anim.push_up_in,
                        R.anim.push_up_out);
                break;
            case 21:
                ay.overridePendingTransition(R.anim.slide_left,
                        R.anim.slide_right);
                break;
            case 22:
                ay.overridePendingTransition(R.anim.slide_top_to_bottom,
                        R.anim.my_alpha_action);
                break;
            case 23:
                ay.overridePendingTransition(R.anim.wave_scale,
                        R.anim.my_alpha_action);
                break;
        }
    }



    public static void _ToNext(NotificatFinishEvent event,Context mContext){
       Intent intent=null;
       if(!StringUtil.isEmptyorNull(event.getiType())) {
           if (event.getiType().equals("0")) {
               intent = new Intent(mContext, ImageActivity.class);
           } else if (event.getiType().equals("1")) {
               intent = new Intent(mContext, VideoActivity.class);
           } else if (event.getiType().equals("3")) {
               intent = new Intent(mContext, FileDisplayActivity.class);
           } else {
               intent = new Intent(mContext, WebActivity.class);
           }
           intent.putExtra("path", event.getPath());
           intent.putExtra("flag", event.getFlag());
           intent.putExtra("size", event.getZsize());
           intent.putExtra("time", event.getTime());
           intent.putExtra("isDefault", event.getIsDefault());
           _ToNextAcy(intent, (Activity) mContext, event.getAction());
       }
    }



}
