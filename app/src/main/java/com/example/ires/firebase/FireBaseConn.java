package com.example.ires.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Locale;

public class FireBaseConn {
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss", Locale.US);
    String incident;
    String strDate;
    public void CheckDatabase() {
        if ( database == null ) {
            System.out.println ( "database is empty" );
            return;
        }
        System.out.println ("database: " + database);
    }
    // Sends data to the firebase dashboard
    // @param incident_number type of incident that occurs
    // 0001 (fire)
    // 0002 (police)
    // 0003 (medical)
    // @param sender_name the name of the person who sent a distress call
    // @param sender_number the number of the person who sent a distress call
    // @param content the context of what happened on the area
    // @param date_sent time and date of the response
    public void SendToDashboard(int incident_number ,String sender_name
            , String sender_number, Calendar date_sent){

        incident = SetIncident (incident_number);
        strDate = dateFormat.format(date_sent.getTime());
        // structure
        // date.incident_number.sender_name.(incident, number, content)
        database.child(strDate)
                .child("incident number")
                .setValue(String.valueOf(incident_number + 1));

        DatabaseReference localData = database.child(strDate)
                .child(String.valueOf(incident_number+ 1))
                .child(sender_name);

        localData.setValue(sender_name);
        localData.child("incident").setValue(incident);
        localData.child("number").setValue(sender_number);
//        localData.child("content").setValue(content);

        database.keepSynced(true);
    }
    public void SetStatus(int incident_number ,String sender_name
            ,Calendar date_sent, boolean isDispatched){
    String strDate = dateFormat.format(date_sent);
        if(isDispatched){
            database.child(strDate)
                    .child(String.valueOf(incident_number))
                    .child (sender_name)
                    .child("status")
                    .setValue(Statuses.dispatched.name());
            return;
        }
        database.child(strDate)
                .child(String.valueOf(incident_number))
                .child (sender_name)
                .child("status")
                .setValue(Statuses.ongoing.name());
    }
    // Sets the type of incident that occurs based on incident number
    // @param number pass the incident_number from SendToDashboard method
    // sets the status if ongoing or dispatched
    private String SetIncident(int number){
        if(number < 0 || number > Incidents.values().length - 1){ return ""; }
        return Incidents.values()[number].name();
    }
}
