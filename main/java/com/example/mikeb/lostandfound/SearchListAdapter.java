package com.example.mikeb.lostandfound;

/**
 * Created by Mikeb on 11/18/2017.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Justin on 2017-11-18.
 */

public class SearchListAdapter extends ArrayAdapter<String>implements View.OnClickListener {
    private final Activity context;
    private List<Bitmap> images;
    private List<String> name;
    private List<String> locations;
    private List<String> contacts;

    public SearchListAdapter(Activity context, List<Bitmap> images, List<String> name,List<String> locations, List<String>contacts) {
        super(context, R.layout.row_layout, name);
        this.context = context;
        this.images = images;
        this.name = name;
        this.locations = locations;
        this.contacts = contacts;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Log.d("Tag", position + "");

    }

    public View getView(int position, View view, ViewGroup Parent) {

        if(view == null){
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.row_layout, null, true);
        }


        TextView txtTile = (TextView) view.findViewById(R.id.rowText);
        ImageView imageView = (ImageView) view.findViewById(R.id.rowImage);
        TextView locationText = view.findViewById(R.id.rowLocation);
        TextView contactText = view.findViewById(R.id.rowContact);

        txtTile.setText(name.get(position).toString());
        imageView.setImageBitmap(images.get(position));

        locationText.setText(locations.get(position));
        contactText.setText(contacts.get(position));

        return view;
    }
}