package com.streamsdk.chat.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;
import android.util.Log;

import com.stream.api.StreamFile;
import com.stream.api.StreamUtils;
import com.streamsdk.chat.domain.IM;

public class AudioHandler {

	public static IM buildIMMessage(String fileName, int recordingTime){
		
		IM voiceIm = new IM();
		voiceIm.setVoice(true);
		voiceIm.setSelf(true);
		voiceIm.setVoiceImFileName(fileName);
		voiceIm.setRecordingTime(recordingTime);
		return voiceIm;
		
	}
	
	public static File getOutputFilePath(){
		String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		File file = new File(mFileName, "voicetalk");
		if (!file.exists())
			file.mkdir();
		return file;
	}
	
	public static IM processReceivedFile(StreamFile streamFile, String body){
		
		long currentTime = System.currentTimeMillis();
		File file = new File(getOutputFilePath(), String.valueOf(currentTime));
		try {
			file.createNewFile();
		} catch (IOException e1) {

		}
		String path = file.getAbsolutePath();
		InputStream in = streamFile.getFileObject(streamFile.getId());
		byte data[] = StreamUtils.readByteArray(in);
		try {
			FileOutputStream out = new FileOutputStream(file);
		    out.write(data);
		    out.close();
		       
		} catch (FileNotFoundException e) {
		   Log.i("", e.getMessage());
			
		} catch (IOException e) {
		   Log.i("", e.getMessage());
		}
		
		IM im = new IM();
		im.setVoice(true);
		im.setVoiceImFileName(path);
		if (body!= null)
		   im.setRecordingTime(Integer.parseInt(body));
		
	    return im;	
		
	}
	
	
}
