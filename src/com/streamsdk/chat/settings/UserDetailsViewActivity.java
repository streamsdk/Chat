package com.streamsdk.chat.settings;

import com.stream.api.ThreadPoolService;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserDetailsViewActivity extends PreferenceScreen{
	
	String userName;
	public void onCreate(Bundle savedInstanceState) {
		
		 Intent intent = getIntent();
         userName = intent.getExtras().getString("username");
         super.onCreate(savedInstanceState);
		 activity = this;
		 
	}
	
	protected void getUserMetaData(){
		 userMetadata = ApplicationInstance.getInstance().getFriendMetadata(userName);
	}
	
	protected void editListener(){
		
	}
	
	protected void setUserInfo(){
		 //basic user info
		 userInfo = (LinearLayout)findViewById(R.id.preBasicUserinfo);
		 TextView userView = (TextView)userInfo.findViewById(R.id.preUsername);
		 userView.setText(userName);
	}
	
	protected void logout(){
		 TextView logout = (TextView)findViewById(R.id.logout);
		 logout.setVisibility(View.GONE);
	}
	
	protected void setProfileImageListener(String name){
		super.setProfileImageListener(userName);
	}
	
	protected void setStatus(){
		 setStatus = (Button)userInfo.findViewById(R.id.userStatus);
		 String status = null;
		 if (userMetadata != null)
		     status = userMetadata.get(ApplicationInstance.STATUS);
		 if (status != null){
			 setStatus.setText(status);
		 }else{
			 setStatus.setText("Hey there! I am using CoolChat.");
		 }
	}
	

	protected boolean isFromLoggedInUser(){
		return false;
	}
	
	public synchronized void updateUI(boolean add){
		LinearLayout profileImageLayout = (LinearLayout)userInfo.findViewById(R.id.profileImageViewLayout);
		 int count = profileImageLayout.getChildCount();
		 for (int i=0; i<count; i++){
			 ImageView view = (ImageView)profileImageLayout.getChildAt(i);
			 if (i != 0){
				 Bitmap bitmap = ImageCache.getInstance().getFriendImage(userName + i);
				 if (bitmap != null){
					 view.setImageBitmap(bitmap);
				 }
			 }else{
				 Bitmap bitmap = ImageCache.getInstance().getFriendImage(userName);
				 if (bitmap != null){
					 view.setImageBitmap(bitmap);
				 }
			 }
		 }
		 if (add){
			 ImageView iv = crateImageView();
			 profileImageLayout.addView(iv);
			 setProfileImageListener(userName);
		 }
		 profileImageLayout.invalidate();
	}
	
	protected void buildProfileImages(){
		
		 String profileImages = ProfileImageUtils.getProfileImages(userMetadata);
		 LinearLayout profileImageLayout = (LinearLayout)userInfo.findViewById(R.id.profileImageViewLayout);
		 if (profileImages != null && !profileImages.equals("")){
			 if (profileImages.contains("|")){
					String images[] = profileImages.split("\\|");
					for (int i=0; i < images.length; i++){
						Bitmap bitmap = null;
						if (i == 0){
							bitmap = ImageCache.getInstance().getFriendImage(userName);
					    }else{
							bitmap = ImageCache.getInstance().getFriendImage(userName + i);
						}
						if (bitmap != null){
							ImageView iv = crateImageView();
					        iv.setImageBitmap(bitmap);
					        profileImageLayout.addView(iv);
						}else{
							ImageView iv = crateImageView();
						    profileImageLayout.addView(iv);
						    if (i != 0){
						       DownloadProfileImageThread dpt = new DownloadProfileImageThread(this, images[i], i, userName);
						       ThreadPoolService.getInstance().submitTask(dpt);
						   }
						}
					}
				 }else{
					Bitmap bitmap = ImageCache.getInstance().getFriendImage(userName);
			        ImageView iv = crateImageView();
			        if (bitmap != null)
			            iv.setImageBitmap(bitmap);
			        profileImageLayout.addView(iv);
			     }
		 }else{
			 ImageView iv = crateImageView();
		     profileImageLayout.addView(iv);
		 }
		 profileImageLayout.invalidate();
		 
	}

}
