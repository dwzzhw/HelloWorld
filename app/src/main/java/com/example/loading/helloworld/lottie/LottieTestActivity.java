package com.example.loading.helloworld.lottie;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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
