package com.example.ires;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ires.firebase.FirebaseActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // private static final int REQUEST_PHONE_PERMISSION = 1;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private AlertDialog.Builder alertdialog;
    private Dialog dialog;
    private ImageButton tips, logs;
    private CardView emergencyCardView, contactsCardView, settingsCardView;

    private Button btn_firebase;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef, myRefName;
    private FirebaseDatabase database;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static final int REQUEST_CODE_SMS = 0;
    private static final int REQUEST_CALL = 1;
    private String locationUrl = "http://www.google.com/maps/place/", currentUserId, message;
    private ArrayList <String> phonenumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializes the firebase SDK connection
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        btn_firebase = findViewById(R.id.btnFireBase);
        btn_firebase.setOnClickListener(v -> startActivity(
                new Intent (MainActivity.this
                , com.example.ires.firebase.FirebaseActivity.class))
        );

        mAuth = FirebaseAuth.getInstance();
        tips = findViewById ( R.id.Tips );
        logs = findViewById ( R.id.Logs );
        //Setting Custom Toolbar
        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        //Click toggle to open Menu
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        requestPermissions();

        database = FirebaseDatabase.getInstance();
        currentUserId = FirebaseAuth.getInstance().getUid();

        //Check if the user is already logged in
        FirebaseUser currentUserInfo = mAuth.getCurrentUser();
        if (currentUserInfo == null) {
            sendUserToLoginActivity();
        }
        else{
            checkUserHasAddedNumbers();
        }

        if(currentUserId != null) {
            myRef = database.getReference(currentUserId).child("phones");
            myRefName = database.getReference(currentUserId).child("name");

            updateNumbers();
            createMessage();
        }


        navigationView = findViewById(R.id.navigation_view);
        //Including navigation_header
        navigationView.inflateHeaderView(R.layout.navigation_header);
        //On item selected
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItemSelected(menuItem);
                return false;
            }
        });

        emergencyCardView = findViewById(R.id.emergencyCardView);
        contactsCardView = findViewById(R.id.contactCardView);
        settingsCardView = findViewById(R.id.settingsCardView);
        emergencyCardView.setOnClickListener(this);
        contactsCardView.setOnClickListener(this);
        settingsCardView.setOnClickListener(this);
        logs.setOnClickListener ( this );
        tips.setOnClickListener ( this );
    }

    @Override
    public void onBackPressed() {

        //AlertDialog
        alertdialog = new AlertDialog.Builder(MainActivity.this);
        alertdialog.setTitle("Exit");
        alertdialog.setMessage("Do you want to exit?");
        alertdialog.setIcon(R.drawable.exit);
        alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertdialog.setNegativeButton("No", null);
        alertdialog.setCancelable(false);

        alertdialog.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        //Check if the user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendUserToLoginActivity();
        }
        else{
            checkUserHasAddedNumbers();
        }
    }*/


    private void checkUserHasAddedNumbers() {
        database.getReference().addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                    //AlertDialog

                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_setupnewuser, null);
                    builder.setView(dialogView);
                    final AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();

                    Button nextButton = dialogView.findViewById(R.id.dialogButton);

                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendUserToSettingsActivity();
                            finish();
                            dialog.cancel();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emergencyCardView:
                startActivity(new Intent(MainActivity.this, EmergencyActivity.class));
                showEmergencyAlertDialog();
                break;
            case R.id.contactCardView:
                startActivity(new Intent (MainActivity.this, ContactActivity.class));
                break;
            case R.id.settingsCardView:
                sendUserToSettingsActivity();
                break;
            case R.id.Tips:
                startActivity(new Intent (MainActivity.this, tipActivity.class));
                break;

            case R.id.Logs:
                startActivity(new Intent (MainActivity.this, CallerLogsActivity.class));
                break;
            case R.id.btnFireBase:
                startActivity(new Intent (MainActivity.this, FirebaseActivity.class));
                break;
        }
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

    private boolean loadFunctionsFromSharedPreference(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("function", MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }


    public void callEmergency(){
        if( ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
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

    private void updateNumbers() {

        phonenumbers = new ArrayList<>();


        myRef.addChildEventListener(new ChildEventListener () {
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
                message = user + " is in an EMERGENCY!\n";
                fetchLastLocation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void menuItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_settings:
                sendUserToSettingsActivity();
                break;
            case R.id.nav_about:
                aboutDeveloper();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                sendUserToLoginActivity();
                break;
        }
    }

    private void aboutDeveloper() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.about_template);

        final TextView emailTextView = dialog.findViewById(R.id.emailTextView);
        emailTextView.setPaintFlags(emailTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);       //for underlining text
        emailTextView.setEnabled(true);
        emailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send email
                String[] recipients = {emailTextView.getText().toString()};

                String subject = "FEEDBACK about " + getString(R.string.app_name);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);

                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Choose an email client"));
            }
        });
        dialog.show();
    }

    private void fetchLastLocation() {
        Log.d("tag", "inside");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    { android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task < Location > task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener <Location> () {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    locationUrl = locationUrl + location.getLatitude() + "," + location.getLongitude();
                    saveLocationOnSharedPreferences(locationUrl);
                    message = message + "Location: " + locationUrl;
                    locationUrl = "http://www.google.com/maps/place/";
                }
                else{
                    Log.d("tag", "problem fetching location");
                    message = message + "Previous location: " + loadLocationFromSharedPreferences();
                }
            }
        });
    }

    private void fetchLocation() {
        Log.d("tag", "inside");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    { android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    locationUrl = locationUrl + location.getLatitude() + "," + location.getLongitude();
                    saveLocationOnSharedPreferences(locationUrl);
                    locationUrl = "http://www.google.com/maps/place/";
                }
            }
        });
    }

    private String loadLocationFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("location_url", MODE_PRIVATE);
        return sharedPreferences.getString("location", null);
    }

    private void saveLocationOnSharedPreferences(String loc) {
        SharedPreferences sharedPreferences = getSharedPreferences("location_url", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("location", loc);
        editor.commit();
    }

    private void sendUserToSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void requestPermissions() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.CALL_PHONE, android.Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }


}
