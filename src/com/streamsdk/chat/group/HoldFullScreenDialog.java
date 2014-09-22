package com.streamsdk.chat.group;

import java.io.File;
import java.io.InputStream;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.stream.api.StreamFile;
import com.streamsdk.cache.FileCache;
import com.streamsdk.chat.R;
import com.streamsdk.util.BitmapUtils;

public class HoldFullScreenDialog extends DialogFragment{
   
	  int first = 0;
	  ImageView iv;
	
	
	  static HoldFullScreenDialog newInstance(){
		  HoldFullScreenDialog f = new HoldFullScreenDialog();
		  return f;
	  }
	  
	  @Override
	  public Dialog onCreateDialog(final Bundle savedInstanceState) {

	      // the content
	      final RelativeLayout root = new RelativeLayout(getActivity());
	      root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

	      // creating the fullscreen dialog
	      Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
	      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	      dialog.setContentView(root);
	      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
	      dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	      
          return dialog;   
	      
	  }
	 
	  public void startDownloading(final String fileId, final String length, final DisplayMetrics metrics){
		  new Thread(new Runnable(){
			public void run() {
				boolean exists = FileCache.getInstance().generateProfileImagePathIfDoesNotExists(fileId);
				File profileImageFile = null;
				if (exists){
					profileImageFile = FileCache.getInstance().loadFile(fileId);
					long l = Long.parseLong(length);
					if (profileImageFile.length() != l){
						profileImageFile.delete();
					    profileImageFile = downloadFile(profileImageFile, fileId);
					}
				}else{
					profileImageFile = downloadFile(profileImageFile, fileId);
				}
			
				Bitmap bitmap = BitmapUtils.loadImageForFullScreen(profileImageFile.getAbsolutePath(),
						metrics.widthPixels, metrics.heightPixels, metrics.widthPixels);
				if (bitmap != null)
				    show(bitmap);	
			}
			  
		  }).start();
		  
	  }
	  
	  private File downloadFile(File profileImageFile, String fileId){
			StreamFile sf = new StreamFile();
			InputStream is = sf.getFileObject(fileId);
			profileImageFile = FileCache.getInstance().loadFile(fileId);
			FileCache.getInstance().writeFileToDisk(profileImageFile, is);
			return profileImageFile;
	  }
	  
	  public void show(final Bitmap bm){
		  Activity activity = getActivity();
		  if (activity != null){
		       activity.runOnUiThread(new Runnable(){
				  public void run() {
				      iv.setImageBitmap(bm);
				      iv.setVisibility(View.VISIBLE);
				  }
			   });
		  }
	  }
	  
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		  View v = inflater.inflate(R.layout.holdfullscreen_layout, container, false);
		  iv = (ImageView)v.findViewById(R.id.imageHoldView);
		  
		  return v;
	  }

}
