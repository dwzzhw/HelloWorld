package com.tencent.qqsports.commentbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tencent.qqsports.common.util.PermissionUtils;


/**
 * 按住说话录制控件
 *
 * @author yudongjin
 */
public class RecorderFloatingView extends RelativeLayout implements // todo yudongjin
//        ApolloVoiceManager.ApolloVoiceListener,
        PermissionUtils.PermissionCallback {
    public RecorderFloatingView(Context context) {
        super(context);
    }

    public RecorderFloatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecorderFloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onPermissionResult(boolean granted) {

    }
}
