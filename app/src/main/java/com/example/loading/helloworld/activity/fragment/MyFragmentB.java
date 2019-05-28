package com.example.loading.helloworld.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loading.common.utils.Loger;

public class MyFragmentB extends Fragment {
    public static final String TAG = "MyFragmentB";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        Loger.d(TAG, "-->onCreateView()");
        textView.setText("                                  Fragment B");
        textView.setAllCaps(true);
        return textView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Loger.d(TAG, "-->onAttach()");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Loger.d(TAG, "-->onCreate()");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Loger.d(TAG, "-->onViewCreated()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Loger.d(TAG, "-->onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Loger.d(TAG, "-->onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Loger.d(TAG, "-->onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Loger.d(TAG, "-->onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Loger.d(TAG, "-->onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Loger.d(TAG, "-->onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Loger.d(TAG, "-->onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Loger.d(TAG, "-->onDetach()");
    }
}
