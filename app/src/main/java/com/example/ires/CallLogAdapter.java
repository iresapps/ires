package com.example.ires;

import android.content.Context;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CallLogAdapter extends BaseAdapter {

    private Context context;
    private List<CallLogEntry> callLogEntries;

    public CallLogAdapter(Context context, List<CallLogEntry> callLogEntries) {
        this.context = context;
        this.callLogEntries = callLogEntries;
    }

    @Override
    public int getCount() {
        return callLogEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return callLogEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.call_log_item, parent, false);
        }

        CallLogEntry callLogEntry = callLogEntries.get(position);

        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView numberTextView = convertView.findViewById(R.id.numberTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView durationTextView = convertView.findViewById(R.id.durationTextView);
        TextView callTypeTextView = convertView.findViewById(R.id.callTypeTextView);

        nameTextView.setText(callLogEntry.getName());
        numberTextView.setText(callLogEntry.getNumber());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateTextView.setText(dateFormat.format(callLogEntry.getDate()));

        int durationSeconds = callLogEntry.getDuration();
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        String durationText = String.format(Locale.getDefault(), "%d min %d sec", minutes, seconds);
        durationTextView.setText(durationText);

        String callType = getCallTypeString(callLogEntry.getCallType());
        callTypeTextView.setText("Call Type: " + callType);

        return convertView;
    }


    private String getCallTypeString(int callType) {
            switch (callType) {
                case CallLog.Calls.INCOMING_TYPE:
                    return "Incoming";
                case CallLog.Calls.OUTGOING_TYPE:
                    return "Outgoing";
                case CallLog.Calls.MISSED_TYPE:
                    return "Missed";
                default:
                    return "Unknown";
            }
        }
    }

