package com.example.ires;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class CustomCallLogsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_call_logs);

        ListView customCallLogListView = findViewById(R.id.customCallLogListView);

        // Create a list of custom call log entries (replace with your own data)
        List<CustomCallLogEntry> customCallLog = new ArrayList<>();
       // customCallLog.add(new CustomCallLogEntry("1234567890", System.currentTimeMillis(), 120));
      //  customCallLog.add(new CustomCallLogEntry("9876543210", System.currentTimeMillis(), 90));
      //  customCallLog.add(new CustomCallLogEntry("5555555555", System.currentTimeMillis(), 180));

        // Create and set the adapter for the ListView
        CustomCallLogAdapter adapter = new CustomCallLogAdapter(this, customCallLog);
        customCallLogListView.setAdapter(adapter);
    }
}
