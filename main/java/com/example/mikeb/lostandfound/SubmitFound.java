package com.example.mikeb.lostandfound;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mikeb.lostandfound.Utils.ConnectToDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

/**
 * Created by Mikeb on 11/18/2017.
 */

public class SubmitFound extends Activity {
    private Uri filePath;
    private StorageReference storageReference;
    private String currentUploadName;
    private Bitmap current;
    private EditText tag1;
    private EditText tag2;
    private EditText tag3;
    private EditText phone;
    private EditText location;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_item);
        storageReference = FirebaseStorage.getInstance().getReference();


        //Buttons
        ImageButton upload = findViewById(R.id.uploadPic);
        ImageButton capture = findViewById(R.id.uploadImage);
        ImageButton back = findViewById(R.id.back_button2);

        //EditText
        phone = findViewById(R.id.contactInfo);
        location = findViewById(R.id.locationInfo);
        tag1 = findViewById(R.id.tag1);
        tag2 = findViewById(R.id.tag2);
        tag3 = findViewById(R.id.tag3);

        //upload clicked
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(view.getContext());
                dialog.setMessage("Uploading");
                dialog.show();
                uploadImage();
                uploadData();

            }
        });

        //take a pic
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),MainMenu.class);
                view.getContext().startActivity(i);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(this.getExternalCacheDir(),
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        Uri fileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            filePath = data.getData();
            try{
                Log.d("here","ere");
                current = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ImageView imageView = (ImageView) findViewById(R.id.imageToUpload);
                imageView.setImageBitmap(current);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            //image bitmap is the image


            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";


            MediaStore.Images.Media.insertImage(getContentResolver(),current , imageFileName, "Origamit");

            FutureTask<ArrayList<String>> futureTask = new FutureTask<ArrayList<String>>(new runnable(imageFileName));
            Thread t = new Thread(futureTask);
            t.start();

            try {
                ArrayList<String> names = futureTask.get();
                System.out.println(names);
                tag1.setText(names.get(0));
                tag2.setText(names.get(1));
                tag3.setText(names.get(2));


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImage(){
        String s = String.format("%x",(int)(Math.random()*10000000));
        currentUploadName = s;
        StorageReference childRef = storageReference.child(currentUploadName+".jpg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        current.compress(Bitmap.CompressFormat.JPEG, 5, bytes);
        String path = MediaStore.Images.Media.insertImage(SubmitFound.this.getContentResolver(), current, currentUploadName+".jpg", null);
        filePath = Uri.parse(path);

        //uploading the image
        UploadTask uploadTask = childRef.putFile(filePath);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SubmitFound.this, "Upload successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SubmitFound.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
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
                    document.put("image",currentUploadName);
                    document.put("where",location.getText().toString());
                    document.put("phone",phone.getText().toString());
                    document.put("tags",new JSONArray(tempTags));
                    connectToDB.insert(document.toString(),"found");
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
