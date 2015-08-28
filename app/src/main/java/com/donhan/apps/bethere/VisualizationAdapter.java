package com.donhan.apps.bethere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by donhan on 8/28/15.
 */
public class VisualizationAdapter extends ArrayAdapter<CheckInCoordinate> {

    public VisualizationAdapter(Context context) {
        super(context, R.layout.checkin_list_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.checkin_list_item, null);
        }

        TextView textView = (TextView)view.findViewById(R.id.text);

        String dateTime = DateFormat.getDateTimeInstance().format(getItem(position).timestamp);
        String text = "Checked in at " + dateTime;
        textView.setText(text);

        return view;
    }
}
