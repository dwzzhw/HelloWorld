package com.example.loading.helloworld.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.example.loading.helloworld.R;
import com.example.loading.helloworld.data.Student;
import com.example.loading.helloworld.databinding.ActivityConstraintTestBinding;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;

public class ConstraintLayoutTestActivity extends BaseActivity {
    public static final String TAG = "ConstraintLayoutTestActivity";

    private Student student;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityConstraintTestBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_constraint_test);
        student = new Student("Loading", "001", 88);
        binding.setStuInfo(student);

//        setContentView(R.layout.activity_constraint_test);
    }

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
    }
}
