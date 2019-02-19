package com.example.loading.helloworld.lottie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.loading.helloworld.R;

/**
 * Created by loading on 2018/8/21.
 */

public class LottieTestActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lottie);


        RecyclerView r;
        LottieAnimationView lottieView = (LottieAnimationView) findViewById(R.id.lottie_view);

        lottieView.setAnimation("ai_boring_watcharound.json");
        lottieView.loop(true);
        lottieView.playAnimation();
    }
}
