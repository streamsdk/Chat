package com.streamsdk.chat.settings;

import java.io.File;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.stream.api.StreamFile;
import com.streamsdk.cache.FileCache;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.RefreshUI;

public class DownloadProfileImageThread implements Runnable{

	RefreshUI refresh;
	String fileId;
	int num;
	String userName;
	
	public DownloadProfileImageThread(RefreshUI rh, String id, int indexNum, String name){
		refresh = rh;
		fileId  = id;
		num = indexNum;
		userName = name;
	}
	
	public void run() {
	
		boolean exists = FileCache.getInstance().generateProfileImagePathIfDoesNotExists(fileId);
		File profileImageFile;
		Bitmap bitmap;
		if (!exists){
		    profileImageFile = FileCache.getInstance().loadFile(fileId);
			StreamFile sf = new StreamFile();
			InputStream in = sf.getFileObject(fileId);
			FileCache.getInstance().writeFileToDisk(profileImageFile, in);
			bitmap = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
		 }else{
			profileImageFile = FileCache.getInstance().loadFile(fileId);
		 }
		
	     bitmap = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
	     if (bitmap != null && num != 0){
	    	  if (!userName.equals(ApplicationInstance.getInstance().getLoginName()))
				 ImageCache.getInstance().putNew(userName + num, bitmap);
		      else
		    	 ImageCache.getInstance().addPermnent(ApplicationInstance.getInstance().getLoginName() + num, bitmap);
	    	  refresh.refresh();
		 }
	    
	}
	
	

}
