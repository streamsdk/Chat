package com.streamsdk.cache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.streamsdk.chat.ApplicationInstance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class ImageCache {

	private static ImageCache imageCache;
	private Map<String, Bitmap> images = new HashMap<String, Bitmap>();
	private List<String> ids = new ArrayList<String>();
	
	public static ImageCache getInstance(){
		
		if (imageCache == null){
			imageCache = new ImageCache();
			return imageCache;
		}
		return imageCache;		
	}
	
	public void putNew(String id, Bitmap bitMap){
		if (ids.size() > 30){
		    String idStr = ids.remove(0);
		    images.remove(idStr);
		    ids.add(id);
			images.put(id, bitMap);
		}else{
			images.put(id, bitMap);
	        ids.add(id);		
		}
	}
	
	
	public Bitmap getImage(String id){
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
		
		String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		Set<String> keyPath = images.keySet();
		Set<String> keyToBeRemoved = new HashSet<String>();
		for (String key : keyPath){
			if (key.startsWith(mFileName)){
				keyToBeRemoved.add(key);
			}
		}
		
		for (String kr : keyToBeRemoved){
			Log.i("remove path", kr);
			images.remove(kr);
		}
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
