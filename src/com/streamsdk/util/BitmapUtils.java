package com.streamsdk.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

public class BitmapUtils {

	
	public static int getRotation(String path){
	
		 int rotate = -1;
         ExifInterface exif = null;
			try {
				exif = new ExifInterface(path);
			} catch (IOException e) {
			    Log.i("", "no exif image");
			}
			
	     int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

         switch (orientation) {
         case ExifInterface.ORIENTATION_ROTATE_270:
             rotate = 270;
             break;
         case ExifInterface.ORIENTATION_ROTATE_180:
             rotate = 180;
             break;
         case ExifInterface.ORIENTATION_ROTATE_90:
             rotate = 90;
             break;
         }
         
         return rotate;
	}
	
	public static Bitmap loadImageForFullScreen(String path, int width, int height, int imageMaxSize){
		
		int rotation = getRotation(path);
		try {
			
			InputStream is = new BufferedInputStream(new FileInputStream(path));
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, options);

			// Calculate inSampleSize
			int s = calculateInSampleSize(options, width, height);
			if (s < 4 && s != 1)
				s = 4;
			options.inSampleSize = s;
				

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
		  try{
			is.close();
		  }catch(Throwable t){}
		    is = new BufferedInputStream(new FileInputStream(path));
			Bitmap selectImg = BitmapFactory.decodeStream(is, null, options);
			if (selectImg == null)
				return null;
			int w = selectImg.getWidth();
			int h = selectImg.getHeight();
			if (w > h){
			   Matrix matrix = new Matrix();
			   if (rotation != -1){
				  matrix.postRotate(rotation);
			   }else{
			      matrix.postRotate(90);
			   }
			   selectImg = Bitmap.createBitmap(selectImg, 0, 0, selectImg.getWidth(), selectImg.getHeight(), matrix, true);
			}
			
			return selectImg;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
	
	/*public static Bitmap loadImageForFullScreen(String path, int width, int height, int imageMaxSize){
		Bitmap retval = null;
		try{
		    BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
		    InputStream is = new BufferedInputStream(new FileInputStream(path));
		    retval = BitmapFactory.decodeStream(is, null, o);
		    /*if (retval == null){
		    	retval = BitmapFactory.decodeFile(path);
		        if (retval == null){
		        	is = new BufferedInputStream(new FileInputStream(path));
		        	retval = BitmapFactory.decodeStream(is);
		        }
		    }*/
		    //retval = BitmapFactory.decodeFile(path);
			/*int scale = 1;
	        if (o.outHeight > imageMaxSize || o.outWidth > imageMaxSize) {
	            scale = (int) Math.pow(2, (int) Math.round(Math.log(imageMaxSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
	        }
			//is.close();
			BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        is = new BufferedInputStream(new FileInputStream(path));
	        retval = BitmapFactory.decodeStream(is, null, o2);
            ImageSize adjustedSize = ImageUtils.getResultSize(retval, width, height);
			retval = Bitmap.createScaledBitmap(retval, adjustedSize.getWidth(), adjustedSize.getHeight(), true);
			is.close();
			return retval;
		  }catch(Throwable t){
			  t.printStackTrace();
		  }
		  return null;
	}*/
	
	
	
}
