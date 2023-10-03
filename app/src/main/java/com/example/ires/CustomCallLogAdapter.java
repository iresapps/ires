package com.example.ires;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CustomCallLogAdapter extends BaseAdapter {

    private Context context;
    private List<CustomCallLogEntry> customCallLog;

    public CustomCallLogAdapter(Context context, List<CustomCallLogEntry> customCallLog) {
        this.context = context;
        this.customCallLog = customCallLog;
    }

    @Override
    public int getCount() {
        return customCallLog.size();
    }

    @Override
    public Object getItem(int position) {
        return customCallLog.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_call_log_item, parent, false);
        }

        CustomCallLogEntry callLogEntry = customCallLog.get(position);

        TextView phoneNumberTextView = convertView.findViewById(R.id.phoneNumberTextView);
        TextView callDateTextView = convertView.findViewById(R.id.callDateTextView);
        TextView callDurationTextView = convertView.findViewById(R.id.callDurationTextView);

        phoneNumberTextView.setText(callLogEntry.getPhoneNumber());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        callDateTextView.setText(dateFormat.format(callLogEntry.getCallDate()));

        int callDuration = callLogEntry.getCallDuration();
        int minutes = callDuration / 60;
        int seconds = callDuration % 60;
        String durationText = String.format(Locale.getDefault(), "%d min %d sec", minutes, seconds);
        callDurationTextView.setText(durationText);

        return convertView;
    }
}
