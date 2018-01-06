package com.example.mikeb.lostandfound.Utils;

/**
 * Created by Mikeb on 11/18/2017.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


/**
 * Created by Mikeb on 11/5/2017.
 */

public class ConnectToDB {
    public String getItems(String query,String type){
        HttpURLConnection connection;
        InputStream inputStream;

        try {
            URL url;
            if(type.equals("found")) url  = new URL(SearchUtils.found_url+query);
            else   url= new URL(SearchUtils.lost_url+query);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoInput(true);
            connection.connect();

            //Read the response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\r\n");
            }

            inputStream.close();
            connection.disconnect();
            return stringBuffer.toString();


        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

    public void insert(String query,String type){
        try {
            SearchUtils contactUtils = new SearchUtils();
            URL object;
            if(type.equals("found"))object =new URL(SearchUtils.found_url);
            else object =new URL(SearchUtils.lost_url);

            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");

            byte[] outputBytes = query.getBytes("UTF-8");
            OutputStream os = con.getOutputStream();
            os.write(outputBytes);
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                System.out.println("Success");
            } else {
                System.out.println("fail");
                System.out.println(con.getResponseMessage());
            }
        }
        catch (Exception e){
            e.printStackTrace();

        }
    }

    public String getType(String object){
        String urlString = "https://api.mlab.com/api/1/databases/phone_number_profiles/collections/lostAndFound?q=";
        urlString = urlString + object + "&fo=true&apiKey=oQ8EeSsOWTSV9AAmp6aUCQCwnaTs08Aq";
        HttpURLConnection connection;
        InputStream inputStream;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoInput(true);
            connection.connect();

            //Read the response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\r\n");
            }

            inputStream.close();
            connection.disconnect();
            return stringBuffer.toString();


        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }
}