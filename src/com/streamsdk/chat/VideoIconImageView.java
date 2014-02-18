package com.streamsdk.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.streamsdk.cache.ImageCache;

public class VideoIconImageView extends ImageView{

    boolean video = false;
    
	public VideoIconImageView(Context context)
    {
        super(context);
    }

    public VideoIconImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

 
    public VideoIconImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

    }
    
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Bitmap bitmap = ImageCache.getInstance().getImage("videoicon");
        if (bitmap == null){
           bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.video1);
           ImageCache.getInstance().putNew("videoicon", bitmap);   	
        }        	
        int w = getWidth();
        int h = getHeight();
        if (video == true)
            canvas.drawBitmap(bitmap , w - 100 , h - 85 , null);
  
    }

	public boolean isVideo() {
		return video;
	}

	public void setVideo(boolean video) {
		this.video = video;
	}
    
    
}
