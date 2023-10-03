package com.example.ires;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;
public class fire_Activity extends AppCompatActivity {
    private ViewFlipper flipperview;
    private ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_fire );
        flipperview = findViewById ( R.id.flipper_view );
        ImageView imageView = new ImageView ( this );

    }
    public void previousView( View v) {
        flipperview.setInAnimation(this, android.R.anim.slide_in_left);
        flipperview.setOutAnimation(this, android.R.anim.slide_out_right);
        flipperview.showPrevious();
    }

    public void nextView(View v) {

        flipperview.setInAnimation(this, R.anim.slide_in_right);
        flipperview.setOutAnimation(this, R.anim.slide_out_left);
        flipperview.showNext();
    }
}