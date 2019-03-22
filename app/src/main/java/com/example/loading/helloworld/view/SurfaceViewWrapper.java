package com.example.loading.helloworld.view;

import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.loading.common.utils.Loger;

/**
 * Created by loading on 25/04/2018.
 */

public class SurfaceViewWrapper implements ISurfaceWrapper, SurfaceHolder.Callback {
    private static final String TAG = "SurfaceViewWrapper";
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private boolean isSurfaceCreated;
    private boolean isZOrderOnTop;
    private boolean isZOrderMediaOverlay;

    public SurfaceViewWrapper(SurfaceView surfaceView, boolean onTop, boolean overlayVideo) {
        Loger.d(TAG, "-->SurfaceViewWrapper<init>, onTop=" + onTop + ", overlayVideo=" + overlayVideo);
        mSurfaceView = surfaceView;
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        isZOrderOnTop = onTop;
        isZOrderMediaOverlay = overlayVideo;

        if (onTop) {
            mSurfaceView.setZOrderOnTop(onTop);
        }
        if (overlayVideo) {
            mSurfaceView.setZOrderMediaOverlay(overlayVideo);
        }
    }

    @Override
    public Canvas lockCanvas() {
        return mSurfaceHolder.lockCanvas();
    }

    @Override
    public void unlockCanvasAndPost(Canvas canvas) {
        if (mSurfaceHolder != null) {
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean isViewCreated() {
        return isSurfaceCreated;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mSurfaceHolder != null) {
            mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        }
        if (mSurfaceView != null) {
            if (isZOrderMediaOverlay) {
                mSurfaceView.setZOrderMediaOverlay(isZOrderMediaOverlay);
            }
            if (isZOrderOnTop) {
                mSurfaceView.setZOrderOnTop(isZOrderOnTop);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        isSurfaceCreated = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceCreated = false;
    }
}
