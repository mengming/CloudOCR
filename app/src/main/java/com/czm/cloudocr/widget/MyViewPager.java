package com.czm.cloudocr.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

    private boolean canSlide = false;

    public MyViewPager(@NonNull Context context) {
        super(context);
    }

    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanSlide(boolean canSlide) {
        this.canSlide = canSlide;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canSlide && super.onInterceptTouchEvent(ev);
    }
}
