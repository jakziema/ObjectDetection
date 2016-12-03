package com.example.beata_macbook.objectrecognition;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;


import com.google.gson.*;

import java.io.IOException;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "Main2Activity";

    static{ System.loadLibrary("opencv_java3"); }

    private static final int SELECTED_PICTURE=1;
    ImageView iv;
    EditText lokalizacja;
    EditText nazwa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv=(ImageView)findViewById(R.id.imageView);


    }

    public void ClickBtn(View v){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECTED_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECTED_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);

                    ImageView iv = (ImageView) findViewById(R.id.imageView2);
                    iv.setImageBitmap(yourSelectedImage);


                        Mat m = new Mat();
                        Utils.bitmapToMat(yourSelectedImage, m);

                        //new Mat();
                        //Utils.bitmapToMat(youtSelectedImage, m);

                        MatOfKeyPoint matOfKeyPoint = new MatOfKeyPoint();
                        FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
                        orbDetector.detect(m, matOfKeyPoint);
                        List<KeyPoint> lista = matOfKeyPoint.toList();
                        int i = 1;
                        for (KeyPoint element : lista) {
                            Log.d(TAG, i + " " + element.toString());
                            i++;
                        }


                    Log.d(TAG, keypointsToJson(matOfKeyPoint));


                       // Mat outputImage = new Mat();
                        // Your image, keypoints, and output image
                      //  Features2d f2d = new Features2d();
                     //   f2d.drawKeypoints(m, matOfKeyPoint, outputImage);
                      //  Bitmap bm = Bitmap.createBitmap(outputImage.cols(), outputImage.rows(), Bitmap.Config.RGB_565);
                       // Utils.matToBitmap(outputImage, bm);
//
                       // iv.setImageBitmap(bm);


                    }
                    break;
                }
        }




    public String keypointsToJson(MatOfKeyPoint matOfKeyPoint){
        if(matOfKeyPoint!=null && !matOfKeyPoint.empty())
        {


            Gson gson = new Gson();
            JsonArray jsonArray = new JsonArray();

            KeyPoint[] array = matOfKeyPoint.toArray();
            for(int i=0; i<array.length; i++){
                KeyPoint keyPoint = array[i];

                JsonObject jsonObject = new JsonObject();

                final EditText lokalizacja = (EditText)findViewById(R.id.editText);
                final EditText nazwa = (EditText)findViewById(R.id.editText2);
                String nazwa1 = nazwa.getText().toString();
                String lokalizacja1 = lokalizacja.getText().toString();

               jsonObject.addProperty("nazwa", nazwa1);
               jsonObject.addProperty("lokalizacja", lokalizacja1);
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
