package com.example.ires.firebase;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ires.R;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.telephony.TelephonyManager;
import android.content.Context;

import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.app.PendingIntent.getActivity;

import java.util.Calendar;

public class FirebaseActivity extends AppCompatActivity  {

    String currentUserId = FirebaseAuth.getInstance().getUid();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(currentUserId);
    String nameRef;
    String numberRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.firebaseviewer);
        FireBaseConn conn = new FireBaseConn();
        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.ddl_incidents);
        Button submit = findViewById(R.id.submit_firebase);
        //create a list of items for the spinner.
        String[] items = new String[Incidents.values().length];
        int i = 0;
        for (Incidents c: Incidents.values()) {
            items[i] = c.name();
            i += 1;
        }
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameRef = snapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Bundle bundle = new Bundle();
        int selected = dropdown.getSelectedItemPosition() + 1;
        bundle.putString("IncidentType", Incidents.values()[selected].name());
        mFirebaseAnalytics.logEvent("Incident", bundle);
        // sends data on click
        submit.setOnClickListener(v -> conn.SendToDashboard(
                selected
                , nameRef
                , GetNumber()
                , Calendar.getInstance())
        );
    }
    // Function will run after click to button
    private String GetNumber() {

        if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // Permission check

            // Create obj of TelephonyManager and ask for current telephone service
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            numberRef = telephonyManager.getLine1Number();
            return numberRef;
        } else {
            // Ask for permission
            requestPermission();
        }
        return "";
    }

    private void requestPermission()
    {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 100) {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        numberRef = telephonyManager.getLine1Number();
    }
}
