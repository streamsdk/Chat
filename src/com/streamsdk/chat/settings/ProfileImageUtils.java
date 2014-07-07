package com.streamsdk.chat.settings;

import java.util.Map;

import com.streamsdk.chat.ApplicationInstance;

public class ProfileImageUtils {

	public static String getProfileImages(Map<String, String> metaData){
		
		 String profileImages = metaData.get(ApplicationInstance.NEW_PROFILE_IMAGE);
		 if (profileImages != null)
			 return profileImages;
		 else{
			 profileImages = metaData.get(ApplicationInstance.PROFILE_IMAGE);
			 return profileImages;	 
		 }
	}
	
}
