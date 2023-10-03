package com.example.ires;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class crimepreventionActivity extends AppCompatActivity {
    private ViewFlipper crime_flipper_view;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_crimeprevention );
        crime_flipper_view = findViewById ( R.id.crime_flipper_view );
        ImageView imageView = new ImageView ( this );

    }
    public void previousV( View v) {
        crime_flipper_view.setInAnimation(this, android.R.anim.slide_in_left);
        crime_flipper_view.setOutAnimation(this, android.R.anim.slide_out_right);
        crime_flipper_view.showPrevious();
    }

    public void nextV(View v) {

        crime_flipper_view.setInAnimation(this, R.anim.slide_in_right);
        crime_flipper_view.setOutAnimation(this, R.anim.slide_out_left);
        crime_flipper_view.showNext();
    }
}