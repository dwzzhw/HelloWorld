package com.tencent.qqsports.commentbar.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loading.common.component.CApplication;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtils;
import com.tencent.qqsports.commentbar.R;
import com.tencent.qqsports.commentbar.submode.IFaceItemLongPressListener;

public class FacePanelPreviewView extends RelativeLayout implements IFaceItemLongPressListener {
    private static final String TAG = "FacePanelPreviewView";
    ImageView facePreviewView;
    TextView faceNameView;
    int faceItemViewSize;
    int bottomMarginToFaceIcon;
    private boolean isInLongPressState = false;

    public FacePanelPreviewView(Context context) {
        this(context, null);
    }

    public FacePanelPreviewView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FacePanelPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.face_panel_preview_layout, this, true);
        faceItemViewSize = CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_item_size);

        facePreviewView = findViewById(R.id.face_preview_view);
        faceNameView = findViewById(R.id.face_name_view);
        bottomMarginToFaceIcon = SystemUtils.dpToPx(0);
    }

    @Override
    public void onFaceLongPressed(Bitmap faceBitmap, String faceName, float faceViewCenterX, float faceViewCenterY) {
        Loger.d(TAG, "-->onFaceLongPressed(), faceName=" + faceName);
        if (faceName != null) {
            faceName = faceName.replaceAll("[\\[\\]]", "");
        }
        if (facePreviewView != null && faceNameView != null && getLayoutParams() instanceof MarginLayoutParams && isInLongPressState) {
            MarginLayoutParams containerLP = (MarginLayoutParams) getLayoutParams();
            if (faceBitmap != null && containerLP != null && getParent() instanceof ViewGroup) {
                facePreviewView.setImageBitmap(faceBitmap);
                faceNameView.setText(faceName);
                setVisibility(View.VISIBLE);

                Rect parentGlobalRect = new Rect();
                ((ViewGroup) getParent()).getGlobalVisibleRect(parentGlobalRect);
                int curWidth = getWidth();
                int curHeight = getHeight();

                Loger.d(TAG, "-->onFaceLongPressed(), centerX=" + faceViewCenterX + ", centerY=" + faceViewCenterY + ",curWidth=" + curWidth + ", curHeight=" + curHeight + ", globalRect=" + parentGlobalRect);
                if (curHeight > 0 && curHeight > 0) {
                    containerLP.leftMargin = (int) (faceViewCenterX - parentGlobalRect.left - curWidth / 2);
                    containerLP.topMargin = (int) (faceViewCenterY - parentGlobalRect.top - curHeight - faceItemViewSize / 2 - bottomMarginToFaceIcon);
                    setLayoutParams(containerLP);
                } else {
                    setVisibility(View.GONE);
                }

            } else {
                setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void enterLongPressState() {
        isInLongPressState = true;
    }

    @Override
    public void exitLongPressState() {
        isInLongPressState = false;
        setVisibility(View.GONE);
    }
}
