package com.donhan.apps.bethere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Akshay on 8/8/2015.
 */
public class CheckinListAdapter extends ArrayAdapter<String> {

    public CheckinListAdapter(Context context){
        super(context, R.layout.checkin_list_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.checkin_list_item, null);
        }

        TextView text = (TextView)view.findViewById(R.id.text);
        text.setText(getItem(position));

        return view;
    }

}
