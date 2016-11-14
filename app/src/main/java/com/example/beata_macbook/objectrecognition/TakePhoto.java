package com.example.beata_macbook.objectrecognition;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.beata_macbook.objectrecognition.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.opencv.android.Utils;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;

import java.util.List;


public class TakePhoto extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    static{ System.loadLibrary("opencv_java3");}

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button btnPhoto;
    private ImageView photo;
    private Button identifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);



        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        photo = (ImageView) findViewById(R.id.photo);
        identifyButton = (Button) findViewById(R.id.identifyButton);

        btnPhoto.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        identifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //Mat m = Utils.loadResource(TakePhoto.this, R.drawable.chair);
                    Mat m = Utils.loadResource(TakePhoto.this, R.id.photo);
                    MatOfKeyPoint matOfKeyPoint = new MatOfKeyPoint();
                    FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
                    orbDetector.detect(m, matOfKeyPoint);
                    List<KeyPoint> lista = matOfKeyPoint.toList();
                    int  i = 1;
                    for (KeyPoint element : lista) {
                        Log.d(TAG, i + " " +  element.toString());
                        i++;
                    }

                    Mat outputImage = new Mat();
                    // Your image, keypoints, and output image
                    Features2d f2d = new Features2d();
                    f2d.drawKeypoints(m, matOfKeyPoint, outputImage);



                    Log.d(TAG, keypointsToJson(matOfKeyPoint));

                } catch (java.io.IOException e){
                    Log.d("ERROR", e.toString());
                }
            }
        });



    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photo.setImageBitmap(imageBitmap);
        }
    }

    public static String keypointsToJson(MatOfKeyPoint matOfKeyPoint){
        if(matOfKeyPoint!=null && !matOfKeyPoint.empty())
        {
            Gson gson = new Gson();
            JsonArray jsonArray = new JsonArray();

            KeyPoint[] array = matOfKeyPoint.toArray();
            for(int i=0; i<array.length; i++){
                KeyPoint keyPoint = array[i];

                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("x",        keyPoint.pt.x);
                jsonObject.addProperty("y",        keyPoint.pt.y);
                jsonObject.addProperty("size",     keyPoint.size);
                jsonObject.addProperty("angle",    keyPoint.angle);
                jsonObject.addProperty("response", keyPoint.response);
                jsonObject.addProperty("octave",   keyPoint.octave);
                jsonObject.addProperty("class_id", keyPoint.class_id);
                jsonArray.add(jsonObject);

            }

            String json = gson.toJson(jsonArray);
            return json;
        }
        return "{}";
    }

    public static MatOfKeyPoint keypointsFromJson(String json){
        MatOfKeyPoint result = new MatOfKeyPoint();

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        int size = jsonArray.size();

        KeyPoint[] keyPointArray = new KeyPoint[size];

        for(int i=0; i<size; i++){
            KeyPoint keyPoint = new KeyPoint();

            JsonObject jsonObject = (JsonObject) jsonArray.get(i);

            Point point = new Point(
                    jsonObject.get("x").getAsDouble(),
                    jsonObject.get("y").getAsDouble()
            );

            keyPoint.pt       = point;
            keyPoint.size     =     jsonObject.get("size").getAsFloat();
            keyPoint.angle    =    jsonObject.get("angle").getAsFloat();
            keyPoint.response = jsonObject.get("response").getAsFloat();
            keyPoint.octave   =   jsonObject.get("octave").getAsInt();
            keyPoint.class_id = jsonObject.get("class_id").getAsInt();

            keyPointArray[i] = keyPoint;
        }

        result.fromArray(keyPointArray);

        return result;
    }
}