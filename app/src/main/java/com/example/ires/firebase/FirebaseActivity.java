package com.example.ires.firebase;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ires.CallerLogsActivity;
import com.example.ires.EmergencyActivity;
import com.example.ires.MainActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.app.PendingIntent.getActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class FirebaseActivity extends AppCompatActivity  {

    private String currentUserId = FirebaseAuth.getInstance().getUid();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(currentUserId);
    private String nameRef;
    private String numberRef;
    private String message;
    private ArrayList<String> phonenumbers;
    private String locationUrl = "http://www.google.com/maps/place/";
    private static final int REQUEST_CODE = 101;
    private static final int REQUEST_CODE_SMS = 0;
    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.firebaseviewer);
        FireBaseConn conn = new FireBaseConn();
//        phonenumbers.add(database.child("phone").get().toString());
        //get the spinner from the xml.

        Button fireButton = findViewById(R.id.fire_btn);
        fireButton.setText(Incidents.fire.name());
        Button crimeButton = findViewById(R.id.crime_btn);
        crimeButton.setText(Incidents.crimes.name());
        Button medicalButton = findViewById(R.id.medical_btn);
        medicalButton.setText(Incidents.medical_emergencies.name());
        TextView emergencyContext = findViewById(R.id.txt_context);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameRef = snapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fireButton.setOnClickListener(v -> SubmitButton(conn, 0, mFirebaseAnalytics));
        crimeButton.setOnClickListener(v -> SubmitButton(conn, 1, mFirebaseAnalytics));
        medicalButton.setOnClickListener(v -> SubmitButton(conn, 2, mFirebaseAnalytics));
    }
    private void SubmitButton(FireBaseConn conn, int selected, FirebaseAnalytics mFirebaseAnalytics){
        Bundle bundle = new Bundle();
        bundle.putString("IncidentType", Incidents.values()[selected].name());
        mFirebaseAnalytics.logEvent("Incident", bundle);
        Calendar setTime = Calendar.getInstance();
        if(setTime.get(Calendar.AM_PM) != Calendar.AM){
            setTime.set(setTime.getTime().getYear()
                    ,setTime.getTime().getMonth()
                    ,setTime.getTime().getDay()
                    ,12 + setTime.getTime().getHours()
                    , setTime.getTime().getMinutes()
                    , setTime.getTime().getSeconds());
        }
        conn.SendToDashboard(
                selected
                , nameRef
                , GetNumber()
                , setTime);
        startActivity(new Intent(FirebaseActivity.this, EmergencyActivity.class));
        CallerLogsActivity caller = new CallerLogsActivity();
        caller.setCallLogs(this);
        showEmergencyAlertDialog();
    }
    private void showEmergencyAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.emergency_alertdialog_template, null);
        builder.setView(dialogView);
        builder.setTitle("Emergency Detected!");
        builder.setMessage("Do you wanna Call 911 and Send Message to your close ones?");
        builder.setIcon(R.drawable.eme);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final Button positive = dialogView.findViewById(R.id.positiveButton);
        Button negative = dialogView.findViewById(R.id.negativeButton);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Make an emergency call
                Log.d("tag", message);
                if(loadFunctionsFromSharedPreference("call"))
                    callEmergency();
                if(loadFunctionsFromSharedPreference("message"))
                    sendMessages();

                dialog.cancel();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }
    public void callEmergency(){
        if( ContextCompat.checkSelfPermission(FirebaseActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FirebaseActivity.this, new String[] { android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }
        //make call to 911

        else{
            String phoneNumber = "09214819524"; // Replace with the desired phone number

            // Create an intent to start the CallerLogsActivity


            //Ndrrmc NO.
            // 09089217888
            // 09700727933 te ann
            //09706980888 dap
            //  String dial = "tel:" + "09214819524";

            //  startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

        }
    }
    private boolean loadFunctionsFromSharedPreference(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("function", MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }
    private void sendMessages() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    { android.Manifest.permission.SEND_SMS}, REQUEST_CODE_SMS);
        }

        StringBuilder numbers = new StringBuilder();
        for (String number: phonenumbers){
            SmsManager smsManager = SmsManager.getDefault();
            Log.d("mes", message);
            Log.d("mes", locationUrl);
            smsManager.sendTextMessage(number, null, message, null, null);


            numbers.append(number).append("\n");
        }
        Toast.makeText(this, "Message sent to:\n" + numbers.toString(), Toast.LENGTH_SHORT).show();
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
