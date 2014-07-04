package com.streamsdk.chat.settings;

import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserDetailsViewActivity extends PreferenceScreen{
	
	String userName;
	public void onCreate(Bundle savedInstanceState) {
		
		 super.onCreate(savedInstanceState);
		 Intent intent = getIntent();
         userName = intent.getExtras().getString("username");
         userMetadata = ApplicationInstance.getInstance().getFriendMetadata(userName);
         activity = this;
		 
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
	
	protected void setProfileImageListener(){}
	
	protected void setStatus(){
		 setStatus = (Button)userInfo.findViewById(R.id.userStatus);
		 String status = userMetadata.get(ApplicationInstance.STATUS);
		 if (status != null){
			 setStatus.setText(status);
		 }else{
			 setStatus.setText("Hey there! I am using CoolChat.");
		 }
	}
	
	protected void buildProfileImages(){
		
		 String profileImages = userMetadata.get(ApplicationInstance.PROFILE_IMAGE);
		 LinearLayout profileImageLayout = (LinearLayout)userInfo.findViewById(R.id.profileImageViewLayout);
		 if (profileImages != null && !profileImages.equals("")){
			 ImageView iv = crateImageView();
		     profileImageLayout.addView(iv);
		 }else{
			 ImageView iv = crateImageView();
		     profileImageLayout.addView(iv);
		 }
		 profileImageLayout.invalidate();
		 
	}

}
