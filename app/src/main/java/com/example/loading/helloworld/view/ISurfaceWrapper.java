package com.example.loading.helloworld.view;

import android.graphics.Canvas;

/**
 * Created by loading on 25/04/2018.
 */

public interface ISurfaceWrapper {
    Canvas lockCanvas();

    void unlockCanvasAndPost(Canvas canvas);

    boolean isViewCreated();
}
