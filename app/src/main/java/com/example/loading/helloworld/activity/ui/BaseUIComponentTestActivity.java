package com.example.loading.helloworld.activity.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.View;
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
        }
    }

    private void fillText(String text) {
        mMovement.resetMaxHeight(mDescTextView);
        mDescTextView.setText(text);
    }
}
