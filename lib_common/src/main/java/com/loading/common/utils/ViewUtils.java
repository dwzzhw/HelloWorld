package com.loading.common.utils;

import android.view.View;
import android.view.ViewGroup;

import com.loading.common.function.Predicate;

public class ViewUtils {
    public static void setVisibility(View view, int visibility) {
        if (view != null && isValidVisibility(visibility)) {
            if (view.getVisibility() != visibility) {
                view.setVisibility(visibility);
            }
        }
    }

    public static boolean isValidVisibility(int visibility) {
        return visibility == View.VISIBLE || visibility == View.INVISIBLE || visibility == View.GONE;
    }

    public static void showSelfIfHasChildVisible(final ViewGroup viewGroup) {
        boolean hasVisibleChild = hasChild(viewGroup, view -> View.VISIBLE == view.getVisibility());
        ViewUtils.setVisibility(viewGroup, hasVisibleChild ? View.VISIBLE : View.GONE);
    }

    private static boolean hasChild(final ViewGroup viewGroup, final Predicate<View> predicate) {
        boolean hasChild = false;
        final int childCnt = viewGroup == null ? 0 : viewGroup.getChildCount();
        for (int i = 0; i < childCnt; i++) {
            View tChildView = viewGroup.getChildAt(i);
            if (tChildView != null && predicate != null && predicate.test(tChildView)) {
                hasChild = true;
                break;
            }
        }
        return hasChild;
    }
}
