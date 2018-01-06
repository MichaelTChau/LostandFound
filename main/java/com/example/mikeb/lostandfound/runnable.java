package com.example.mikeb.lostandfound;

/**
 * Created by Mikeb on 11/18/2017.
 */

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.json.*;

import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Created by Justin on 2017-11-18.
 */

public class runnable extends AppCompatActivity implements Callable<ArrayList<String>> {
    private ArrayList<String> names = new ArrayList<>();
    Thread runner;
    File chosenFileName;
    String fName;

    public runnable(String imageFileName) {
        fName = imageFileName;
        names.clear();
    }

    public runnable(File file) {
        chosenFileName = file;
        names.clear();
    }

    public ArrayList<String> call() {
        try {

            VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
            service.setApiKey("8d7aced8efa9ce11cca985d203dce5989cc20148");

            File folder = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String[] listOfFiles = folder.list();
            String lastImagePath = "";
            for (String path : listOfFiles) {
                lastImagePath = path;
            }


            System.out.println("Classify an image");
            ClassifyImagesOptions options;
            //just took picture
            if (fName != null) {
                options = new ClassifyImagesOptions.Builder()
                        .images(new File(getExternalStoragePublicDirectory
                                (Environment.DIRECTORY_PICTURES)
                                + "/" + lastImagePath)) // "/storage/0123-4567/Frog.jpg"
                        .build(); //+ "/" + fName + ".jpg"
            } else { //from gallery
                options = new ClassifyImagesOptions.Builder()
                        .images(chosenFileName) // "/storage/0123-4567/Frog.jpg"
                        .build(); //+ "/" + fName + ".jpg"
            }


            VisualClassification result = service.classify(options).execute();
            System.out.println(result);

            String s = result.toString();
            //System.out.println(s);

            JSONObject obj = new JSONObject(s);
            JSONArray arr = obj.getJSONArray("images");
            System.out.println(arr);
            obj = new JSONObject(arr.getString(0));
            arr = obj.getJSONArray("classifiers");
            obj = new JSONObject(arr.getString(0));
            arr = obj.getJSONArray("classes");
            obj = new JSONObject(arr.getString(0));

            names.add(obj.getString("class"));
            obj = new JSONObject(arr.getString(1));
            names.add(obj.getString("class"));
            obj = new JSONObject(arr.getString(2));
            names.add(obj.getString("class"));

            return names;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void stop() {
        runner = null;
    }
}