package com.streamsdk.chat;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.streamsdk.cache.FileCache;

public class AndroidVideoCapture extends Activity{

	private Camera myCamera;
    private MyCameraSurfaceView myCameraSurfaceView;
    private MediaRecorder mediaRecorder;

    ImageView myButton;
    SurfaceHolder surfaceHolder;
    boolean recording;
    File recordingVideoFile;
    TextView timerText = null;
    int count = 1;
    int cameraId;
    Timer timer;
    private SurfaceHolder mHolder;
    private int rotation = 0;
    private Activity activity;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        recording = false;
        activity = this;
        setContentView(R.layout.videocapture_layout);
       
        //Get Camera for preview
        cameraId = CameraInfo.CAMERA_FACING_BACK;
        myCamera = getCameraInstance(cameraId);
        
        if(myCamera == null){
            Toast.makeText(AndroidVideoCapture.this,
                    "Fail to get Camera",
                    Toast.LENGTH_LONG).show();
        }

        myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);
        FrameLayout myCameraPreview = (FrameLayout)findViewById(R.id.videoview);
        myCameraPreview.addView(myCameraSurfaceView);
       
        myButton = (ImageView)findViewById(R.id.recButton);
        
        
        timerText = (TextView)findViewById(R.id.myTextVideoTime);
        myButton.setOnClickListener(myButtonOnClickListener);
        
        ImageView change = (ImageView)findViewById(R.id.changeCam);
        
        rotation = calculateRotation(cameraId); 	
        //Camera.Parameters params  = myCamera.getParameters();
        //params.setRotation(rotation);
        //myCamera.setParameters(params);
        
        change.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (cameraId ==  CameraInfo.CAMERA_FACING_FRONT)
					cameraId = CameraInfo.CAMERA_FACING_BACK;
				else
				    cameraId = CameraInfo.CAMERA_FACING_FRONT;
		
				rotation = calculateRotation(cameraId); 	
			  
				myCamera.stopPreview();
				releaseCamera();
				myCamera = getCameraInstance(cameraId);
				Camera.Parameters params  = myCamera.getParameters();
				params.setRotation(rotation);
				myCamera.setParameters(params);
				
				try {
					myCamera.setPreviewDisplay(mHolder);
				} catch (Throwable t) {
					Log.i("", "");
				}
				myCamera.setDisplayOrientation(90);
				myCamera.startPreview();
			}
		});
        
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
   
    Button.OnClickListener myButtonOnClickListener = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(recording){
                // stop recording and release camera
            	
            	recording = false;
            	count=0;
            	timer.cancel();
                mediaRecorder.stop();  // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                
            	Intent intent = new Intent(getApplicationContext(),VideoFullScreen.class);
	    		intent.putExtra("path", recordingVideoFile.getAbsolutePath());
	    		intent.putExtra("send", "true");
		    	startActivity(intent);
                finish();
                
            }else{
               
                //Release Camera before MediaRecorder start
            	myButton.setImageResource(R.drawable.stop);
                releaseCamera();
                if(!prepareMediaRecorder()){
                    Toast.makeText(AndroidVideoCapture.this,
                            "Fail in prepareMediaRecorder()!\n - Ended -",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
               
                mediaRecorder.start();
                recording = true;
               // myButton.setText("STOP");
                timer = new Timer();
                timer.schedule(new TickClass(), 0, 1000);
               
            }
        }};
   
    private Camera getCameraInstance(int cam){
        // TODO Auto-generated method stub
        Camera c = null;
        try {
            c = Camera.open(cam); // attempt to get a Camera instance
            c.setDisplayOrientation(90);
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
   
    private boolean prepareMediaRecorder(){
    	rotation = calculateRotation(cameraId); 
    	myCamera = getCameraInstance(cameraId);
        mediaRecorder = new MediaRecorder();
        myCamera.unlock();
        mediaRecorder.setCamera(myCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

       // String path= Environment.getExternalStorageDirectory().getAbsolutePath().toString();
       // String fileName = String.valueOf(System.currentTimeMillis()) + ".mp4";
        recordingVideoFile = FileCache.getInstance().generateMediaOutputFilePath(".mp4");
      
       
        mediaRecorder.setOutputFile(recordingVideoFile.getAbsolutePath());
        mediaRecorder.setMaxDuration(30000); // Set max duration 60 sec.
        mediaRecorder.setMaxFileSize(10000000); // Set max file size 10M

        mediaRecorder.setPreviewDisplay(myCameraSurfaceView.getHolder().getSurface());
        mediaRecorder.setOrientationHint(rotation);
        //mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); 
        
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
       
    }
   
    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            myCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (myCamera != null){
            myCamera.release();        // release the camera for other applications
            myCamera = null;
        }
    }
    
    private class TickClass extends TimerTask{
		public void run() {
			runOnUiThread(new Runnable() {
				public void run() {
					timerText.setVisibility(View.VISIBLE);
					timerText.setText(String.valueOf(count));
					count++;
				}
			});
		}
    }
    
    private class MyCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

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
              if (mCamera != null){
                 mCamera.setPreviewDisplay(mHolder);
                 mCamera.startPreview();
              }
            } catch (Exception e){
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
              if (mCamera != null){
                mCamera.setPreviewDisplay(holder);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
              }
            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
           
        }
    }
}
