package com.example.ires.firebase;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ires.R;

import java.util.List;
public class DashboardAdapter extends ArrayAdapter<dashboardUtils> {
    public DashboardAdapter(Context context, List<dashboardUtils> objects) {
        super(context, 0, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        dashboardUtils dataObject = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_viewer, parent, false);
        }

        // Lookup view for data population
        TextView IncidentNumberTextView = convertView.findViewById(R.id.IncidentNumberTextView);
        TextView IncidentTypeTextView = convertView.findViewById(R.id.IncidentTextView);
        TextView NameTextView = convertView.findViewById(R.id.nameTextView);
        TextView PhoneNumberTextView = convertView.findViewById(R.id.phoneNumberTextView);
        TextView CallTypeTextView = convertView.findViewById(R.id.callTypeTextView);
        TextView TimeDateTextView = convertView.findViewById(R.id.TimeDateTextView);

        // Populate the data into the template view using the data object
        IncidentNumberTextView.setText(dataObject.getIncidentNumber());
        IncidentTypeTextView.setText(dataObject.getIncidentType());
        NameTextView.setText(dataObject.getName());
        PhoneNumberTextView.setText(dataObject.getPhoneNumber());
        CallTypeTextView.setText(dataObject.getCallType());
        TimeDateTextView.setText(dataObject.getDateTime());


        // Return the completed view to render on screen
        return convertView;
    }

}
