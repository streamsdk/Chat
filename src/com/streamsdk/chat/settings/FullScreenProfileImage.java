package com.streamsdk.chat.settings;

import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

public class FullScreenProfileImage extends Activity{
    
	String imageId;
	String index;
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     Intent intent = getIntent();
	     imageId = intent.getExtras().getString("imageid");
	     index = intent.getExtras().getString("index");
	     getActionBar().setDisplayHomeAsUpEnabled(true);
		 setContentView(R.layout.profileimagefullscreen_layout);
	     ImageView iv = (ImageView)findViewById(R.id.profileImageFullscreen);
	     boolean friendImage = intent.getBooleanExtra("friend", false);
	     Bitmap bm = null;
	     if (friendImage){
	    	 bm = ImageCache.getInstance().getFriendImage(imageId);
	     }else{
	    	 bm = ImageCache.getInstance().getPermImage(imageId);
		 }
	     iv.setImageBitmap(bm);
	}
	

	
	public boolean onPrepareOptionsMenu(Menu menu) {
		 MenuInflater inflater = getMenuInflater();
		 menu.clear();
		 inflater.inflate(R.menu.delete_menu, menu); 
		 return super.onPrepareOptionsMenu(menu);
	 }
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
		String title = (String) item.getTitle();
		if (title.equals("Delete")){
			ApplicationInstance.getInstance().setDeletedPhotoId(index);
			setResult(124);
			finish();
		}
	    switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
               return true;
            default:
               return super.onOptionsItemSelected(item);
      }
    }

}
