package com.streamsdk.chat.settings;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.streamsdk.chat.ApplicationInstance;

public class BackgroundImageAdapter extends BaseAdapter{

   private int imageCount = 8;
   private List<Integer> localImages;
   private Activity context;
   
   public BackgroundImageAdapter(Activity con, List<Integer> images){
	   localImages = images;
	   context = con;
   }
   
   public int getCount() {
 		return imageCount;
	}
	
   public Object getItem(int position) {
	    return localImages.get(position);
	}

	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup vg) {
	
		  final Object item = getItem(position);
		  ImageView imageView = new ImageView(context);
		  if (item instanceof Integer){
			  imageView.setImageResource((Integer)item);
		      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		      imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
		  }
		  
		  imageView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 if (item instanceof Integer){
					 String userName = ApplicationInstance.getInstance().getCurrentChatbackgroundReceiver();
				     ApplicationInstance.getInstance().getChatBackgroundDB().updateChatBackground(userName, String.valueOf((Integer)item), "");
				 }
				context.finish();
			}
		  });
		  
		  return imageView;

	}

}
