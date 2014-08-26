package com.streamsdk.chat.group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.streamsdk.chat.R;
import com.streamsdk.util.BitmapUtils;

public class FullScreenImageDrawing extends Activity{
	
	String path = "";
	
	public void onCreate(Bundle savedInstanceState) {
		
		 super.onCreate(savedInstanceState);
		 Intent intent = getIntent();
		 path = intent.getExtras().getString("path");
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 setContentView(R.layout.fullscreendraw_layout);
		 
		 final float scale = getResources().getDisplayMetrics().density;
		 
		 final ImageView iv = (ImageView)findViewById(R.id.imageDrawView);
         DisplayMetrics metrics = new DisplayMetrics();
 		 getWindowManager().getDefaultDisplay().getMetrics(metrics);
 		 final Bitmap bitmap = BitmapUtils.loadImageForFullScreen(path, metrics.widthPixels, metrics.heightPixels, metrics.widthPixels);
         //iv.setImageBitmap(bitmap);
         
         ImageView postPhoto = (ImageView)findViewById(R.id.postPhtotButton);
         postPhoto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				   
		           
			
			}
		 });
         
         FrameLayout fl = (FrameLayout)findViewById(R.id.fullImageDrawFrame);
         fl.setOnDragListener(new View.OnDragListener() {
			public boolean onDrag(View arg0, DragEvent arg1) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
	}
	
	private void testedDraw(){
		
		/*Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(mutableBitmap);

		RectF rectF = new RectF();

		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		paint.setARGB(30, 0, 0, 0);
		rectF.set(0, 0, bitmap.getWidth(), 70 * scale);
		canvas.drawRect(rectF, paint);

		// text color - #3D3D3D
		paint.setColor(Color.GREEN);
		// text size in pixels
		paint.setTextSize(scale * 30);
		// text shadow
		paint.setShadowLayer(1f, 0f, 1f, Color.TRANSPARENT);
		// draw text to the Canvas center
		Rect bounds = new Rect();
		String mText = "Hello World";
		paint.getTextBounds(mText, 0, mText.length(), bounds);
		int x = (bitmap.getWidth() - bounds.width()) / 2;
		int y = (bitmap.getHeight() + bounds.height()) / 2;
		canvas.drawText(mText, x, y, paint);*/
		
	}
	
	

}
