package com.streamsdk.chat.handler;

import java.io.File;
import java.io.InputStream;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import com.stream.api.StreamFile;
import com.streamsdk.cache.FileCache;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.domain.IM;
import com.streamsdk.util.BitmapUtils;

public class ImageHandler {

	public static IM buildImageIMMessage(String path){
		
		 IM im = new IM();
		 if (path.endsWith(".mp4"))
			 im.setVideo(true);
		 else
             im.setImage(true);
         im.setSelf(true);
         im.storeSendImage(path);
		 return im;
		 
	}
	
	
	public static File createImageFile() {
	     File file = FileCache.getInstance().getOutputFilePath();
	     String fileId = String.valueOf(System.currentTimeMillis() + ".jpg");
	     return new File(file, fileId);
	}
	
	public static String getImgPath(Uri uri, Activity ac) {
		Cursor cursor = ac.getContentResolver().query(uri, null, null, null, null);
		if (cursor == null) { 
			return uri.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
		}
    }
	
	public static IM processReceivedFriendImage(StreamFile streamFile, boolean isImage) throws Exception{
		
		IM im = new IM();
		im.setSelf(false);
		InputStream in = streamFile.getFileObject(streamFile.getId());
		FileCache.getInstance().writeFileToDisk(streamFile.getId(), in);
		File file = FileCache.getInstance().loadFile(streamFile.getId());
		if (isImage){
		   im.setImage(true);
		   Bitmap sBitMap = BitmapUtils.loadImageForFullScreen(file.getAbsolutePath(), 230, 230, 300);
		   ImageCache.getInstance().putNew(file.getAbsolutePath(), sBitMap);	
		}else{
		   im.setVideo(true);
		   long length = file.length();
		   //TODO: sometimes video can not be decoded
		   Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
		   Bitmap resizedBitmap = Bitmap.createScaledBitmap(thumb, 230, 230, false);
		   ImageCache.getInstance().putNew(file.getAbsolutePath(), resizedBitmap);
		}
		im.setReceivedFilePath(file.getAbsolutePath());
		return im;
		
	}
	
	
	
}
