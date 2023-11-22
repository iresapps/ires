package com.example.ires.firebase;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ires.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalDashboard extends AppCompatActivity {
    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("incidents table");
//    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
    List<String> dataList = new ArrayList<>();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        listView = findViewById(R.id.dashboard_listview);
        initializeListView();
//        DatabaseReference myTableRef = database.child("incidents table");
//        ArrayList<String> dateList = new ArrayList<>();
//        dateList.add(myTableRef.get().toString());
//        String incidentNumber = "";
//        String incidentType = "";
//        String name = "";
//        String phoneNumber = "";
//        for(String date : dateList){
//            incidentNumber = myTableRef.child(date).child("incident number").get().toString();
//            incidentType = myTableRef.child(date).child("incident").get().toString();
//            name = myTableRef.child(date).child("name").get().toString();
//            phoneNumber = myTableRef.child(date).child("number").get().toString();
////            String callType = myTableRef.child(date).child("incident number").child("name").get().toString();
//            dataList.add(incidentNumber + "\t" + incidentType + "\t" + name + "\t" + phoneNumber);
//        }

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dateList);
//        listView.setAdapter(adapter);
    }
    private void initializeListView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        // below line is used for setting
        // an adapter to our list view.
        listView.setAdapter(adapter);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot snap: snapshot.getChildren()) {
                    dataList.add(snap.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
