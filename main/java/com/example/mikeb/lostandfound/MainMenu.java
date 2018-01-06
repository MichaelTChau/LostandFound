package com.example.mikeb.lostandfound;

/**
 * Created by Mikeb on 11/18/2017.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikeb.lostandfound.Utils.ConnectToDB;
import com.example.mikeb.lostandfound.Utils.SearchUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import static java.lang.Math.min;
public class MainMenu extends Activity{
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    ProgressDialog dialog;
    private StorageReference storageReference;

    private List<String> lostItems = new ArrayList<String>();
    private List<Bitmap> pictures = new ArrayList<Bitmap>();
    private List<String> imagePaths = new ArrayList<String>();
    List <String> phones;

    List<String> locations;
    List <String[] >tags = new ArrayList<String[]>();
    private String currentResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        dialog = new ProgressDialog(this);
        dialog.setMessage("loading");
        SearchView searchView = findViewById(R.id.searchFound);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals(""))updateList();
                return true;
            }
        });



        //submit new found
        ImageButton submitFound = findViewById(R.id.add_button);
        submitFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),SubmitFound.class);
                view.getContext().startActivity(i);
            }
        });

        //switch mode to lost item mode
        Button switchMode = findViewById(R.id.switchView);
        switchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),lostItemMenu.class);
                view.getContext().startActivity(i);
            }
        });
        ImageButton refresh = findViewById(R.id.refreshBtn);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateList();
            }
        });

        //permissions
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }


        dialog.show();
        new getList().execute();
    }


    private void filter(String s){
        List <String> filterStr = new ArrayList<String>();
        List <Bitmap> filterImg = new ArrayList<Bitmap>();
        List <String> filterPhone = new ArrayList<String>();
        List <String> filterLocation = new ArrayList<String>();
        Log.d("search",s);
        for(int x=0;x<tags.size();x++){
            for(int y =0;y<tags.get(x).length;y++){
                if(tags.get(x)[y].contains(s)){
                    try{
                        filterStr.add(new JSONArray(tags.get(x)).toString());
                        filterImg.add(pictures.get(x));
                        filterLocation.add(locations.get(x));
                        filterPhone.add(phones.get(x));

                        break;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        ListView listView = findViewById(R.id.listView);
        SearchListAdapter searchListAdapter = new SearchListAdapter(this,filterImg,filterStr,filterLocation,filterPhone);
        listView.setAdapter(searchListAdapter);
    }


    private void fetchImage(final int pos, final int last){
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference ref = storage.getReferenceFromUrl("gs://lostandfound-288e4.appspot.com/"+imagePaths.get(pos)+".jpg");
                try {
                    final File localFile = File.createTempFile("Images", "png");
                    ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            my_image.compress(Bitmap.CompressFormat.JPEG, 5, bytes);
                            pictures.add(my_image);
                            if(pos+1 ==last){
                                dialog.hide();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private class getList extends AsyncTask<Void,Void,String>{

            @Override
            protected String doInBackground(Void... voids) {
                SearchUtils contactUtils = new SearchUtils();
                ConnectToDB connectToDB = new ConnectToDB();
                String lookupResult =connectToDB.getItems("","found");
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


                try{
                    JSONArray jsonArray = new JSONArray(currentResult);
                    for (int x = 0;x<jsonArray.length();x++){
                        JSONObject listItem = jsonArray.getJSONObject(x);
                        imagePaths.add(listItem.getString("image"));
                        locations.add(listItem.getString("where"));
                        tags.add(toStringArray(listItem.getJSONArray("tags")));
                        phones.add(listItem.getString("phone"));
                    }
                    for (int x = 0;x<jsonArray.length();x++){
                        fetchImage(x,jsonArray.length());
                    }
                    Log.d("imagePaths",imagePaths.toString());
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

    private void updateList(){
        List <String> temp = new ArrayList<String>();
        for (int x =0;x<pictures.size();x++){
            try{
                Log.d("element",new JSONArray(tags.get(x)).toString());
                temp.add(x,new JSONArray(tags.get(x)).toString());
            }catch (Exception e){

            }

        }
        Log.d("list",temp.toString());
        SearchListAdapter adapter = new SearchListAdapter(this,pictures,temp,locations,phones);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }
}

