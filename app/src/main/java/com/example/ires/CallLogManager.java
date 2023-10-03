package com.example.ires;import java.util.ArrayList;
import java.util.List;

public class CallLogManager {
    private static CallLogManager instance;
    private List<CallLogEntry> callLog;

    private CallLogManager() {
        callLog = new ArrayList<>();
    }

    public static CallLogManager getInstance() {
        if (instance == null) {
            instance = new CallLogManager();
        }
        return instance;
    }

    public void addCallLogEntry(CallLogEntry entry) {
        callLog.add(entry);
    }

    public List<CallLogEntry> getCallLog() {
        return callLog;
    }
}
