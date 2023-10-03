package com.example.ires;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class categorysignup extends AppCompatActivity {

    private Button responder, caller;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_categorysignup );

        responder = findViewById ( R.id.responder_btn );
        caller = findViewById ( R.id.caller_btn );


     /*   responder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(categorysignup.this, responder_signup.class);
                startActivity(intent);
            }
        });*/
        caller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(categorysignup.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }
}