package project.firstattempt.noseapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.images.Size;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback pictureCallback ;
    Camera.Parameters parameters;
    Bitmap bitmap;

    ImageView imageView;
    FirebaseVisionImage image;
    boolean imgCaptured=false;
    private TextView textView;
    FirebaseVisionFaceDetector detector;
    FirebaseVisionFaceDetectorOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //*****************SETTING VARS AMAND ACTION LISTENERS***************
        textView=findViewById(R.id.textView);
        imageView=findViewById(R.id.imageView);
        mCamera = getCameraInstance();
        initializePictureCallBack();
        addActionListenerToButton();
        parameters=mCamera.getParameters();

        //*****************SETTING VARS AMAND ACTION LISTENERS***************
        //*****************Creating and setting in framelaoyout the camera preview*****************
        mPreview = new CameraPreview(this, mCamera);
        mCamera.setDisplayOrientation(90);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        //*****************Creating and setting in framelaoyout the camera preview*****************

        options=
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                        .build();
        //*******************FIREBASE MLKIT configure korchi********************


        //DETECTOR BEING MADE
         detector
                = FirebaseVision.getInstance().getVisionFaceDetector(options);











    }

    Camera getCameraInstance(){
        mCamera=null;
        try{
            mCamera=Camera.open(2);
        }catch (Exception E){

        }
        return mCamera;
    }
    void addActionListenerToButton(){
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        parameters=mCamera.getParameters();

                        Log.d("TAG1","pictureSize:w"+parameters.getPictureSize().width+" h="+
                        parameters.getPictureSize().height);

                        int width=parameters.getPictureSize().width;
                        int height=parameters.getPictureSize().height;






                        mCamera.takePicture(null,null,pictureCallback);
                        textView.setText("Processing Image");








                    }
                }
        );
    }

    void initializePictureCallBack(){
        pictureCallback=new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

               // Toast.makeText(MainActivity.this,"Picture Taken",Toast.LENGTH_SHORT).show();
                mPreview.camera.startPreview();
                Log.d("TAG1","DEBUG1");
                bitmap= BitmapFactory.decodeByteArray(data,0,data.length);
                //***************BITMAP COMPRESSION CODE*******************
                ByteArrayOutputStream stream =new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,0,stream);
                byte [] byteArray=stream.toByteArray();
                bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                //***************BITMAP COMPRESSION CODE*******************
                Log.d("TAG1","bitmap condn=" );

               Matrix matrix=new Matrix();
                //**************ROTATING THE BITMAP
                matrix.postRotate(-90);
                bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);



                imageView.setImageBitmap(bitmap);
                    //**************ROTATING THE BITMAP
                imgCaptured=true;
                image =FirebaseVisionImage.fromBitmap(bitmap);


                Log.d("TAG1","Image for firebase formed");

                //*******************FIREBASE MLKIT configure korchi********************
                // High-accuracy landmark detection and face classification


                // Real-time contour detection of multiple faces


                detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        Log.d("TAG1","Image detected with success");
                        int count=0;
                        FirebaseVisionFace face1=null;
                        for(FirebaseVisionFace face:firebaseVisionFaces){
                            count=count+1;
                            Log.d("TAG1","Face detected="+count);
                            if(count==1){
                                face1=face;
                                Log.d("TAG1","Face1 assigned");
                            }
                        }
                        if(count==0){
                            Log.d("TAG1","Face detected="+count);
                        }else{
                            FirebaseVisionFaceLandmark noseBase = face1.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE);
                            if (noseBase != null) {
                                FirebaseVisionPoint nosePosition = noseBase.getPosition();
                                float x=nosePosition.getX();
                                float y=nosePosition.getY();
                                Log.d("TAG1","Position: x="+x+" y="+y);
                                textView.setText("Position: x="+x+" y="+y);

                            }

                        }






                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG1","Image  not detected ");
                    }
                });
                //DETECTOR MADE WITH THE REQUIRED CONFIGS



            }
        };
    }





}
