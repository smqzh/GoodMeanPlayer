package com.gm.player.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gm.player.R;

@SuppressLint("AppCompatCustomView")
public class ImageFoucsView extends ImageView {

    private Animation scaleSmallAnimation;
    private Animation scaleBigAnimation;

    public ImageFoucsView(Context context) {
        super(context);
    }

    public ImageFoucsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageFoucsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            zoomOut();
        } else {
            zoomIn();
        }
    }

    private void zoomIn() {
        if (scaleSmallAnimation == null) {
            scaleSmallAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale_small);
        }
        startAnimation(scaleSmallAnimation);
    }

    private void zoomOut() {
        if (scaleBigAnimation == null) {
            scaleBigAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale_big);
        }
        startAnimation(scaleBigAnimation);
    }
}
