package com.example.mikeb.lostandfound.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mikeb on 11/18/2017.
 */

public class SearchUtils{
        public static final String found_url = "https://api.mlab.com/api/1/databases/phone_" +
                "number_profiles/collections/foundItems?apiKey=oQ8EeSsOWTSV9AAmp6aUCQCwnaTs08Aq";
        public static final String lost_url =  "https://api.mlab.com/api/1/databases/phone" +
                "_number_profiles/collections/lostItems?apiKey=oQ8EeSsOWTSV9AAmp6aUCQCwnaTs08Aq";

    public static JSONObject getObject(String tagName, JSONObject jsonObject) throws JSONException
        {
            JSONObject jObj = jsonObject.getJSONObject(tagName);
            return jObj;
        }
        public static String getString(String tagName,JSONObject jsonObject) throws  JSONException
        {
            return jsonObject.getString(tagName);
        }

        public static long getLong (String tagName , JSONObject jsonObject)throws  JSONException
        {
            return  jsonObject.getLong (tagName);
        }
        public static int getInt (String tagName , JSONObject jsonObject)throws  JSONException
        {
            return  jsonObject.getInt(tagName);
        }
}
