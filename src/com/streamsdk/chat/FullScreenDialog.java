package com.streamsdk.chat;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FullScreenDialog extends DialogFragment{
   
	  private static Bitmap bm;
	
	  static FullScreenDialog newInstance(Bitmap bmm){
		  FullScreenDialog f = new FullScreenDialog();
		  bm = bmm;
		  return f;
	  }
	  
	  @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		  View v = inflater.inflate(R.layout.fullscreenimage_layout, container, false);
		  ImageView iv = (ImageView) v.findViewById(R.id.imageView);
		  iv.setImageBitmap(bm);
		  return v;
	  }

}
