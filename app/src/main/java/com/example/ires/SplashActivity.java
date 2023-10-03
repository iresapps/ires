package com.example.ires;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashActivity extends AppCompatActivity {

    private ImageView logo,title;
    private TextView slogan;

    Animation topAnimation, bottomAnimation,bottom_v ;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo_apps);
        title = findViewById(R.id.kid_logo);
        slogan = findViewById(R.id.slogan);

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.to_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        bottom_v = AnimationUtils.loadAnimation(this, R.anim.bottom_v);

        slogan.setAnimation(topAnimation);
        logo.setAnimation(bottom_v);
        title.setAnimation(bottomAnimation);


        new Handler ().postDelayed( new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent (SplashActivity.this,MainActivity.class));
                finish();
            }
        }, 4000);
    }
}