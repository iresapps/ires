package com.example.ires.firebase;

public class dashboardUtils {
    public String incidentNumber;
    public String incidentType;
    public String name;
    public String phoneNumber;
    public  String callType;
    public String dateTime;
    dashboardUtils(String incidentNumber, String type, String name, String phoneNumber, String callType){
        this.incidentNumber = incidentNumber;
        this.incidentType = type;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.callType =  callType;
    }
    public String getName() {
        return name;
    }
    public String getIncidentNumber() {
        return incidentNumber;
    }
    public String getIncidentType() {
        return incidentType;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCallType() {
        return callType;
    }

    public String getDateTime() {
        return dateTime;
    }
}
