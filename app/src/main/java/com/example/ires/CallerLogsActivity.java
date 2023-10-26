package com.example.ires;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class CallerLogsActivity extends Activity {

//    private static final int REQUEST_READ_CALL_LOG = 1;
//    String phoneNumber = getIntent().getStringExtra("phoneNumber");

    private void loadCallLog(ListView callLogListView) {
        List<CallLogEntry> callLogEntries = new ArrayList<>();
        CallLogEntry callLogEntry = null;
        List<String> callerNameEntries = new ArrayList<>();
        List<String> callerNumberEntries = new ArrayList<>();
        callerNameEntries.add("John Luc");
        callerNumberEntries.add("09098031481");
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
//        CallLogAdapter adapter = new CallLogAdapter(this, callLogEntries);
//        callLogListView.setAdapter(adapter);
        ArrayAdapter<String> NumberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, callerNumberEntries);
//        ArrayAdapter<String> NameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, callerNameEntries);
        callLogListView.setAdapter(NumberAdapter);
//        callLogListView.setAdapter(NameAdapter);
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
            loadCallLog(callLogListView);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1/*REQUEST_READ_CALL_LOG*/) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load call log
                ListView callLogListView = findViewById(R.id.callLogListView);
                loadCallLog(callLogListView);
            } else {
                // Permission denied, show a message
                Toast.makeText(this, "Permission denied. Cannot access call log.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
