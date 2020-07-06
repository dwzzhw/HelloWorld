package com.example.loading.helloworld.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loading.common.utils.Loger;

public class MyFragmentA extends Fragment {
    public String TAG = "MyFragmentA";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Loger.d(TAG, "-->onCreateView()");
        TextView textView = new TextView(getContext());
        textView.setText("Fragment A");
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Loger.d(TAG, "-->setUserVisibleHint(), isVisibleToUser=" + isVisibleToUser);
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
