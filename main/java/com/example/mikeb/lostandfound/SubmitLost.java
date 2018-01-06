package com.example.mikeb.lostandfound;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mikeb.lostandfound.Utils.ConnectToDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 2017-11-18.
 */

public class SubmitLost extends Activity {
    private EditText location;
    private EditText contact;
    private EditText tag1;
    private EditText tag2;
    private EditText tag3;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_lost);
         location = findViewById(R.id.lostLocation);
         contact = findViewById(R.id.lostContactInfo);
         tag1 = findViewById(R.id.lTag1);
         tag2 = findViewById(R.id.lTag2);
         tag3 = findViewById(R.id.lTag3);
        Button submit = findViewById(R.id.lostEnter);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                Log.d("clicking","start");
                uploadData();

            }
        });
    }

    private void uploadData(){
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                ConnectToDB connectToDB = new ConnectToDB();
                JSONObject document = new JSONObject();
                try{
                    List<String> tempTags = new ArrayList<String>();
                    if((tag1 != null)&&(!tag1.getText().toString().equals(""))) tempTags.add(tag1.getText().toString());
                    if((tag2 != null)&&(!tag2.getText().toString().equals(""))) tempTags.add(tag2.getText().toString());
                    if((tag3 != null)&&(!tag3.getText().toString().equals(""))) tempTags.add(tag3.getText().toString());
                    document.put("where",location.getText().toString());
                    document.put("phone",contact.getText().toString());
                    document.put("tags",new JSONArray(tempTags));
                    connectToDB.insert(document.toString(),"lost");
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.hide();

            }
        }.execute();
    }
}
