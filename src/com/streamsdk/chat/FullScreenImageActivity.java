package com.streamsdk.chat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.streamsdk.util.BitmapUtils;

public class FullScreenImageActivity extends Activity{

	 
	 String path;
	 ImageView retakePhotoButton;
	 ImageView sendPhotoButton;
	 Activity activity;
	 String duration;
	 Timer timer;
	 TextView timeText;
	 int count;
	 ImageView savePhotoButton;
	 private PopupWindow popupWindow;
	 private View popUpView;
	 private NumberPicker np;
	 private int timeout = -1;
	 
	 public void onCreate(Bundle savedInstanceState) {
		    
		    super.onCreate(savedInstanceState);
		    Intent intent = getIntent();
            path = intent.getExtras().getString("path");
            String send = intent.getExtras().getString("send");
            final String fromGalleryPick = intent.getExtras().getString("fromgallery");
            duration = intent.getExtras().getString("duration");
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		    setContentView(R.layout.fullscreenimage_layout);
		    final RelativeLayout parentLayout = (RelativeLayout)findViewById(R.id.fullscreenimageLayout);
		    activity = this;
		    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		    ImageView iv = (ImageView)findViewById(R.id.imageView);
            DisplayMetrics metrics = new DisplayMetrics();
    		getWindowManager().getDefaultDisplay().getMetrics(metrics);
    		Bitmap bitmap = BitmapUtils.loadImageForFullScreen(path, metrics.widthPixels, metrics.heightPixels, metrics.widthPixels);
            iv.setImageBitmap(bitmap);
            FrameLayout fl = (FrameLayout)findViewById(R.id.timeCountLayout);
            ImageView numPicker = (ImageView)findViewById(R.id.popNum);
            savePhotoButton = (ImageView)findViewById(R.id.savePhotoButton);
        	FrameLayout fTop = (FrameLayout)findViewById(R.id.fullImageFramgeTop);
        	FrameLayout fBottom = (FrameLayout)findViewById(R.id.fullImageFrameBottom);
            
            if (duration != null){
            	fTop.setVisibility(View.GONE);
            	fBottom.setVisibility(View.GONE);
            	savePhotoButton.setVisibility(View.GONE);
            	String countText = duration.substring(0, duration.length()-1);
                count = Integer.parseInt(countText);
            	timer = new Timer();
                timer.schedule(new TickClass(), 0, 1000);
                fl.setVisibility(View.VISIBLE);
                numPicker.setVisibility(View.VISIBLE);
                timeText = (TextView)fl.findViewById(R.id.txtTimeCount);
            }else{
		        fl.setVisibility(View.GONE);
		        numPicker.setVisibility(View.GONE);
            }
		    
        	popUpView = getLayoutInflater().inflate(R.layout.numberpicker_layout, null);
            popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);
            np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
            np.setMaxValue(20);
            np.setMinValue(3);
            np.setWrapSelectorWheel(true);
            
            
            if (send != null){
            
                numPicker.setOnClickListener(new View.OnClickListener() {
        			public void onClick(View v) {
        			    popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
        			}
        		});
            	
               numPicker.setVisibility(View.VISIBLE);
                
               retakePhotoButton = (ImageView)findViewById(R.id.retakePhotoButton);
		       retakePhotoButton.setVisibility(View.VISIBLE);
               retakePhotoButton.setOnClickListener(new View.OnClickListener() {
				  public void onClick(View v) {
					if(fromGalleryPick == null){
					     File file  = new File(path);
					     file.delete();
				         Intent intent = new Intent(activity, AndroidPhotoCapture.class);
				         startActivity(intent);
				    }
					finish();
				  }
		  	   });
               
               sendPhotoButton = (ImageView)findViewById(R.id.sendPhtotButton);
               sendPhotoButton.setVisibility(View.VISIBLE);
               sendPhotoButton.setOnClickListener(new View.OnClickListener() {
				   public void onClick(View v) {
					    ApplicationInstance.getInstance().setPhotoTakenPath(path);
					    Log.i("timeout", String.valueOf(timeout));
					    if (timeout != -1)
					    	ApplicationInstance.getInstance().setPhotoTimeout(timeout);
					    setResult(RESULT_OK);
			            finish();
				   }
			  });
            }else{
               numPicker.setVisibility(View.GONE);
               fBottom.setVisibility(View.GONE);
            }
            
            
            Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
            buttonOK.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				 popupWindow.dismiss();
    				 timeout = np.getValue();
    				 Log.i("", String.valueOf(timeout));
    			}
    		});
            
            Button buttonCancel = (Button)popUpView.findViewById(R.id.numPickerButtonCancel);
            buttonCancel.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    			     popupWindow.dismiss();	
    			}
    		});
            
            
	 }
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = (String)item.getTitle();
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			finish();
			return true;
		case 0:
			if (title.equals("Save")) {
			    try {
			    	//TODO: out of memory sometimes
					MediaStore.Images.Media.insertImage(getContentResolver(), path, "" , "");
				} catch (FileNotFoundException e) {
		
				}
			    Toast.makeText(getApplicationContext(), "saved to photo gallery", Toast.LENGTH_LONG).show();
           } else {

			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	 
	public boolean onCreateOptionsMenu(Menu menu){
	   if (duration == null)
	       menu.add("Save").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	   return true;
	}
	
	 private class TickClass extends TimerTask{
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						timeText.setText(String.valueOf(count));
						count--;
						if (count == 0){
							File imageFile = new File(path);
							imageFile.delete();
						    onBackPressed();
						}
					}
				});
			}
	    }
}
