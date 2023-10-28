package com.example.ires;

import java.util.Calendar;

public class CallLogEntry {
    private String name;
    private String number;
    private Calendar date;
    private int duration;
    private int callType;

    public CallLogEntry(String name, String number, Calendar date, int duration, int callType) {
        this.name = name;
        this.number = number;
        this.date = date;
        this.duration = duration;
        this.callType = callType;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public Calendar getDate() {
        return date;
    }

    public int getDuration() {
        return duration;
    }

    public int getCallType() {
        return callType;
    }
}
