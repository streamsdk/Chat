package com.streamsdk.chat.settings;

import java.util.ArrayList;
import java.util.List;

import com.streamsdk.chat.R;
import android.graphics.Bitmap;
import android.app.Activity;
import android.os.Bundle;

public class ChatBackgroundGrid extends Activity{
	
	 List<Bitmap> images = new ArrayList<Bitmap>(); 
	 public void onCreate(Bundle savedInstanceState) {
		  
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.chatbackgroundgrid_layout);
		  
	  }
	  
	  public void loadLocalImages(){
		  
	  }

}
