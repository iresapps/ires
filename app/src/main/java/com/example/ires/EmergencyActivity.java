package com.example.ires;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

public class EmergencyActivity extends AppCompatActivity {

   private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static final int REQUEST_CODE_SMS = 0;
    private static final int REQUEST_CALL = 1;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

       fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        database = FirebaseDatabase.getInstance();
        currentUserId = FirebaseAuth.getInstance().getUid();

        countTextView = findViewById(R.id.countTextView);
        stopButton = findViewById(R.id.stopButton);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        myRef = database.getReference(currentUserId).child("phones");
        myRefName = database.getReference(currentUserId).child("name");

//        updateNumbers();
//        initializeEmergencyContacts();
        createMessage();

        count = loadTimeFromSharedPreferences();
        countTextView.setText(count+"");
        vibrate();

        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(count == 0){
                                    stopButton.setEnabled(false);
                                    interrupt();

                                    //Make an emergency call
                                    Log.d("tag", message);
                                    if(loadFunctionsFromSharedPreference("call"))
                                        callEmergency();

                                    if(loadFunctionsFromSharedPreference("message"))
                                        sendMessages();

                                    finish();
                                }
                                else {
                                    count--;
                                    countTextView.setText(count+"");
                                    vibrate();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread.interrupt();
                countTextView.setText("Stopped");
                finish();
            }
        });
    }

    private int loadTimeFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("timer", MODE_PRIVATE);
        return sharedPreferences.getInt("time", 5);
    }

    private boolean loadFunctionsFromSharedPreference(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("function", MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }

    private String loadLocationFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("location_url", MODE_PRIVATE);
        return sharedPreferences.getString("location", null);
    }

    private void vibrate() {
        if ( Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate( VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else{
            vibrator.vibrate(500);
        }
    }

    private void sendMessages() {
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.SEND_SMS}, REQUEST_CODE_SMS);
        }
        StringBuilder numbers = new StringBuilder();
        for (String number: phonenumbers){
//            Log.d("mes", number);
//            Log.d("mes", message);
//            Log.d("mes", locationUrl);

            android.telephony.SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            numbers.append(number + "\n");
        }
        Toast.makeText(this, "Message sent to:\n" + numbers, Toast.LENGTH_SHORT).show();
    }
    private void initializeEmergencyContacts(){
        DatabaseReference numberReference = FirebaseDatabase.getInstance().getReference().child(currentUserId).child("numbers");
        numberReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                phonenumbers.clear();
                for (DataSnapshot snap:snapshot.getChildren()) {
                    phonenumbers.add(snap.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }
    public void callEmergency() {
        String phoneNumber = "09214819524";
        makeCall(phoneNumber);
        ContentValues values = new ContentValues();
        values.put( CallLog.Calls.NUMBER, phoneNumber);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        values.put(CallLog.Calls.DURATION, 0); // Set your call duration here
        values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);

        // Set a custom flag to identify your application's calls
        values.put(CallLog.Calls.FEATURES, "AppFlag"); // Replace with your custom identifier

        // Insert the call into the call log
        getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
    }


    private void makeCall(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EmergencyActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + phoneNumber;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }


    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task < Location > task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener <Location> () {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    locationUrl = locationUrl + location.getLatitude() + "," + location.getLongitude();
                    message = message + "Location: " + locationUrl;
                }
                else{
                    Log.d("tag", "problem fetching location");
                    message = message + "Previous location: " + loadLocationFromSharedPreferences();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode , permissions , grantResults );
        switch ( requestCode ) {
            case REQUEST_CODE:
                if ( grantResults.length > 0 && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) {
                    fetchLastLocation ( );
                }
                break;

            case REQUEST_CODE_SMS:
                if ( grantResults.length > 0 && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) {
                    sendMessages ( );
                }
                break;

            case REQUEST_CALL:
                if ( grantResults.length > 0 && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) {
                    callEmergency ( );
                }
                break;
        }
    }

    private void updateNumbers() {

        phonenumbers = new ArrayList<String>();


        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String phone = dataSnapshot.getValue(String.class);
                phonenumbers.add(phone);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String phone = dataSnapshot.getValue(String.class);
                phonenumbers.remove(phone);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createMessage() {
        myRefName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user = dataSnapshot.getValue(String.class);
                message = user + " is in DANGER!\n";
                fetchLastLocation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
