package com.streamsdk.chat.settings;

import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	boolean isLoggedIn;
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     Intent intent = getIntent();
	     imageId = intent.getExtras().getString("imageid");
	     index = intent.getExtras().getString("index");
	     isLoggedIn = intent.getExtras().getBoolean("loggedin");
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
		if (isLoggedIn){
		    MenuInflater inflater = getMenuInflater();
		    menu.clear();
		    inflater.inflate(R.menu.delete_menu, menu);
		}
		return super.onPrepareOptionsMenu(menu);
	 }
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		String title = (String) item.getTitle();
		if (title.equals("Delete")){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	        alertDialogBuilder.setTitle("Delete");
	        alertDialogBuilder.setMessage("Are you sure to delete this photo ?")
					.setCancelable(false)
					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							ApplicationInstance.getInstance().setDeletedPhotoId(index);
							setResult(124);
							finish();
						}
					  })
					.setNegativeButton("No",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						}
					});
	 	
	        AlertDialog alertDialog = alertDialogBuilder.create();
	 		alertDialog.show();
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
