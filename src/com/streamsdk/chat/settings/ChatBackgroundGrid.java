package com.streamsdk.chat.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import com.streamsdk.chat.R;

public class ChatBackgroundGrid extends Activity{
	
	 public void onCreate(Bundle savedInstanceState) {
		  
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.chatbackgroundgrid_layout);
		  GridView gv = (GridView)findViewById(R.id.gridviewchatbackground);
		  BackgroundImageAdapter bia = new BackgroundImageAdapter(this, getLocalImages());
		  gv.setAdapter(bia);
		  
	  }
	  
	  public List<Integer> getLocalImages(){
	
		  List<Integer> localImages = new ArrayList<Integer>();
		  localImages.add(R.drawable.back1);
		  localImages.add(R.drawable.back2);
		  localImages.add(R.drawable.back3);
		  localImages.add(R.drawable.back4);
		  localImages.add(R.drawable.back5);
		  localImages.add(R.drawable.back6);
		  localImages.add(R.drawable.back7);
		  localImages.add(R.drawable.back8);
		  localImages.add(R.drawable.back9);
		  localImages.add(R.drawable.back10);
		  localImages.add(R.drawable.back11);
				  
					  
		  /*localImages.add(R.drawable.b1);
		  localImages.add(R.drawable.b2);
		  localImages.add(R.drawable.b3);
		  localImages.add(R.drawable.b4);
		  localImages.add(R.drawable.b5);
		  localImages.add(R.drawable.b6);
		  localImages.add(R.drawable.b7);
		  localImages.add(R.drawable.b8);
		  localImages.add(R.drawable.b9);
		  localImages.add(R.drawable.b12);
		  localImages.add(R.drawable.b10);
		  /*localImages.add(R.drawable.b10);
		  localImages.add(R.drawable.b11);
		  localImages.add(R.drawable.b12);
		  localImages.add(R.drawable.b13);*/
		  return localImages;
		  
	  }

}
