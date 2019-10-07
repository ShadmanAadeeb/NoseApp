package project.firstattempt.noseapp;

import android.content.Context;

import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceHolder surfaceHolder;//interface for sm1 holding a surface

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera=camera;
        this.surfaceHolder=getHolder();
        this.surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //the surface has been created . Now telling the camera where to draw the preview
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(holder.getSurface()==null){
            return;
            //surface already destroyed. Null pointer
        }

        camera.stopPreview();


        //STARTING A PREVIEW WITH NEW SETTINGS
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
