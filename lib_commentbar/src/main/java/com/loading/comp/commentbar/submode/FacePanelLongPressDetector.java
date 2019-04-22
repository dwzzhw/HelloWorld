package com.loading.comp.commentbar.submode;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.loading.common.component.CApplication;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtil;
import com.loading.common.utils.UiThreadUtil;
import com.loading.comp.commentbar.R;
import com.loading.modules.interfaces.face.data.FacePageItems;

public class FacePanelLongPressDetector implements View.OnTouchListener {
    private static final String TAG = "FacePanelLongPressDetector";
    private static long LONG_PRESS_THRESHOLD_IN_MS = 500; //长按超过这个时间阈值则展示表情提示符
    private FacePageItems pageItems;
    private int paddingLR;
    private int paddingTop;
    private int horizontalSpace;
    private int verticalSpace;
    private int columnCnt;
    private int rowCnt;
    private int screenWidth;
    private int itemContentHeight;
    private float itemTotalWidth;
    private long downEventTime = 0;
    private boolean isInLongPressMode;
    private boolean isLongPressInterrupted; //比如多手势动作会打断长按的过程
    private int lastPressItemRowIndex;
    private int lastPressItemColumnIndex;

    private IFaceItemLongPressListener mFaceItemLongPressListener;
    private Runnable mNoMoveDetectRunnable;
    private View mTargetView;
    private MotionEvent mDownEvent;


    public FacePanelLongPressDetector(FacePageItems pageItems, IFaceItemLongPressListener faceItemLongPressListener, int paddingLR, int paddingTop, int horizontalSpace, int verticalSpace, int columnCnt, int rowCnt) {
        this.pageItems = pageItems;
        this.paddingLR = paddingLR;
        this.paddingTop = paddingTop;
        this.horizontalSpace = horizontalSpace;
        this.verticalSpace = verticalSpace;
        this.columnCnt = columnCnt;
        this.rowCnt = rowCnt;

        mFaceItemLongPressListener = faceItemLongPressListener;

        screenWidth = SystemUtil.getRealTimeScreenWidthIntPx(CApplication.getAppContext());
        itemContentHeight = CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_item_size);
        itemTotalWidth = (screenWidth - paddingLR * 2) / columnCnt;
        Loger.d(TAG, "-->LongPressDetectorOnTouchListener.init<>, paddingLR=" + paddingLR + ", paddingTop=" + paddingTop + ", horizontalSpace=" + horizontalSpace
                + ", verticalSpace=" + verticalSpace + ", itemContentHeight=" + itemContentHeight + ", itemTotalWidth=" + itemTotalWidth + ", screenWidth=" + screenWidth);
        mNoMoveDetectRunnable = () -> checkLongPressEvent(mTargetView, mDownEvent);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int eventAction = event.getAction() & MotionEvent.ACTION_MASK;
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                initState();
                downEventTime = System.currentTimeMillis();
                mDownEvent = MotionEvent.obtain(event);
                mTargetView = v;
                UiThreadUtil.removeRunnable(mNoMoveDetectRunnable);
                UiThreadUtil.postDelay(mNoMoveDetectRunnable, LONG_PRESS_THRESHOLD_IN_MS);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isLongPressInterrupted) {
                    checkLongPressEvent(v, event);
                }
                break;
            default:
                UiThreadUtil.removeRunnable(mNoMoveDetectRunnable);
                isLongPressInterrupted = true;
                if (mFaceItemLongPressListener != null && pageItems != null && isInLongPressMode) {
                    mFaceItemLongPressListener.exitLongPressState();
                }
                break;
        }
        return isInLongPressMode;
    }

    private void initState() {
        isInLongPressMode = false;
        isLongPressInterrupted = false;
        lastPressItemRowIndex = -1;
        lastPressItemColumnIndex = -1;
        downEventTime = -1;
    }

    private void checkLongPressEvent(View view, MotionEvent event) {
        if (view != null && event != null) {
            if (!isInLongPressMode && downEventTime > 0 && System.currentTimeMillis() - downEventTime > LONG_PRESS_THRESHOLD_IN_MS) {
                isInLongPressMode = true;
                if (mFaceItemLongPressListener != null) {
                    mFaceItemLongPressListener.enterLongPressState();
                }
            }
            if (isInLongPressMode) {
                UiThreadUtil.removeRunnable(mNoMoveDetectRunnable);
                int curPressedItemRowIndex = getPressedItemRowIndex(event.getY());
                int curPressedItemColumnIndex = getPressedItemColumnIndex(event.getX());
                if (curPressedItemRowIndex != lastPressItemRowIndex || curPressedItemColumnIndex != lastPressItemColumnIndex) {
                    int pressedFaceItemPos = curPressedItemRowIndex * columnCnt + curPressedItemColumnIndex;
                    Loger.i(TAG, "-->current pressed item index=[" + curPressedItemRowIndex + ", " + curPressedItemColumnIndex
                            + "] , center pos=[" + getItemViewCenterX(curPressedItemColumnIndex) + ", " + getItemViewCenterY(curPressedItemRowIndex)
                            + "], event pos=[" + event.getX() + ", " + event.getY() + "], "
                            + "], event raw pos=[" + event.getRawX() + ", " + event.getRawY() + "], "
                            + " face name=" + pageItems.getFaceStringAtGroupPosition(pressedFaceItemPos));
                    lastPressItemRowIndex = curPressedItemRowIndex;
                    lastPressItemColumnIndex = curPressedItemColumnIndex;
                    if (mFaceItemLongPressListener != null && pageItems != null) {
                        if (curPressedItemRowIndex >= 0 && curPressedItemColumnIndex >= 0 && (curPressedItemRowIndex < rowCnt - 1 || curPressedItemColumnIndex < columnCnt - 1)) {
                            Rect globalRect = new Rect();
                            view.getGlobalVisibleRect(globalRect);
                            if (view.getParent() != null) {
                                view.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            mFaceItemLongPressListener.onFaceLongPressed(pageItems.getFaceBitmapAtGroupPosition(pressedFaceItemPos), pageItems.getFaceStringAtGroupPosition(pressedFaceItemPos),
                                    getItemViewCenterX(curPressedItemColumnIndex) + globalRect.left, getItemViewCenterY(curPressedItemRowIndex) + globalRect.top);
                        } else {
                            mFaceItemLongPressListener.onFaceLongPressed(null, null, -1, -1);
                        }
                    }
                }
            }
        }
    }

    private int getPressedItemRowIndex(float eventY) {
        int rowIndex = -1;
        if (eventY > paddingTop && eventY < paddingTop + rowCnt * (itemContentHeight + verticalSpace)) {
            float itemTotalHeight = itemContentHeight + verticalSpace;
            float relatedEventY = eventY - paddingTop;
            rowIndex = (int) (relatedEventY / itemTotalHeight);
        }
        if (rowIndex >= rowCnt) {
            rowIndex = rowCnt - 1;
        }
        return rowIndex;
    }

    private int getPressedItemColumnIndex(float eventX) {
        int columnIndex = -1;
        if (eventX > paddingLR && eventX < screenWidth - paddingLR) {
            float relatedEventX = eventX - paddingLR;
            columnIndex = (int) (relatedEventX / itemTotalWidth);
        }
        if (columnIndex >= columnCnt) {
            columnIndex = columnCnt - 1;
        }
        return columnIndex;
    }

    private float getItemViewCenterX(int columnIndex) {
        float centerX = -1;
        if (columnIndex >= 0 && columnIndex < columnCnt) {
            centerX = paddingLR + columnIndex * itemTotalWidth + itemTotalWidth / 2;
        }
        return centerX;
    }

    private float getItemViewCenterY(int rowIndex) {
        float centerY = -1;
        if (rowIndex >= 0 && rowIndex < rowCnt) {
            float itemTotalHeight = itemContentHeight + verticalSpace;
            centerY = paddingTop + rowIndex * itemTotalHeight + itemTotalHeight / 2;
        }
        return centerY;
    }
}
