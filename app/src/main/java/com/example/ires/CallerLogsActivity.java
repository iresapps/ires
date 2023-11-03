package com.example.ires;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CallerLogsActivity extends Activity {

//    private static final int REQUEST_READ_CALL_LOG = 1;
//    String phoneNumber = getIntent().getStringExtra("phoneNumber");

//    private void loadCallLog(ListView callLogListView) {
//        List<CallLogEntry> callLogEntries = new ArrayList<>();
//        CallLogEntry callLogEntry = null;
//        List<String> callerNumberEntries = new ArrayList<>();
        // Query the call log and retrieve call details for calls made by your application
//        String[] projection = {
//                CallLog.Calls._ID,
//                CallLog.Calls.NUMBER,
//                CallLog.Calls.CACHED_NAME,
//                CallLog.Calls.DATE,
//                CallLog.Calls.DURATION,
//                CallLog.Calls.TYPE
//        };

        // Add a filter for your custom flag
//        String selection = CallLog.Calls.FEATURES + " = ?";
//        String[] selectionArgs = {"AppFlag"}; // Replace with your custom identifier
//        String sortOrder = CallLog.Calls.DATE + " DESC";

//        Cursor cursor = getContentResolver().query(
//                CallLog.Calls.CONTENT_URI,
//                projection,
//                selection,
//                selectionArgs,
//                sortOrder
//        );

//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
//                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
//                @SuppressLint("Range") long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
//                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
//                @SuppressLint("Range") int callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
//
//                // Create a CallLogEntry object and add it to the list
//                callLogEntry = new CallLogEntry(name, number, date, duration, callType);
//                callLogEntries.add(callLogEntry);
//            }
//            cursor.close();
//        }

        // Create and set the adapter for the ListView
//        ArrayAdapter<String> NumberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, callerNumberEntries);
//        callLogListView.setAdapter(NumberAdapter);

//    }
    ArrayList<String> callLogsBuffer = new ArrayList<>();
    public void setCallLogs(Context context){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("phoneLogs", Context.MODE_PRIVATE);
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI,
                null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        managedCursor.moveToNext();
        String phNumber = managedCursor.getString(number);
        String callType = managedCursor.getString(type);
        String callDate = managedCursor.getString(date);
        Date callDayTime = new Date(Long.valueOf(callDate));
        String callDuration = managedCursor.getString(duration);
        String dir = null;
        int dircode = Integer.parseInt(callType);
        switch (dircode) {
            case CallLog.Calls.OUTGOING_TYPE:
                dir = "OUTGOING";
                break;
            case CallLog.Calls.INCOMING_TYPE:
                dir = "INCOMING";
                break;
            case CallLog.Calls.MISSED_TYPE:
                dir = "MISSED";
                break;
        }
        callLogsBuffer.add("\nPhone Number: " + phNumber + " \nCall Type: "
                + dir + " \nCall Date: " + callDayTime
                + " \nCall duration in sec : " + callDuration + "\n");
        managedCursor.close();
        Set<String> set = new HashSet<>();
        set.addAll(callLogsBuffer);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("phoneLogs", set);
        editor.commit();
    }
    private void getCallLogs(ListView callLogListView) {
        ArrayAdapter<String> logAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, callLogsBuffer);
        callLogListView.setAdapter(logAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caller_logs);

        ListView callLogListView = findViewById(R.id.callLogListView);

//        // Check if permission to read call log is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    1);
        } else {
//            // Permission is already granted, proceed to load call log
            getCallLogs(callLogListView);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1/*REQUEST_READ_CALL_LOG*/) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load call log
                ListView callLogListView = findViewById(R.id.callLogListView);
                getCallLogs(callLogListView);
            } else {
                // Permission denied, show a message
                Toast.makeText(this, "Permission denied. Cannot access call log.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
