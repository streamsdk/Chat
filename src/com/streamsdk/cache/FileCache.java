package com.streamsdk.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.streamsdk.chat.ApplicationInstance;

public class FileCache {

	private static FileCache fileCache;
	private static String COOL_CHAT = "coolchat";
	
	public static FileCache getInstance(){
		
		if (fileCache == null){
			fileCache = new FileCache();
	        return fileCache;
		}
			
		return fileCache;
	}
	
	public void deleteAllFiles(){
		
		String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		File hiddenFile = new File(mFileName, COOL_CHAT);
        String listFiles[] = hiddenFile.list();          		
		for (String strFile : listFiles){
			File file = new File(hiddenFile, strFile);
			file.delete();
		}
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
		//return getHiddenOutputFilePath();
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
