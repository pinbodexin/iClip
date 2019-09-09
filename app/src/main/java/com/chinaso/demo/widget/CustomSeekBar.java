package com.chinaso.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


public class CustomSeekBar extends android.support.v7.widget.AppCompatSeekBar {
    boolean touchingProgressBar = true;

    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTouchingProgressBar(boolean touchingProgressBar) {
        this.touchingProgressBar = touchingProgressBar;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchingProgressBar) {
            super.onTouchEvent(event);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("yanxu", "MotionEvent.ACTION_DOWN");
                    //请求触摸事件不被打断
                    getParent().requestDisallowInterceptTouchEvent(true);
                    touchingProgressBar = true;
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("yanxu", "MotionEvent.ACTION_UP");
                    //当结束滑动时请求触摸事件可以被打断
                    getParent().requestDisallowInterceptTouchEvent(false);
                    touchingProgressBar = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        } else {

        }
        return true;
    }

    //此处非常重要， 返回true，后续事件（ACTION_MOVE、ACTION_UP）会再传递
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d("yanxu", "dispatchTouchEvent");
        super.dispatchTouchEvent(event);
        return true;
    }
}


