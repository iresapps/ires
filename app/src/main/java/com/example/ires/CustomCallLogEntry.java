package com.example.ires;

public class CustomCallLogEntry {
    private String phoneNumber;
    private long callDate;
    private int callDuration;

    public CustomCallLogEntry(String phoneNumber, long callDate, int callDuration) {
        this.phoneNumber = phoneNumber;
        this.callDate = callDate;
        this.callDuration = callDuration;
    }

    // Getter methods for attributes
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public long getCallDate() {
        return callDate;
    }

    public int getCallDuration() {
        return callDuration;
    }
}

