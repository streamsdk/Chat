package com.streamsdk.cache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.streamsdk.chat.ApplicationInstance;

public class ImageCache {

	private static ImageCache imageCache;
	private Map<String, Bitmap> images = new HashMap<String, Bitmap>();
	private Map<String, Bitmap> chatImages = new HashMap<String, Bitmap>();
	private List<String> ids = new ArrayList<String>();
	private Map<String, Bitmap> permnent = new HashMap<String, Bitmap>();
	
	public static ImageCache getInstance(){
		
		if (imageCache == null){
			imageCache = new ImageCache();
			return imageCache;
		}
		return imageCache;		
	}
	
	public void addPermnent(String id, Bitmap bm){
		permnent.put(id, bm);
	}
	
	public byte[] getImagePem(String path){
		Bitmap b = permnent.get(path);
		if (b != null){
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] byteArray = stream.toByteArray();
		    return byteArray;
		 }
		return null;		
	}
	
	public Bitmap getPermImage(String path){
		return permnent.get(path);
	}
	
	
	public void putNew(String id, Bitmap bitMap){
		if (ids.size() > 40){
		    String idStr = ids.remove(0);
		    images.remove(idStr);
		    ids.add(id);
			images.put(id, bitMap);
		}else{
			images.put(id, bitMap);
	        ids.add(id);		
		}
	}
	
	public void putChatImages(String path, Bitmap bm){
		chatImages.put(path, bm);
	}
	
	public Bitmap getChatImage(String path){
		return chatImages.get(path);
	}
	
	public Bitmap justGetImage(String id){
		return images.get(id);
	}
	
	public Bitmap getFriendImage(String id){
		Bitmap bitmap = images.get(id);
		if (bitmap != null){
			return bitmap;
		}else{
			String fileId = ApplicationInstance.getInstance().getProfileImage(id);
			if (fileId != null && !fileId.equals("")){
				File profileImageFile = FileCache.getInstance().loadFile(fileId);
				if (profileImageFile.exists()){
					Bitmap bm = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
					if (bm != null){
						 putNew(id, bm);
					     return bm;
					}
				}
			}
		}
		return null;
	}
	
	public void removeAll(){
		chatImages.clear();
	}
	
	public byte[] getImageBytes(String path){
		Bitmap b = chatImages.get(path);
		if (b != null){
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] byteArray = stream.toByteArray();
		    return byteArray;
		 }
		return null;		
	}
	
}
