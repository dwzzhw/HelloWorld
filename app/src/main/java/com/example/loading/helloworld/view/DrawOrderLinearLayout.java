package com.example.loading.helloworld.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.loading.helloworld.utils.Loger;

public class DrawOrderLinearLayout extends LinearLayout {
    private static final String TAG = "DrawOrderLinearLayout_dwz";
    private int[] order = new int[]{1, 2, 0, 4, 3};
//    private int[] order = new int[]{1, 2, 0, 1, 3};

    public DrawOrderLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int result = order[i];
        Loger.d(TAG, "-->getChildDrawingOrder(), i=" + i + ", order=" + result);
        return result;
    }
}
