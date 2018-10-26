package com.gm.player.widget;

import android.view.animation.Interpolator;

public class MyInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float i) {
        // TODO Auto-generated method stub
        return 2*i*i-2*i+1;
    }

}
