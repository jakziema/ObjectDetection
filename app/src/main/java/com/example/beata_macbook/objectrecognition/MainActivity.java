package com.example.beata_macbook.objectrecognition;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;

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



        } catch (java.io.IOException e){
            Log.d("ERROR", e.toString());
        }



    }




}
