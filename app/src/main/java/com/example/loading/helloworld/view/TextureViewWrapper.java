package com.example.loading.helloworld.view;

import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.view.TextureView;

/**
 * Created by loading on 25/04/2018.
 */

public class TextureViewWrapper implements ISurfaceWrapper, TextureView.SurfaceTextureListener {
    private TextureView mTextureView;
    private boolean isViewCreated;

    public TextureViewWrapper(TextureView textureView) {
        mTextureView = textureView;
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setOpaque(false);
    }

    @Override
    public Canvas lockCanvas() {
        Canvas canvas = null;
        if (mTextureView != null && mTextureView.isAvailable()) {
            canvas = mTextureView.lockCanvas();
        }
        return canvas;
    }

    @Override
    public void unlockCanvasAndPost(Canvas canvas) {
        mTextureView.unlockCanvasAndPost(canvas);
    }

    @Override
    public boolean isViewCreated() {
        return isViewCreated;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        isViewCreated = true;
        mTextureView.setOpaque(false);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        isViewCreated = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        isViewCreated = true;
    }
}
