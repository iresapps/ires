package com.example.ires;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    private ListView phoneListView;
    private ArrayList<String> phonenumbers = new ArrayList<>();
    private Toolbar toolbar;
    private TextView nameTextBox, addnumberTextView;
    private AlertDialog.Builder alertdialog;
    private Vibrator vibrator;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String currentUserId, numberDel;
    private DatabaseReference myRef, myRefName;
    private Dialog dialog;
    Context context = this;
//    SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.User), Context.MODE_PRIVATE);
//    SharedPreferences.Editor myEdit = sharedPref.edit();

    public void ChangeOnClick() {
        dialog = new Dialog(SettingsActivity.this);
        dialog.setContentView(R.layout.dialog_template);
        final EditText dialogEditText = dialog.findViewById(R.id.dialogEditText);
        Button dialogButton = dialog.findViewById(R.id.dialogButton);
        dialogEditText.setEnabled(true);
        dialogButton.setEnabled(true);
        dialog.show();
        dialogEditText.setHint("Change name");
        dialogButton.setText("Set");
        dialogButton.setOnClickListener( v -> {
            String name = dialogEditText.getText().toString();
            if (name.isEmpty()) {
                dialogEditText.setError("Enter your name");
                dialogEditText.requestFocus();
            }
            else{
                myRefName.setValue(name);
                dialog.cancel();
            }
        });
    }
//    private void UpdateUserNumber(String PhoneNumber){
//        myEdit.putString("UserNumber", PhoneNumber);
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Setting Custom Toolbar
        toolbar = findViewById(R.id.activity_settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        //Adding back button on action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        // Set Phone Number
//        TextView PhoneTextBox = findViewById(R.id.PhoneTextBox);
//        String userPhoneNum = PhoneTextBox.getText().toString();
//        if(userPhoneNum == null){userPhoneNum = " "; }
//        PhoneTextBox.setText(sharedPref.getString("UserNumber", userPhoneNum));
//
//        // Collect phone number
//        Button UserPhoneNumber_btn = findViewById(R.id.changeUserNum_btn);
//        String finalUserPhoneNum = userPhoneNum;
//        UserPhoneNumber_btn.setOnClickListener(v -> {UpdateUserNumber(finalUserPhoneNum);});

        currentUserId = FirebaseAuth.getInstance().getUid();

        myRef = database.getReference(currentUserId).child("phones");
        myRef.keepSynced(true);
        myRefName = database.getReference(currentUserId).child("name");
        myRefName.keepSynced(true);

        Button changeNamebtn = findViewById(R.id.changeName_btn);
        nameTextBox = findViewById(R.id.NameTextBox);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        updateNumbers();
        updateName();

        phoneListView = findViewById(R.id.phoneListView);
        addnumberTextView = findViewById(R.id.addnumberTextView);
        addnumberTextView.setPaintFlags(addnumberTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        changeNamebtn.setOnClickListener(v -> { ChangeOnClick(); });

        addnumberTextView.setOnClickListener(v -> {
            dialog = new Dialog(SettingsActivity.this);
            dialog.setContentView(R.layout.dialog_template);
            final EditText dialogEditText = dialog.findViewById(R.id.dialogEditText);
            Button dialogButton = dialog.findViewById(R.id.dialogButton);
            dialogEditText.setEnabled(true);
            dialogButton.setEnabled(true);
            dialog.show();
            dialogEditText.setHint("Add number");
            dialogEditText.setInputType(InputType.TYPE_CLASS_PHONE);
            dialogButton.setText("Add");
            dialogButton.setOnClickListener(ve -> {
                String phone = dialogEditText.getText().toString().trim();

                //Checking validity of number
                if (phone.isEmpty()) {
                    dialogEditText.setError("Enter a phone number");
                    dialogEditText.requestFocus();
                } else if (phone.length() != 11) {                            //for being safe
                    dialogEditText.setError("Enter a valid phone number");
                    dialogEditText.requestFocus();
                } else if (!Patterns.PHONE.matcher(phone).matches()) {
                    dialogEditText.setError("Enter a valid phone number");
                    dialogEditText.requestFocus();
                } else {
                    //Valid
                    myRef.push().setValue(phone);
                    dialog.cancel();
                }
            });
        });


        phoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Long Press to Update", Toast.LENGTH_SHORT).show();
            }
        });

        phoneListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                numberDel = phonenumbers.get(position);

                vibrator.vibrate(50);

                showAlertDialog();
                return false;
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.update_number_template, null);
        builder.setView(dialogView);
        builder.setTitle("Update Number");
        builder.setIcon(R.drawable.exit);
        final AlertDialog dialog = builder.create();
        dialog.show();


        final EditText number = dialogView.findViewById(R.id.dialogNumberEditText);
        final Button update = dialogView.findViewById(R.id.updateButton);
        Button delete = dialogView.findViewById(R.id.deleteButton);

        number.setText(numberDel);
        update.setOnClickListener(v -> {
            String updatedNumber = number.getText().toString().trim();
            if(TextUtils.isEmpty(updatedNumber)) {
                number.setError("Enter a phone number");
                number.requestFocus();
            }
            else if (updatedNumber.length() != 11) {                            //for being safe
                number.setError("Enter a valid phone number");
                number.requestFocus();
            }
            else if (!Patterns.PHONE.matcher(updatedNumber).matches()) {
                number.setError("Enter a valid phone number");
                number.requestFocus();
            }
            else{
                //Valid
                deleteNumber();
                myRef.push().setValue(updatedNumber);
                dialog.cancel();
            }
        });
        delete.setOnClickListener(v-> {
            deleteNumber();
            dialog.cancel();
        });
    }

    private void deleteNumber() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    if(childSnapShot.getValue().equals(numberDel)){
                        myRef.child(childSnapShot.getKey()).removeValue();
                    }
                }
                numberDel = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings){

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.advanced_settings_template, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();
            dialog.show();

            final CheckBox call = dialogView.findViewById(R.id.callCheckBox);
            final CheckBox message = dialogView.findViewById(R.id.messageCheckBox);
            Button saveButton = dialogView.findViewById(R.id.saveButton);
            final Spinner countSpinner = dialogView.findViewById(R.id.countSpinner);

            //Adapter for spinner
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.count_times, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            countSpinner.setAdapter(adapter);

            ArrayList<String> countItems = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.count_times)));
            countSpinner.setSelection(countItems.indexOf(loadTimeFromSharedPreferences() + ""));

            //keepFunctionsSelected
            call.setChecked(loadFunctionsFromSharedPreference("call"));
            message.setChecked(loadFunctionsFromSharedPreference("message"));

            saveButton.setOnClickListener(v -> {
                saveTimeOnSharedPreferences(Integer.parseInt(countSpinner.getSelectedItem().toString()));
                saveFunctionsOnSharedPreference("call", call.isChecked());
                saveFunctionsOnSharedPreference("message", message.isChecked());
                dialog.cancel();
            });


        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTimeOnSharedPreferences(int sec) {
        SharedPreferences sharedPreferences = getSharedPreferences("timer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("time", sec);
        editor.commit();
    }

    private int loadTimeFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("timer", MODE_PRIVATE);
        return sharedPreferences.getInt("time", 5);
    }

    private void saveFunctionsOnSharedPreference(String key, boolean state) {
        SharedPreferences sharedPreferences = getSharedPreferences("function", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, state);
        editor.commit();
    }

    private boolean loadFunctionsFromSharedPreference(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("function", MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }

    private void updateName() {
        myRefName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                nameTextBox.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showNumbers() {
        CustomAdapter customAdapter = new CustomAdapter(SettingsActivity.this, phonenumbers, R.drawable.about);
        phoneListView.setAdapter(customAdapter);
    }

    private void updateNumbers() {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String phone = dataSnapshot.getValue(String.class);
                phonenumbers.add(phone);
                showNumbers();

                Log.d("tag", "added");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String phone = dataSnapshot.getValue(String.class);
                phonenumbers.remove(phone);
                showNumbers();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
