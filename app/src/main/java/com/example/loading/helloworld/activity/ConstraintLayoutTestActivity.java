package com.example.loading.helloworld.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.example.loading.helloworld.R;
import com.example.loading.helloworld.data.Student;
import com.example.loading.helloworld.databinding.ActivityConstraintTestBinding;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;

public class ConstraintLayoutTestActivity extends BaseActivity {
//    public static final String TAG = "ConstraintLayoutTestActivity";
    private static final String TAG = "ActivitySwitch_B";

    private Student student;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Loger.d(TAG, "-->onCreate(): ");
        super.onCreate(savedInstanceState);

        ActivityConstraintTestBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_constraint_test);
        student = new Student("Loading", "001", 88);
        binding.setStuInfo(student);

//        setContentView(R.layout.activity_constraint_test);
    }

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
    }

    @Override
    protected void onStart() {
        Loger.d(TAG, "-->onStart(): ");
        super.onStart();
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
    protected void onResume() {
        Loger.d(TAG, "-->onResume(): ");
        super.onResume();
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
