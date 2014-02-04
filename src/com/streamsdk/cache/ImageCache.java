package com.streamsdk.cache;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

public class ImageCache {

	private static ImageCache imageCache;
	private Map<String, Bitmap> images = new HashMap<String, Bitmap>();
	
	public static ImageCache getInstance(){
		
		if (imageCache == null){
			imageCache = new ImageCache();
			return imageCache;
		}
		return imageCache;		
	}
	
	public void putNew(String id, Bitmap bitMap){
		images.put(id, bitMap);
	}
	
	
	public Bitmap getImage(String id){
		return images.get(id);
	}
	
	public byte[] getImageBytes(String path){
		Bitmap b = images.get(path);
		if (b != null){
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] byteArray = stream.toByteArray();
		    return byteArray;
		 }
		return null;		
	}
	
}
