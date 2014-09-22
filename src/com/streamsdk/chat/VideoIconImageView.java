package com.streamsdk.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

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
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.video1);
        int w = getWidth(); 
        int h = getHeight();
        int bw  = bitmap.getWidth();
        int bh = bitmap.getHeight();
        if (video == true){
            canvas.drawBitmap(bitmap , w - 2*bw, h - 2*bh , null);
       }
    }

	public boolean isVideo() {
		return video;
	}

	public void setVideo(boolean video) {
		this.video = video;
	}
    
    
}
