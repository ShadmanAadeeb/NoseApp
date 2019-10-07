package project.firstattempt.noseapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageDisplay extends AppCompatActivity {
    ImageView imageView;
    Bitmap bitmap;
    byte []data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        imageView=findViewById(R.id.imageView);
        Intent intent=getIntent();
        data=intent.getByteArrayExtra("Data");


    }
}
