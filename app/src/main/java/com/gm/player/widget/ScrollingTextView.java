package com.gm.player.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by shaodongPC on 2017/8/8.
 * 参考：https://stackoverflow.com/questions/11856875/textview-restarts-marquee-when-changing-another-textview-in-same-linearlayout
 */

public class ScrollingTextView extends android.support.v7.widget.AppCompatTextView {
    public ScrollingTextView(Context context) {
        super(context, null);
    }

    public ScrollingTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * 这个属性这个View得到焦点,在这里我们设置为true,这个View就永远是有焦点的
     */
    @Override
    public boolean isFocused() {
        return true;
    }

    /*
     * 用于EditText抢注焦点的问题
     * */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    /*
     * Window与Window间焦点发生改变时的回调
     * */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus)
            super.onWindowFocusChanged(hasWindowFocus);
    }

    /*
     * 页面页面中有控件改变布局触发layoutchange时的处理
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.width = w;
        params.height = h;
        params.weight = 0;
        setLayoutParams(params);
    }
}
