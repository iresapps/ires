package com.example.ires;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class tipActivity extends AppCompatActivity {
        private ImageButton fire, firstaidbtn, crime;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_tip );
        fire = findViewById ( R.id.fireaidbtn );
        firstaidbtn = findViewById ( R.id.firstaidbtn );
        crime = findViewById ( R.id.crimeprev_btn );
        fire.setOnClickListener ( new View.OnClickListener ( ) {
                                      @Override
                                      public void onClick ( View v ) {
                                          startActivity(new Intent (tipActivity.this, fire_Activity.class));
                                      }
                                  }

        );

        firstaidbtn .setOnClickListener ( new View.OnClickListener ( ) {
                                            @Override
                                            public void onClick ( View v ) {
                                                startActivity(new Intent (tipActivity.this, first_aidActivity.class));
                                            }
                                        }

        );
        crime.setOnClickListener ( new View.OnClickListener ( ) {
                                  @Override
                                  public void onClick ( View v ) {
                                      startActivity(new Intent (tipActivity.this, crimepreventionActivity.class));
                                  }
                              }

        );


    }


}