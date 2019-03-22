package com.example.loading.helloworld.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.loading.common.utils.Loger;

/**
 * Created by loading on 25/04/2018.
 */

public class MySurfaceView extends SurfaceView {
    private static final String TAG = "MySurfaceView";

    public MySurfaceView(Context context) {
        super(context);
        initSurfaceView();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSurfaceView();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSurfaceView();
    }

    private void initSurfaceView() {
//        Loger.d(TAG, "-->initSurfaceView()");
//        setZOrderMediaOverlay(true);
//        getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    protected void onAttachedToWindow() {
        Loger.d(TAG, "-->onAttachedToWindow()");
        super.onAttachedToWindow();
    }

    @Override
    public void setZOrderOnTop(boolean onTop) {
        Loger.d(TAG, "-->setZOrderOnTop(), onTop=" + onTop);
        super.setZOrderOnTop(onTop);
    }

    @Override
    public void setZOrderMediaOverlay(boolean isMediaOverlay) {
        Loger.d(TAG, "-->setZOrderMediaOverlay(), isMediaOverlay=" + isMediaOverlay);
        super.setZOrderMediaOverlay(isMediaOverlay);
    }
}
