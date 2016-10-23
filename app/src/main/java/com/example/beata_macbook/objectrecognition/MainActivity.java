package com.example.beata_macbook.objectrecognition;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;


import com.google.gson.*;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    static{ System.loadLibrary("opencv_java3"); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            Mat m = Utils.loadResource(MainActivity.this, R.drawable.waffles);
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

            Bitmap bm = Bitmap.createBitmap(outputImage.cols(), outputImage.rows(),Bitmap.Config.RGB_565);
            Utils.matToBitmap(outputImage, bm);
            ImageView iv = (ImageView) findViewById(R.id.imageView);
            iv.setImageBitmap(bm);

            Log.d(TAG, keypointsToJson(matOfKeyPoint));

        } catch (java.io.IOException e){
            Log.d("ERROR", e.toString());
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
