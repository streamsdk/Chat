package com.streamsdk.chat.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stream.api.StreamCallback;
import com.stream.api.StreamFile;
import com.stream.api.StreamUser;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.cache.FileCache;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.FirstPageActivity;
import com.streamsdk.chat.R;
import com.streamsdk.chat.handler.ImageHandler;
import com.streamsdk.util.BitmapUtils;

public class PreferenceScreen extends Activity{
	
	static final int REQUEST_IMAGE_PICK = 1;
	ImageView profileImageView;
	Activity activity;
	
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		 setContentView(R.layout.settings_layout);
		 activity = this;
		 LinearLayout userInfo = (LinearLayout)findViewById(R.id.preBasicUserinfo);
		 TextView userView = (TextView)userInfo.findViewById(R.id.preUsername);
		 userView.setText(ApplicationInstance.getInstance().getLoginName());
		 profileImageView = (ImageView)userInfo.findViewById(R.id.preProfileImage);
		 profileImageView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(pickPhoto , REQUEST_IMAGE_PICK);	
			}
		});
		 Bitmap bitmap = ImageCache.getInstance().getImage(ApplicationInstance.getInstance().getLoginName());
		 if (bitmap != null)
			 profileImageView.setImageBitmap(bitmap);
		 else{
			Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.yahoo_no_avatar);
			profileImageView.setImageBitmap(bm);
		  }
		 
		 TextView logout = (TextView)findViewById(R.id.logout);
		 logout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.remove("username");
			    editor.remove("password");
			    editor.commit();
				FileCache.getInstance().deleteAllFiles();
				ApplicationInstance.getInstance().getMessagingHistoryDB().deleteAll();
				ApplicationInstance.getInstance().getFriendDB().deleteAll();
				ApplicationInstance.getInstance().getChatBackgroundDB().deleteAll();
				ApplicationInstance.getInstance().getInivitationDB().deleteAll();
				ApplicationInstance.getInstance().getMessagingCountDB().deleteAll();
				Intent intent = new Intent(activity, FirstPageActivity.class);
				startActivity(intent);
				setResult(RESULT_OK);
				finish();
				ApplicationInstance.getInstance().logout();
				new Thread(new Runnable(){
					public void run(){
					   StreamXMPP.getInstance().disconnect();
					}
				}).start();
			}
		});
		 
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch(requestCode) {
		
		   case REQUEST_IMAGE_PICK:
		      if(resultCode == RESULT_OK){  
		    	  String path = ImageHandler.getImgPath(imageReturnedIntent.getData(), this);
		    	  Bitmap bitmap = BitmapUtils.loadImageForFullScreen(path, 230, 230, 300);
		    	  profileImageView.setImageBitmap(bitmap);
		    	  ImageCache.getInstance().putNew(ApplicationInstance.getInstance().getLoginName(), bitmap);
		    	  byte profileImageBytes[] = ImageCache.getInstance().getImageBytes(ApplicationInstance.getInstance().getLoginName());
		    	  final StreamFile sf = new StreamFile();
		    	  sf.postBytes(profileImageBytes, new StreamCallback() {
					public void result(boolean succeed, String errorMessage) {
					      if (succeed){
					    	  StreamUser su = new StreamUser();
					    	  su.updateUserMetadata(ApplicationInstance.PROFILE_IMAGE, sf.getId());
					    	  su.updateUserMetadataInBackground(ApplicationInstance.getInstance().getLoginName());
					      }
					}
				});
		      }
		      break;
		}
		
	}

}
