package com.streamsdk.chat.addfriend;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.stream.api.StreamFile;
import com.stream.api.StreamUser;
import com.streamsdk.cache.FileCache;
import com.streamsdk.cache.FriendDB;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.ApplicationInstance;

public class UsersLoadThread implements Runnable{

	public void run() {
	
		StreamUser su = new StreamUser();
		su.setCurrentUserName(ApplicationInstance.getInstance().getLoginName());
		List<Map<String, String>> allUsers = su.getAllUsers();
		ApplicationInstance.getInstance().setAllUsers(allUsers);
		FriendDB db = ApplicationInstance.getInstance().getFriendDB();
		String names[] = db.getFriendsArray();
		Set<String> nameSet = new HashSet<String>();
		for (String n : names)
			nameSet.add(n);
		
		for (int i=0; i < allUsers.size(); i++){
			Map<String, String> metaData = allUsers.get(i);
			String fileId = metaData.get(ApplicationInstance.PROFILE_IMAGE);
			String name = metaData.get("name");
			if (fileId != null && !fileId.equals("") && !nameSet.contains(name)){
				boolean exists = FileCache.getInstance().generateProfileImagePathIfDoesNotExists(fileId);
				File profileImageFile;
				Bitmap bitmap;
				if (!exists){
				    profileImageFile = FileCache.getInstance().loadFile(fileId);
				    Log.i("download profile", name);
					StreamFile sf = new StreamFile();
					InputStream in = sf.getFileObject(fileId);
					FileCache.getInstance().writeFileToDisk(profileImageFile, in);
					bitmap = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
				}else{
					profileImageFile = FileCache.getInstance().loadFile(fileId);
				}
				
			    bitmap = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
				
			    if (bitmap != null){
					ImageCache.getInstance().putNew(name, bitmap);
				}
			}
		}
		Log.i("", String.valueOf(allUsers.size()));
		
	}
	
}