package com.example.ires.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FireBaseConn {
    // Sends data to the firebase dashboard
    // @param incident_number type of incident that occurs
    // 0001 (fire)
    // 0002 (police)
    // 0003 (medical)
    // @param sender_name the name of the person who sent a distress call
    // @param sender_number the number of the person who sent a distress call
    // @param content the context of what happened on the area
    // @param date_sent time and date of the response
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

    public void CheckDatabase() {
        if ( database == null ) {
            System.out.println ( "database is empty" );
            return;
        }
        System.out.println ("database: " + database);
    }
    public void SendToDashboard(int incident_number ,String sender_name
            , String sender_number, String content, Date date_sent){

        String incident = SetIncident (incident_number);
        String strDate = dateFormat.format (date_sent);
        // structure
        // date.incident_number.sender_name.(incident, number, content)
        database.child(strDate)
                .child("incident number")
                .setValue(String.valueOf(incident_number));
        database.child(strDate)
                .child(String.valueOf(incident_number))
                .child(sender_name)
                .setValue(sender_name);
        database.child(strDate)
                .child(String.valueOf(incident_number))
                .child(sender_name)
                .child("incident")
                .setValue(incident);
        database.child(strDate)
                .child(String.valueOf(incident_number))
                .child(sender_name)
                .child("number")
                .setValue(sender_number);
        database.child(strDate)
                .child(String.valueOf(incident_number))
                .child(sender_name)
                .child("content")
                .setValue(content);
    }
    // sets the status if ongoing or dispatched
    public void SetStatus(int incident_number ,String sender_name
            ,Date date_sent, boolean isDispatched){
        String strDate = dateFormat.format (date_sent);
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
    private String SetIncident(int number){
        switch ( number ){
            case 1:
                return Incidents.fire.name();
            case 2:
                return Incidents.crimes.name();
            case 3:
                return Incidents.medical_emergencies.name();
        }
        return "";
    }
}
