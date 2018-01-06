package com.example.mikeb.lostandfound;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.mikeb.lostandfound.Utils.ConnectToDB;
import com.example.mikeb.lostandfound.Utils.SearchUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 2017-11-18.
 */

public class lostItemMenu extends Activity {


    private List <String[] >tags = new ArrayList<String[]>();
    private List<String> locations;
    private List<String> phones;
    private String currentResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_list);

        SearchView searchView = findViewById(R.id.searchLost);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals("")) formatData();
                return true;
            }
        });

        new getList().execute();
        //switch mode to lost item mode
        Button switchModeBack = findViewById(R.id.found_btn);
        switchModeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),MainMenu.class);
                view.getContext().startActivity(i);
            }
        });

        //switch mode to lost item mode
        Button addLostItem = findViewById(R.id.add_lost);
        addLostItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),SubmitLost.class);
                view.getContext().startActivity(i);
            }
        });
    }

    private class getList extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            SearchUtils contactUtils = new SearchUtils();
            ConnectToDB connectToDB = new ConnectToDB();
            String lookupResult =connectToDB.getItems("","lost");
            currentResult = lookupResult;
            return lookupResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            formatData();
        }
    }

    private void formatData(){

        phones = new ArrayList<String>();

        locations = new ArrayList<String>();
        tags = new ArrayList<String[]>();


        try{
            JSONArray jsonArray = new JSONArray(currentResult);
            for (int x = 0;x<jsonArray.length();x++){
                JSONObject listItem = jsonArray.getJSONObject(x);
                locations.add(listItem.getString("where"));
                tags.add(toStringArray(listItem.getJSONArray("tags")));
                phones.add(listItem.getString("phone"));
            }
            List <String> temp = new ArrayList<String>();
            for (int x =0;x<tags.size();x++){
                try{
                    Log.d("element",new JSONArray(tags.get(x)).toString());
                    temp.add(x,new JSONArray(tags.get(x)).toString());
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            SearchListAdapter2 adapter2 = new SearchListAdapter2(this,temp,locations,phones);
            ListView listView = findViewById(R.id.lostItemList);
            listView.setAdapter(adapter2);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
    }

    private void filter(String s){
        List <String> filterStr = new ArrayList<String>();
        List <String> filterLocation = new ArrayList<String>();
        List <String> filterContract = new ArrayList<String>();
        Log.d("search",s);
        for(int x=0;x<tags.size();x++){
            for(int y =0;y<tags.get(x).length;y++){
                if(tags.get(x)[y].contains(s)){
                    try{
                        filterStr.add(new JSONArray(tags.get(x)).toString());
                        filterLocation.add(locations.get(x));
                        filterContract.add(locations.get(x));
                        break;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        ListView listView = findViewById(R.id.lostItemList);
        SearchListAdapter2 searchListAdapter = new SearchListAdapter2(this,filterStr,filterLocation,filterContract);
        listView.setAdapter(searchListAdapter);
    }

}
