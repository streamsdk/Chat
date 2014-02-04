package com.streamsdk.util;


import android.graphics.Bitmap;

public class ImageUtils {
	
	
	public static ImageSize getResultSize(Bitmap retval, int width, int height){
		
		 ImageSize sourceSize = new ImageSize(retval.getWidth(), retval.getHeight());
		 ImageSize targetSize = new ImageSize(width, height);
		 if (retval.getWidth() <= width && retval.getHeight() <= height)
		 {
			return sourceSize;
		 }
		 else if (retval.getWidth() <= width && retval.getHeight() >= height)
		 {
			targetSize = new ImageSize(width, retval.getHeight());
		 }
		 else if (retval.getWidth() > width && retval.getHeight() < height)
		 {
			targetSize = new ImageSize(retval.getWidth(), height);
		 }

		 ImageSize resultSize = fitToAspect(targetSize, sourceSize);
		// ImageSize resultSize = fitToAspect(sourceSize, targetSize);
			 
		 return resultSize;
		
	}
	
	public static ImageSize fitToAspect(ImageSize sizeToFit, ImageSize actualSize)
	{
		ImageSize resized = new ImageSize(actualSize.getWidth(), sizeToFit.getHeight() * actualSize.getWidth() / sizeToFit.getWidth());
		if (resized.getHeight() > actualSize.getHeight())
			resized = sizeDownByHeight(resized, actualSize);
		return resized;
	}

	public static ImageSize sizeDownByHeight(ImageSize resized, ImageSize actual)
	{
		return new ImageSize(resized.getWidth() * actual.getHeight() / resized.getHeight(), actual.getHeight());
	}

}
