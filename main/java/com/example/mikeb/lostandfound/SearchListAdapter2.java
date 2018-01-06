package com.example.mikeb.lostandfound;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Justin on 2017-11-18.
 */

public class SearchListAdapter2 extends ArrayAdapter<String> implements View.OnClickListener {
    private final Activity context;
    private final List<String>descriptors;
    private final List<String> lostAt;
    private final List<String> contactInfo;

    public SearchListAdapter2(Activity context, List<String> descriptors, List<String> lostAt,
                              List<String> contactInfo) {
        super(context, R.layout.row_layout2, descriptors);
        this.context = context;
        this.descriptors = descriptors;
        this.lostAt = lostAt;
        this.contactInfo = contactInfo;

    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Log.d("Tag", position + "");

    }

    public View getView(int position, View view, ViewGroup Parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row_layout2, null, true);

        TextView txtTile1 = (TextView) rowView.findViewById(R.id.row2Descriptors);
        TextView txtTile2 = (TextView) rowView.findViewById(R.id.row2LostAt);
        TextView txtTile3 = (TextView) rowView.findViewById(R.id.row2ContactInfo);

        txtTile1.setText(descriptors.get(position));
        txtTile2.setText(lostAt.get(position));
        txtTile3.setText(contactInfo.get(position));


        return rowView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
