package com.streamsdk.chat.settings;

import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.R;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

public class FullScreenProfileImage extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     Intent intent = getIntent();
	     String path = intent.getExtras().getString("imageid");
	     getActionBar().setDisplayHomeAsUpEnabled(true);
		 setContentView(R.layout.profileimagefullscreen_layout);
	     ImageView iv = (ImageView)findViewById(R.id.profileImageFullscreen);
	     boolean friendImage = intent.getBooleanExtra("friend", false);
	     Bitmap bm = null;
	     if (friendImage){
	    	 bm = ImageCache.getInstance().getFriendImage(path);
	     }else{
	    	 bm = ImageCache.getInstance().getPermImage(path);
		 }
	     iv.setImageBitmap(bm);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
      }
    }

}
