package com.streamsdk.chat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.streamsdk.cache.FileCache;

public class AndroidPhotoCapture extends Activity{

	private Camera myCamera;
    private MyCameraSurfaceView myCameraSurfaceView;
    private Button myButton;
    private Activity activity;
    private int cameraId;
    private int rotation = 0;
    private SurfaceHolder mHolder;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photocapture_layout);
        activity = this;
       // final LinearLayout parentLayout = (LinearLayout)findViewById(R.id.androidPhotoCaptureLayout);
        cameraId = CameraInfo.CAMERA_FACING_BACK;
        myCamera = getCameraInstance(cameraId);
        Camera.Parameters params = getCameraParameters();
        
        try{
           myCamera.setParameters(params);
        }catch(Throwable t){
           Log.i("", "");	
        }
        
        if(myCamera == null){
            Toast.makeText(AndroidPhotoCapture.this, "Fail to get Camera", Toast.LENGTH_LONG).show();
        }
        
        myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);
        final FrameLayout myCameraPreview = (FrameLayout)findViewById(R.id.photoview);
        myCameraPreview.addView(myCameraSurfaceView);
        myButton = (Button)findViewById(R.id.takePhotoButton);
        myButton.setOnClickListener(myButtonOnClickListener);
        
        Button change = (Button)findViewById(R.id.changeCam);
        change.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (cameraId ==  CameraInfo.CAMERA_FACING_FRONT)
					cameraId = CameraInfo.CAMERA_FACING_BACK;
				else
				    cameraId = CameraInfo.CAMERA_FACING_FRONT;
					
				myCamera.stopPreview();
				releaseCamera();
				myCamera = getCameraInstance(cameraId);
				Camera.Parameters params = getCameraParameters();
				try {
					myCamera.setParameters(params);
					myCamera.setPreviewDisplay(mHolder);
				} catch (Throwable t) {
					Log.i("", "");
				}
				myCamera.setDisplayOrientation(90);
				myCamera.startPreview();
			}
		});

    }
   
    Button.OnClickListener myButtonOnClickListener = new Button.OnClickListener(){
        public void onClick(View v) {
        	myCamera.takePicture(null, null, mPicture);
        }
    };
   
    private Camera.Parameters getCameraParameters(){
    	
    	Camera.Parameters params  = myCamera.getParameters();
         if (params.getFlashMode() != null)
            params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        /* if (params.getFocusMode() != null)
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
         if (params.getSceneMode() != null)
            params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
         if (params.getWhiteBalance() != null)
            params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);*/
         params.setExposureCompensation(0);
         params.setPictureFormat(ImageFormat.JPEG);
         params.setJpegQuality(100);
         rotation = calculateRotation(cameraId);
         params.setRotation(rotation);
         List<Size> sizes = params.getSupportedPictureSizes();
         Camera.Size size = null ;
         for(int i=0;i<sizes.size();i++){
             if(sizes.get(i).width > 1024){
                 size = sizes.get(i);
                 break;
             }
         }
         
         if (size != null){
            params.setPictureSize(size.width, size.height);
         }else{
            params.setPictureSize(sizes.get(0).width, sizes.get(0).height);
         }
         
         return params;
    	
    }
    
    
    private int calculateRotation(int orientation){
    	
         android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
         android.hardware.Camera.getCameraInfo(cameraId, info);
         orientation = (orientation + 45) / 90 * 90;
         if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
             rotation = (info.orientation - orientation + 360) % 360;
         } else {  // back-facing camera
             rotation = (info.orientation + orientation) % 360;
         }
         return rotation;
    	
    }
    
    private Camera getCameraInstance(int cam){
        // TODO Auto-generated method stub
        Camera c = null;
        try {
            c = Camera.open(cam); // attempt to get a Camera instance

        }
        catch (Exception e){
        	Log.i("", e.getMessage());
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
   
    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = FileCache.getInstance().generateMediaOutputFilePath(".jpg");
            if (pictureFile == null){
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
         
            } catch (IOException e) {
            
            }
                       
            Intent intent = new Intent(activity, FullScreenImageActivity.class);
            intent.putExtra("path", pictureFile.getAbsolutePath());
            intent.putExtra("send", "true");
            startActivity(intent);
            releaseCamera();
            finish();
            
        }
    };
   
   private void releaseCamera(){
        if (myCamera != null){
            myCamera.release();        // release the camera for other applications
            myCamera = null;
        }
    }
    
   
    public class MyCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

        
        private Camera mCamera;
       
        public MyCameraSurfaceView(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int weight,
                int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
              // preview surface does not exist
              return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
              // ignore: tried to stop a non-existent preview
            }

            // make any resize, rotate or reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
            } catch (IOException e) {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
           
        }
    }
}
