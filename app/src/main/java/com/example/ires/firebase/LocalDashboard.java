package com.example.ires.firebase;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ires.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalDashboard extends AppCompatActivity {
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        DatabaseReference myTableRef = database.child("incidents table");

        ArrayList<String> incidentNumberList = new ArrayList<>();
        incidentNumberList.add(myTableRef.child("incident number").get().toString());

        List<dashboardUtils> dataObjects = new ArrayList<>();
        dataObjects.add(new dashboardUtils(incidentNumberList.get(0),"","","",""));
        DashboardAdapter adapter = new DashboardAdapter(this, dataObjects);
        ListView listView = findViewById(R.id.dashboard_listview);

    }
}
