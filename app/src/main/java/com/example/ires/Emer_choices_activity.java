package com.example.ires;

import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Emer_choices_activity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static final int REQUEST_CODE_SMS = 0;
    private static final int REQUEST_CALL = 1;
    private String emergencyType = "medical";
    private String locationUrl = "http://www.google.com/maps/place/";
    private String message, currentUserId;
    private ArrayList <String> phonenumbers;
    private FirebaseDatabase database;
    private DatabaseReference myRef, myRefName;

    private TextView countTextView;
    private ImageButton stopButton;
    Vibrator vibrator;
    private int count;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_emer_choices );
    }
    }


