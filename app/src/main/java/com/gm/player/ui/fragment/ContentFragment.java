package com.gm.player.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.gm.player.R;
import com.gm.player.widget.MyInterpolator;
import com.nineoldandroids.animation.AnimatorSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by Konstantin on 22.12.2014.
 */
public class ContentFragment extends BaseFragment{
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    Unbinder unbinder1;
    private String TAG = this.getClass().getSimpleName();
    public static final String CLOSE = "Close";
    public static final String BUILDING = "设 置";
    public static final String FILES = "文 件";
    public static final String DOWNLOAD = "下 载";
    public static final String INDEX = "首 页";
    public static final String ABOUT = "关 于";
    public static final String ARG_MAR = "ARG_MAR";
    Unbinder unbinder;
    @BindView(R.id.tv_left)
    TextView tvLeft;
//    @BindView(R.id.image_content)
//    ImageView mImageView;


    private View containerView;
    protected String res;
    private Bitmap bitmap;

    public static ContentFragment newInstance(String key) {
        ContentFragment contentFragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAR, key);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            res = getArguments().getString(ARG_MAR);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, mRootView);
        init();
    }

    @Override
    protected String setTagName() {
        return INDEX;
    }

    private void init() {
        initIndex();
    }


    private void initIndex() {

        Animation t2=new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.45f,
                Animation.RELATIVE_TO_PARENT, 0.45f,
                Animation.RELATIVE_TO_PARENT, -0.5f,
                Animation.RELATIVE_TO_PARENT, 0.32f );
        t2.setDuration(5000);
        t2.setInterpolator(new BounceInterpolator());
        t2.setFillAfter(true);
        ivLogo.setAnimation(t2);
        ivLogo.setVisibility(View.VISIBLE);
        // 步骤2：创建平移动画的对象：平移动画对应的Animation子类为TranslateAnimation
        // 参数分别是：
        // 1. fromXDelta ：视图在水平方向x 移动的起始值
        // 2. toXDelta ：视图在水平方向x 移动的结束值
        // 3. fromYDelta ：视图在竖直方向y 移动的起始值
        // 4. toYDelta：视图在竖直方向y 移动的结束值
        AnimationSet set=new AnimationSet(false);
        Animation translateAnimation1 = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1f,
                Animation.RELATIVE_TO_PARENT, 0.25f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0.55f );
        Animation translateAnimation2 = new RotateAnimation(0f,360f);
        translateAnimation1.setDuration(5000);
        translateAnimation2.setDuration(5000);
        translateAnimation1.setFillAfter(true);
        translateAnimation2.setFillAfter(true);
        translateAnimation1.setInterpolator(new OvershootInterpolator());
        translateAnimation2.setInterpolator(new AnticipateInterpolator());
        set.setFillAfter(true);
        set.addAnimation(translateAnimation1);
       // set.addAnimation(translateAnimation2);
        // 固定属性的设置都是在其属性前加“set”，如setDuration（）
        tvLeft.startAnimation(set);
       // tvLeft.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

