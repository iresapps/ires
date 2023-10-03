package com.example.ires;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class first_aidActivity extends AppCompatActivity {
    private ViewFlipper first_aid_fviewer;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_first_aid );
        first_aid_fviewer = findViewById ( R.id.first_aid_view );
        ImageView imageView = new ImageView ( this );

    }
    public void FpreviousView( View v) {
        first_aid_fviewer.setInAnimation(this, android.R.anim.slide_in_left);
        first_aid_fviewer.setOutAnimation(this, android.R.anim.slide_out_right);
        first_aid_fviewer.showPrevious();
    }

    public void FnextView(View v) {

        first_aid_fviewer.setInAnimation(this, R.anim.slide_in_right);
        first_aid_fviewer.setOutAnimation(this, R.anim.slide_out_left);
        first_aid_fviewer.showNext();
    }
}