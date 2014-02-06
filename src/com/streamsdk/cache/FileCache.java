package com.streamsdk.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.streamsdk.chat.ApplicationInstance;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileCache {

	private static FileCache fileCache;
	private static String COOL_CHAT = "swiftchat";
	
	public static FileCache getInstance(){
		
		if (fileCache == null){
			fileCache = new FileCache();
	        return fileCache;
		}
			
		return fileCache;
	}
	
	 public File getOutputMediaFile(){
	        // To be safe, you should check that the SDCard is mounted
	        // using Environment.getExternalStorageState() before doing this.

	        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AbsoluteChat");
	        // This location works best if you want the created images to be shared
	        // between applications and persist after your app has been uninstalled.

	        // Create the storage directory if it does not exist
	        if (! mediaStorageDir.exists()){
	            if (! mediaStorageDir.mkdirs()){
	                return null;
	            }
	        }

	        // Create a media file name
	        Date now = new Date();
	        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(now);
	        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
	      
	        return mediaFile;
	    }
	    
	public File getHiddenOutputFilePath(){
		Context context = ApplicationInstance.getInstance().getContext();
		File hiddenFile = new File(context.getFilesDir(), COOL_CHAT);
		if (!hiddenFile.exists()){
			hiddenFile.mkdir();
		    Log.i("stream sdk hidden file created", hiddenFile.getAbsolutePath());
		}
		return hiddenFile;
	}
	 	 
	public File getOutputFilePath(){
		String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		File hiddenFile = new File(mFileName, COOL_CHAT);
		if (!hiddenFile.exists()){
			hiddenFile.mkdir();
		    Log.i("stream sdk hidden file created", hiddenFile.getAbsolutePath());
		}
		return hiddenFile;
	}
	
	public boolean generateProfileImagePathIfDoesNotExists(String id){

		File voiceTalkPath = getOutputFilePath();
		File profileImagePath = new File(voiceTalkPath, id);
		try {
			if (!profileImagePath.exists()){
			    profileImagePath.createNewFile();
			    return false;
			}
		} catch (IOException e) {
		
		}
		return true;
	}
	
	public File generateMediaOutputFilePath(String type){
		
		File voiceTalkPath = getOutputFilePath();
		File videoPath = new File(voiceTalkPath, String.valueOf(System.currentTimeMillis()) + type);
		try {
			videoPath.createNewFile();
		} catch (IOException e) {
	
		}
		return videoPath;
	}
	
	public File loadFile(String fileId){
		File outputFile = getOutputFilePath();
		File file = new File(outputFile, fileId);
		return file;
	}
	
	public void writeStaff(File file, InputStream is){
		

		try {
	
			FileOutputStream out = new FileOutputStream(file);
			int readBytes;
			byte buf[] = new byte[2048];
			while ((readBytes = is.read(buf)) != -1) {
				out.write(buf, 0, readBytes);
			}
			out.close();
		} catch (FileNotFoundException e) {
           Log.i("", "");
		} catch (IOException e) {
		    Log.i("", "");
		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
		
	}
	
	public void writeFileToDisk(File file, InputStream is){		
		  writeStaff(file, is);
	}
	
	public void writeFileToDisk(String fileId, InputStream is) {

		    File outputFile = getOutputFilePath();
			File file = new File(outputFile, fileId);
			try {
				file.createNewFile();
			} catch (IOException e) {
	
			}
			writeStaff(file, is);
	
	}
	
}
