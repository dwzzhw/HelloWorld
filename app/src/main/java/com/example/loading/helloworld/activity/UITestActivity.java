package com.example.loading.helloworld.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loading.helloworld.CountDownCircleBar;
import com.example.loading.helloworld.MemoryMonitorService;
import com.example.loading.helloworld.R;
import com.example.loading.helloworld.SurfaceViewTestActivity;
import com.example.loading.helloworld.ViewDrawingOrderTestActivity;
import com.example.loading.helloworld.activity.fragment.MyFragmentA;
import com.example.loading.helloworld.activity.fragment.MyFragmentB;
import com.example.loading.helloworld.activity.ui.BaseUIComponentTestActivity;
import com.example.loading.helloworld.lottie.LottieTestActivity;
import com.example.loading.helloworld.mykotlin.ui.CoordinatorLayoutActivity;
import com.example.loading.helloworld.view.CustomizedTextDrawable;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.Loger;

public class UITestActivity extends BaseActivity {
    //    private static final String TAG = "UITestActivity";
    private static final String TAG = "ActivitySwitch_A";
    private EditText testTextView = null;
    private TextView titleTextView = null;
    private ImageView mImageView;
    private CountDownCircleBar mCountingBar = null;

    int cnt = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Loger.d(TAG, "-->onCreate(): ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_test);

        mCountingBar = (CountDownCircleBar) findViewById(R.id.counting_bar);
//        mCountingBar.setcountingListener(new CountDownCircleBar.ICountingListener() {
//            @Override
//            public void onCountingFinished() {
//                Log.d(TAG, "-->onCountingFinished()");
//            }
//
//            @Override
//            public void onCountingCanceled() {
//                Log.d(TAG, "-->onCountingCanceled()");
//            }
//        });
        testTextView = (EditText) findViewById(R.id.text_test);
        titleTextView = (TextView) findViewById(R.id.title);
        mImageView = findViewById(R.id.img_view);
        titleTextView.setOnClickListener(mTitleClickListener);

        mCountingBar.setOnClickListener(mClickListener);
//        testTextView.setOnClickListener(mClickListener);
        mCountingBar.setEnabled(true);
    }


    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "View is clicked");
//            openKbsMatchDetailPage();
            changeTitle();
//            openSurfacePage();
//            openLottiePage();
//            gpower(5);
//            openViewDrawingOrderPage();
        }
    };

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
        int viewId = view.getId();
        if (viewId == R.id.btn_surface_view_test) {
            openSurfacePage();
        } else if (viewId == R.id.btn_lottie_test) {
            openLottiePage();
        } else if (viewId == R.id.btn_view_draw_order_test) {
            openViewDrawingOrderPage();
        } else if (viewId == R.id.btn_memory_monitor_test) {
            startMemoryService();
        } else if (viewId == R.id.btn_constraint_layout_test) {
            startConstraintLayoutTest();
        } else if (viewId == R.id.btn_lifecycle) {
            doFragmentLifeCycleTest();
        } else if (viewId == R.id.recycler_view_test) {
            doRecyclerViewTest();
        } else if (viewId == R.id.btn_coordinator_layout_test) {
            startCoordinatorLayoutTest();
        } else if (viewId == R.id.notification_test) {
            startNotificationTestPage();
        }else if (viewId == R.id.webview_test) {
            startWebViewTestPage();
        }else if (viewId == R.id.base_comp_test) {
            startBaseViewTestPage();
        }
    }

    private void openSurfacePage() {
        Intent intent = new Intent(this, SurfaceViewTestActivity.class);
        startActivity(intent);
    }

    private void openLottiePage() {
        Intent intent = new Intent(this, LottieTestActivity.class);
        startActivity(intent);
    }

    private void openViewDrawingOrderPage() {
        Intent intent = new Intent(this, ViewDrawingOrderTestActivity.class);
        startActivity(intent);
    }

    private void startConstraintLayoutTest() {
        Loger.d(TAG, "startConstraintLayoutTest()");
        Intent intent = new Intent(this, ConstraintLayoutTestActivity.class);
        startActivity(intent);
    }

    private void startNotificationTestPage() {
        Loger.d(TAG, "startNotificationTestPage()");
        Intent intent = new Intent(this, NotificationTestActivity.class);
        startActivity(intent);
    }

    private void startWebViewTestPage() {
        Loger.d(TAG, "startWebViewTestPage()");
        Intent intent = new Intent(this, WebViewTestActivity.class);
        startActivity(intent);
    }

    private void startBaseViewTestPage() {
        Loger.d(TAG, "startBaseViewTestPage()");
        Intent intent = new Intent(this, BaseUIComponentTestActivity.class);
        startActivity(intent);
    }

    private void startCoordinatorLayoutTest() {
        Loger.d(TAG, "startCoordinatorLayoutTest()");
        CoordinatorLayoutActivity.Companion.startActivity(this);
    }

    private int fragmentCnt = 0;

    private void doFragmentLifeCycleTest() {
        Loger.d(TAG, "-->doFragmentLifeCycleTest(), fragmentCnt=" + fragmentCnt);
        if (fragmentCnt == 0) {
            fragmentCnt++;
            Fragment fragmentA = new MyFragmentA();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragmentA).commit();
        } else if (fragmentCnt == 1) {
            fragmentCnt++;
            Fragment fragmentB = new MyFragmentB();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentB).commit();
        }
    }

    private void doRecyclerViewTest() {
        Loger.d(TAG, "-->doRecyclerViewTest()");
        Intent intent = new Intent(this, RecyclerViewTestActivity.class);
        startActivity(intent);
    }

    private void startMemoryService() {
        Log.i(TAG, "startMemoryService()");
        Intent intent = new Intent(this, MemoryMonitorService.class);
        startService(intent);
    }

    private View.OnClickListener mTitleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Title view is clicked");
            changeTitle();

        }
    };

    @Override
    protected void onResume() {
        Loger.d(TAG, "-->onResume(): ");
        super.onResume();
        mCountingBar.startCounting(10);
        loadRoundCornerBitmapWith565();

//        mCountingBar.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mCountingBar.stopCounting();
//            }
//        }, 5000);
    }

    private void changeTitle() {
        String titleStr = null;
        if (cnt % 2 == 0) {
            titleStr = "Hello";
        } else {
            titleStr = "Hello World Hello World Hello World";
        }
        Log.d(TAG, "set title:" + titleStr);
        cnt++;
        titleTextView.setText(titleStr);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        private int atSymbleIndex = -1;    //该位置出现了一个@，可能是手动输入或黏贴的最后一个字符，也可能是从后面开始删除到该位置

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "-->onTextChanged(), s=" + s + ", start=" + start + ", before=" + before + ", count=" + count);
            atSymbleIndex = -1;
            if (before == 0 && count > 0) {   //手动输入或黏贴的最后一个字符 @
                if (s.charAt(start + count - 1) == '@') {
                    Log.i(TAG, "input @");
                    atSymbleIndex = start + count - 1;
                }
            } else if (before > 0 && count == 0) {  //从后面开始删除字符，遇到@
                if (start > 0 && s.charAt(start - 1) == '@') {
                    Log.i(TAG, "delete then encount @");
                    atSymbleIndex = start - 1;
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d(TAG, "-->beforeTextChanged(), s=" + s + ", start=" + start + ", after=" + after + ", count=" + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, "-->afterTextChanged(), s=" + s);
            int encountedAtIndex = atSymbleIndex;
            if (encountedAtIndex >= 0 && s.charAt(encountedAtIndex) == '@') {
                Log.i(TAG, "Do auto replace now, atSymbleIndex=" + encountedAtIndex);
                String faceContent = "@主持人";
                s.replace(encountedAtIndex, encountedAtIndex + 1, faceContent);

                CustomizedTextDrawable faceDrawable = new CustomizedTextDrawable(UITestActivity.this, faceContent, testTextView.getPaint());

                faceDrawable.setBounds(0, 0, faceDrawable.getIntrinsicWidth(), faceDrawable.getIntrinsicHeight());
                ImageSpan imageSpan = new ImageSpan(faceDrawable, DynamicDrawableSpan.ALIGN_BOTTOM);
                Log.i(TAG, "before crash, atSymbleIndex=" + encountedAtIndex + ", string length=" + s.length());
                s.setSpan(imageSpan, encountedAtIndex, encountedAtIndex + faceContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void bgTest() {
        if (titleTextView != null) {
            Drawable bgDrawable = CommonUtil.getTL2BRGradientMaskDrawable(new int[]{0x22ff0000, 0x2200ff00}, R.drawable.emo_banku, this);
            Log.i(TAG, "-->bgTest(), bgDrawable=" + bgDrawable);
            titleTextView.setBackground(bgDrawable);
        }
    }

    private void textTest() {
        testTextView.removeTextChangedListener(mTextWatcher);
        testTextView.addTextChangedListener(mTextWatcher);

        String formerPart = "大个红字123";
        String laterPart = "small blue";

        SpannableString spannableString = new SpannableString(formerPart + laterPart);
        AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(20, true);
        AbsoluteSizeSpan smallSizeSpan = new AbsoluteSizeSpan(12, true);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(0xffff0000);
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(0xff0000ff);

        Bitmap faceIcon = BitmapFactory.decodeResource(getResources(), R.drawable.emo_banku);
//        Drawable faceDrawable = new BitmapDrawable(getResources(), faceIcon);
        String faceContent = "@主持人";
        CustomizedTextDrawable faceDrawable = new CustomizedTextDrawable(this, faceContent, testTextView.getPaint());

        faceDrawable.setBounds(0, 0, faceDrawable.getIntrinsicWidth(), faceDrawable.getIntrinsicHeight());
        ImageSpan imageSpan = new ImageSpan(faceDrawable, DynamicDrawableSpan.ALIGN_BOTTOM);


        spannableString.setSpan(bigSizeSpan, 0, formerPart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(smallSizeSpan, formerPart.length(), formerPart.length() + laterPart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(redSpan, 0, formerPart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(blueSpan, formerPart.length(), formerPart.length() + laterPart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(imageSpan, 3, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//        SpannableStringBuilder builder = new SpannableStringBuilder(spannableString);
//        builder.setSpan(imageSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String contentStr = "<font size='" + 50 + "'>" + 12 + "</font> <font size='" + 30 + "'>K币</font>";

        // color='#000000'

//        testTextView.setText(Html.fromHtml(contentStr));
        testTextView.setText(spannableString);
//        for(;;){
//            int i =999;
//            int j = 999;
//            int k = i*j;
////              Log.d(TAG, " only print log");
//        }

//        Log.d(TAG, "long sleep begin");
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, "long sleep end");
    }

    private void loadRoundCornerBitmapWith565() {
        BitmapFactory.Options optionsA565 = new BitmapFactory.Options();
        optionsA565.inPreferredConfig = Bitmap.Config.RGB_565;

        BitmapFactory.Options options565 = new BitmapFactory.Options();
        options565.inPreferredConfig = Bitmap.Config.RGB_565;

        BitmapFactory.Options options888 = new BitmapFactory.Options();
//        options888.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap roundCornerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher, optionsA565);
        mImageView.setImageBitmap(roundCornerBitmap);

        Bitmap bitmap565 = BitmapFactory.decodeResource(getResources(), R.drawable.medium_img, options565);
        Bitmap bitmap888 = BitmapFactory.decodeResource(getResources(), R.drawable.medium_img, options888);
//        Bitmap bitmap565 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher, options565);
//        Bitmap bitmap888 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher, options888);
        Loger.d(TAG, "-->loadRoundCornerBitmapWith565(), [w,h]=[" + bitmap565.getWidth() + ", " + bitmap565.getHeight()
                + "] 565 byte cnt=" + bitmap565.getByteCount()
                + ", allocated cnt=" + bitmap565.getAllocationByteCount());
        Loger.d(TAG, "-->loadRoundCornerBitmapWith565(), [w,h]=[" + bitmap888.getWidth() + ", " + bitmap888.getHeight() +
                "] 8888 byte cnt=" + bitmap888.getByteCount()
                + ", allocated cnt=" + bitmap888.getAllocationByteCount());
        Loger.d(TAG, "-->loadRoundCornerBitmapWith565(), [w,h]=[" + roundCornerBitmap.getWidth() + ", " + roundCornerBitmap.getHeight()
                + "], alpha byte cnt=" + roundCornerBitmap.getByteCount()
                + ", allocated cnt=" + roundCornerBitmap.getAllocationByteCount());
    }

    @Override
    protected void onStart() {
        Loger.d(TAG, "-->onStart(): ");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Loger.d(TAG, "-->onRestart(): ");
        super.onRestart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Loger.d(TAG, "-->onSaveInstanceState(): ");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Loger.d(TAG, "-->onRestoreInstanceState(): ");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Loger.d(TAG, "-->onNewIntent(): ");
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        Loger.d(TAG, "-->onPause(): ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Loger.d(TAG, "-->onStop(): ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Loger.d(TAG, "-->onDestroy(): ");
        super.onDestroy();
    }
}
