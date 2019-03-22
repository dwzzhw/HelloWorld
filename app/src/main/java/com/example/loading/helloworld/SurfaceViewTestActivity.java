package com.example.loading.helloworld;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;

import com.loading.common.utils.DrawUtils;
import com.loading.common.utils.Loger;
import com.example.loading.helloworld.view.ISurfaceWrapper;
import com.example.loading.helloworld.view.SurfaceViewWrapper;
import com.example.loading.helloworld.view.TextureViewWrapper;

/**
 * Created by loading on 25/04/2018.
 */

public class SurfaceViewTestActivity extends Activity implements Handler.Callback {
    private static final String TAG = "SurfaceViewTestActivity";
    private static final int MSG_PLAY_VIDEO = 1;
    private static final int MSG_PLAY_DANMAKU = 2;
    private static final int MSG_DRAW_BG = 3;
    //    private SurfaceView mockPlayerView;
    private RelativeLayout mRootView;
    private View mockPlayerView;
    private ISurfaceWrapper mPlayerWrapper;
    private Paint mVideoPaint;

    private View mockDanmakuView;
    private ISurfaceWrapper mDanmakuWrapper;
    private Paint mDanmakuPaint;

//    private TextureView mBgTextureView;
//    private TextureViewWrapper mBgTextureWrapper;

    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_surface);
        mRootView = (RelativeLayout) findViewById(R.id.root_view);
//        mockPlayerView = (SurfaceView) findViewById(R.id.mock_player_surface);
//        mPlayerWrapper = new SurfaceViewWrapper(mockPlayerView, false, false);
//        mBgTextureView = (TextureView) findViewById(R.id.bg_textureview);
//        mBgTextureWrapper = new TextureViewWrapper(mBgTextureView);
        mockPlayerView = findViewById(R.id.mock_player_surface);

        if (mockPlayerView instanceof SurfaceView) {
            mPlayerWrapper = new SurfaceViewWrapper((SurfaceView) mockPlayerView, false, false);
        } else if (mockPlayerView instanceof TextureView) {
            mPlayerWrapper = new TextureViewWrapper((TextureView) mockPlayerView);
        }

        mockDanmakuView = findViewById(R.id.mock_danmaku_surface);
        if (mockDanmakuView instanceof SurfaceView) {
            mDanmakuWrapper = new SurfaceViewWrapper((SurfaceView) mockDanmakuView, false, false);
        } else if (mockDanmakuView instanceof TextureView) {
            mDanmakuWrapper = new TextureViewWrapper((TextureView) mockDanmakuView);
        }

        mHandler = new Handler(this);
        initPaint();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mockPlayerView.setVisibility(View.VISIBLE);
                startPlayVideo();
            }
        }, 1000);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mockDanmakuView.setVisibility(View.VISIBLE);
                startPlayDanmaku();

//                addSurfaceView();
            }
        }, 2000);

        mHandler.sendEmptyMessageDelayed(MSG_PLAY_VIDEO, 60);

//        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.big_img));
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                drawBg();
//            }
//        }, 500);
    }

    private void initPaint() {
        mVideoPaint = new Paint();
        mVideoPaint.setStyle(Paint.Style.FILL);
        mVideoPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mVideoPaint.setStrokeWidth(2);
        mVideoPaint.setTextSize(60);
        mVideoPaint.setColor(Color.GREEN);

        mDanmakuPaint = new Paint();
        mDanmakuPaint.setStyle(Paint.Style.FILL);
        mDanmakuPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mDanmakuPaint.setStrokeWidth(1);
        mDanmakuPaint.setTextSize(40);
        mDanmakuPaint.setColor(Color.RED);
    }

    private void startPlayVideo() {
//        Loger.d(TAG, "-->startPlayVideo()");
        Canvas canvas = null;
        if (mPlayerWrapper != null) {
            try {
                canvas = mPlayerWrapper.lockCanvas();
//            drawVideoTextOnCanvas(canvas, "Video Time: " + System.currentTimeMillis(), Color.parseColor("#55ff0000"));
                Drawable drawable = getResources().getDrawable(R.drawable.emo_banku);
                drawVideoTextOnCanvas(canvas, drawable, "Video: " + System.currentTimeMillis(), 0);
            } catch (Exception e) {
                Loger.e("Opt Video Canvas exception", e);
            } finally {
                if (canvas != null) {
                    try {
                        mPlayerWrapper.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        Loger.e("release Video Canvas exception", e);
                    }
                }
            }

            if (!isFinishing()) {
                mHandler.sendEmptyMessageDelayed(MSG_PLAY_VIDEO, 100);
            }
        }
    }

    private void startPlayDanmaku() {
//        Loger.d(TAG, "-->startPlayDanmaku()");
        Canvas canvas = null;
        if (mDanmakuWrapper != null) {
            try {
                canvas = mDanmakuWrapper.lockCanvas();
                drawDanmakuTextOnCanvas(canvas, "Danmaku: " + System.currentTimeMillis(), 0);
//            drawVideoTextOnCanvas(canvas, "Danmaku Time: " + System.currentTimeMillis(), Color.parseColor("#55ff0000"));
            } catch (Exception e) {
                Loger.e("Opt danmaku canvas exception", e);
            } finally {
                if (canvas != null) {
                    try {
                        mDanmakuWrapper.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        Loger.e("release danmaku canvas exception", e);
                    }
                }
            }

            if (!isFinishing()) {
                mHandler.sendEmptyMessageDelayed(MSG_PLAY_DANMAKU, 100);
            }
        }
    }

    private void drawVideoTextOnCanvas(Canvas canvas, Drawable drawable, String contentStr, int fillColor) {
//        Loger.d(TAG, "-->drawVideoTextOnCanvas(), contentStr=" + contentStr + ", fillColor=" + fillColor);
        if (canvas != null && !TextUtils.isEmpty(contentStr)) {
            DrawUtils.clearCanvas(canvas);
            if (fillColor != 0) {
                canvas.drawColor(fillColor);
            }
            if (drawable != null) {
                drawable.setBounds(100, 200, 300, 400);
                drawable.draw(canvas);
            }
            canvas.drawText(contentStr, 10, 100, mVideoPaint);
        }
    }

    private void drawDanmakuTextOnCanvas(Canvas canvas, String contentStr, int fillColor) {
//        Loger.d(TAG, "-->drawDanmakuTextOnCanvas(), contentStr=" + contentStr + ", fillColor=" + fillColor);
        if (canvas != null && !TextUtils.isEmpty(contentStr)) {
            DrawUtils.clearCanvas(canvas);
            if (fillColor != 0) {
                canvas.drawColor(fillColor);
            }
            canvas.drawText(contentStr, 10, 100, mDanmakuPaint);
        }
    }

//    private void drawBg() {
//        Canvas canvas = null;
//        if (mBgTextureView != null) {
//            mBgTextureView.setVisibility(View.VISIBLE);
//        }
//        if (mBgTextureWrapper != null) {
//            try {
//                canvas = mBgTextureWrapper.lockCanvas();
//                drawDanmakuTextOnCanvas(canvas, "BG: " + System.currentTimeMillis(), Color.WHITE);
//                Loger.d(TAG, "draw bg ok");
//                mHandler.sendEmptyMessageDelayed(MSG_DRAW_BG, 100);
////            drawVideoTextOnCanvas(canvas, "Danmaku Time: " + System.currentTimeMillis(), Color.parseColor("#55ff0000"));
//            } catch (Exception e) {
//                Loger.e("Opt danmaku canvas exception", e);
//            } finally {
//                if (canvas != null) {
//                    try {
//                        mBgTextureWrapper.unlockCanvasAndPost(canvas);
//                    } catch (Exception e) {
//                        Loger.e("release danmaku canvas exception", e);
//                    }
//                }
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPlayVideo();
        startPlayDanmaku();
//        drawBg();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_PLAY_VIDEO:
                startPlayVideo();
                break;
            case MSG_PLAY_DANMAKU:
                startPlayDanmaku();
                break;
            case MSG_DRAW_BG:
//                drawBg();
                break;
        }

        return false;
    }

    private void addSurfaceView() {
        Loger.d(TAG, "addSurfaceView()");
        if (mRootView != null) {
            SurfaceView surfaceView = new SurfaceView(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(100, 100);
            mRootView.addView(surfaceView, lp);
        }

    }
}
