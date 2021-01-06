package com.example.loading.helloworld.activity.ui;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loading.helloworld.R;
import com.example.loading.helloworld.view.AutoExpandMovementMethod;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtil;

/**
 *
 */
public class BaseUIComponentTestActivity extends BaseActivity {
    private static final String TAG = "BaseUIComponentTestActivity";
    private TextView mDescTextView;
    private AutoExpandMovementMethod mMovement;
    private View mColorImageView = null;
    private FrameLayout mColorContainer;

    private String mShortStr = "很短的描述";
    private String mMediumStr = "不长不短的描述1不长不短的描述2不长不短的描述3不长不短的描述4不长不短的描述5不长不短的描述6";
    private String mLongStr = "我是一段很长的描述第一段我是一段很长的描述第一段我是一段很长的描述第一段我是一段很长的描述第一段我是一段很长的描述第一段" +
            "\n\n" +
            "我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段" +
            "我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段" +
            "我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段我是一段很长的描述第二段" +
            "\n\n" +
            "我是一段很长的描述第三段";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_ui_comp_test);

        mDescTextView = findViewById(R.id.photo_content_text);
        mMovement = new AutoExpandMovementMethod(SystemUtil.dpToPx(60), SystemUtil.dpToPx(180));
        mDescTextView.setMovementMethod(mMovement);
        mColorImageView = findViewById(R.id.color_image);
        mColorContainer = findViewById(R.id.drawable_container);

//        mDescTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
        int viewId = view.getId();
        if (viewId == R.id.btn_short_content) {
            fillText(mShortStr);
        } else if (viewId == R.id.btn_long_content) {
            fillText(mLongStr);
        } else if (viewId == R.id.btn_middle_content) {
            fillText(mMediumStr);
        }else if (viewId == R.id.btn_color_drawable) {
            fillColorDrawable();
        }
    }

    private void fillText(String text) {
        mMovement.resetMaxHeight(mDescTextView);
        mDescTextView.setText(text);
    }

    int index = 0;
    private Drawable mDrawable = null;
    private void fillColorDrawable(){
        Loger.d(TAG, "-->fillColorDrawable()");
        if(mDrawable==null){
//            mDrawable = new ColorDrawable(Color.RED);
            mDrawable = new ColorDrawable(-16776961);
            mColorImageView.setBackgroundColor(Color.BLUE);
//            setImageMatrix();
            return;
        }

        if(index++%2==0){
            mColorContainer.setAlpha(1);
        }else {
            mColorContainer.setAlpha(0.5f);
        }
    }

//    private void setImageMatrix() {
//        Matrix matrix = mColorImageView.getImageMatrix();
//
//        float scale;
//        float dx = 0, dy = 0;
//
//        final int dWidth = mColorImageView.getDrawable().getIntrinsicWidth();
//        final int dHeight = mColorImageView.getDrawable().getIntrinsicHeight();
//
//        final int vWidth = mColorImageView.getWidth() - mColorImageView.getPaddingLeft() - mColorImageView.getPaddingRight();
//        final int vHeight = mColorImageView.getHeight() - mColorImageView.getPaddingTop() - mColorImageView.getPaddingBottom();
//
//        if (dWidth * vHeight > vWidth * dHeight) {
//            scale = (float) vHeight / (float) dHeight;
//            dx = (vWidth - dWidth * scale) * 0.5f;
//        } else {
//            scale = (float) vWidth / (float) dWidth;
//            dy = (vHeight - dHeight * scale) * 1.0f;
//        }
//
//        matrix.setScale(scale, scale);
//        matrix.postTranslate(Math.round(dx), Math.round(dy));
//        mColorImageView.setImageMatrix(matrix);
//    }
}
