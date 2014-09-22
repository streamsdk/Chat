package com.streamsdk.chat.group;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.stream.api.StreamCallback;
import com.stream.api.StreamCategoryObject;
import com.stream.api.StreamFile;
import com.stream.api.StreamObject;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class FullScreenTextDrawing extends Activity{

	float scale;
	ProgressDialog pd;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullscreentextdraw_layout);
		final DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    scale = getResources().getDisplayMetrics().density;
		final EditText et = (EditText)findViewById(R.id.fullscreenDrawText);
		ImageView iv = (ImageView)findViewById(R.id.postTextButton);
		iv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
	            Bitmap bm = drawText(et.getText().toString(), metrics);       
				postImages(bm);
			}
		});
		
	}
	
	private void showDialog(String message) {
		pd = ProgressDialog.show(this, "", message, true, true);
    }
	
	private void gobackToMainScreen(){
		pd.dismiss();
		setResult(RESULT_OK);
		finish();
	}
	
	private byte[] getImageBytes(Bitmap bm){
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}
	
	private void postImages(Bitmap bm){
	    	
	    	showDialog("Posting Image...");
	    	final StreamFile sf = new StreamFile();
	    	final byte postBytes[] = getImageBytes(bm);
	    	sf.postBytes(postBytes, new StreamCallback() {
				public void result(boolean succeed, String errorMessage) {
					gobackToMainScreen(); 
					if (succeed){
					  List<StreamObject> sos = new ArrayList<StreamObject>();
					  StreamObject so = new StreamObject();
					  so.put("postedBy", ApplicationInstance.getInstance().getLoginName());
					  String fileId = sf.getId();
					  so.put("fileId", fileId);
					  so.put("length", String.valueOf(postBytes.length));
					  sos.add(so);
					  ApplicationInstance.getInstance().addGroupPosts(so);
					  StreamCategoryObject sco = new StreamCategoryObject("groupphotos");
					  sco.updateSteamCategoryObjects(sos);
				   }else{
					   
				   }
				}
		});
	    	
	  }
	
	private Bitmap drawText(String text, DisplayMetrics metrics){
		
		Bitmap.Config conf = Bitmap.Config.ARGB_4444; // see other conf types
		Bitmap bmp = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, conf); // this creates a MUTABLE bitmap
		Canvas canvas = new Canvas(bmp);
		canvas.drawColor(Color.WHITE);
		Rect bounds = new Rect();
		TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(scale * 25);
		paint.setShadowLayer(1f, 0f, 1f, Color.TRANSPARENT);
		paint.getTextBounds(text, 0, text.length(), bounds);
		int y = (bmp.getHeight() + bounds.height()) / 2;
		StaticLayout mTextLayout = new StaticLayout(text, paint, canvas.getWidth(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
		canvas.save();
		canvas.translate(0, y - (30 * scale));
		mTextLayout.draw(canvas);
		canvas.restore();
		return bmp;
		
	}
}
