package com.streamsdk.chat.group;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.stream.api.StreamCallback;
import com.stream.api.StreamCategoryObject;
import com.stream.api.StreamFile;
import com.stream.api.StreamObject;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;
import com.streamsdk.util.BitmapUtils;

public class FullScreenImageDrawing extends Activity{
	
	String path = "";
	android.widget.RelativeLayout.LayoutParams layoutParams = null;
	Activity activity;
	float scale;
	View popupView;
	PopupWindow popupWindow;
	RadioGroup txtOptionsGroup;
	boolean top = false;
	boolean showTxt = false;
	ProgressDialog pd;
	
	public void onCreate(Bundle savedInstanceState) {
		
		 super.onCreate(savedInstanceState);
		 activity = this;
		 Intent intent = getIntent();
		 path = intent.getExtras().getString("path");
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 setContentView(R.layout.fullscreendraw_layout);
		 
		 final RelativeLayout parentLayout = (RelativeLayout)findViewById(R.id.fullscreenimagedrawLayout); 
		 
		 scale = getResources().getDisplayMetrics().density;
		 final FrameLayout fl = (FrameLayout)findViewById(R.id.fullImageDrawFrame);
		 RelativeLayout.LayoutParams layoutP = (RelativeLayout.LayoutParams)fl.getLayoutParams();
		 layoutP.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		 fl.setLayoutParams(layoutP);
		 
		 final ImageView iv = (ImageView)findViewById(R.id.imageDrawView);
         DisplayMetrics metrics = new DisplayMetrics();
 		 getWindowManager().getDefaultDisplay().getMetrics(metrics);
 		 final Bitmap bitmap = BitmapUtils.loadImageForFullScreen(path, metrics.widthPixels, metrics.heightPixels, metrics.widthPixels);
         iv.setImageBitmap(bitmap);
 		 final EditText et = (EditText)findViewById(R.id.drawText);
         /*et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View v, boolean hasFocus) {
                 if (!hasFocus) {
                     hideKeyboard();
                 } else {
                     showCursor();
                 }
             }
         });*/
    
         popupView = getLayoutInflater().inflate(R.layout.videodisappearoptions_layout, null);
         popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false); 
         
         final ImageView txtOptions = (ImageView)findViewById(R.id.textOptionsButton);
         txtOptions.setOnClickListener(new View.OnClickListener() {
  		 public void onClick(View v) {
  			   popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
  		   }
  	     });
         
         ImageView postPhoto = (ImageView)findViewById(R.id.postPhtotButton);
         postPhoto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String mText = et.getText().toString();
				if (mText.length() > 85){
					 showAlertDialog();
				}else{
				     Bitmap bm = drawText(bitmap, et.getText().toString());
				     iv.setImageBitmap(bm);
				     fl.setVisibility(View.GONE);
				     txtOptions.setVisibility(View.GONE);
				     postImages(bm);
				}
			 }
		 });
         
       /*  fl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 showCursor();
			}
		  });
         */
        ImageView useTxt = (ImageView)findViewById(R.id.useTxtButton);
        useTxt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
			  if (!showTxt){
				 showKeyboard();
		         fl.setVisibility(View.VISIBLE);
		         txtOptions.setVisibility(View.VISIBLE);
			     showTxt = true;
			  }else{
				  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
			        alertDialogBuilder
					.setMessage("Do you want to remove the text?")
					.setCancelable(false)
					.setPositiveButton("YES", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									 hideKeyboard();
									 fl.setVisibility(View.GONE);
								     txtOptions.setVisibility(View.GONE);
								     et.setText("");
								     showTxt = false;
								}
					}).setNegativeButton("NO", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
					});
					
			        AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();	   
			  }
			}
		 });
         
         
         txtOptionsGroup = (RadioGroup)popupView.findViewById(R.id.radioVideoOptions);
         RadioButton rbTop = (RadioButton)txtOptionsGroup.findViewById(R.id.radioApp);
         RadioButton rbBottom = (RadioButton)txtOptionsGroup.findViewById(R.id.radioDis);
         rbTop.setText("set txt on top");
         rbBottom.setText("set text on center");
         
         Button buttonOK = (Button)popupView.findViewById(R.id.videoOptionsButtonOK);
         buttonOK.setOnClickListener(new View.OnClickListener() {
  			public void onClick(View v) {
  				 popupWindow.dismiss();
  				 int selectedId =  txtOptionsGroup.getCheckedRadioButtonId();
  				 if (selectedId == R.id.radioDis){
  					 RelativeLayout.LayoutParams layoutP = (RelativeLayout.LayoutParams)fl.getLayoutParams();
  					 layoutP.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
  					 layoutP.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
  					 fl.setLayoutParams(layoutP);
  					 top = false;
  				 }
  				 if (selectedId == R.id.radioApp){
  					 RelativeLayout.LayoutParams layoutP = (RelativeLayout.LayoutParams)fl.getLayoutParams();
  					 layoutP.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
  					 layoutP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
  					 fl.setLayoutParams(layoutP);
  					 top = true;
  				 }
  			 }
  		});
         
         Button bCancel = (Button)popupView.findViewById(R.id.videoOptionsButtonCancel);
         bCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
			    popupWindow.dismiss();	
			}
		});
    
	}
	
	private void showDialog(String message) {
		pd = ProgressDialog.show(this, "", message, true, true);
    }
	
	
	private void gobackToMainScreen(){
		pd.dismiss();
		setResult(124);
		finish();
	}
	
    private void postImages(Bitmap bm){
    	
    	showDialog("Posting Image...");
    	//gobackToMainScreen();
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
				  StreamCategoryObject sco = new StreamCategoryObject("groupphotos");
				  sco.updateSteamCategoryObjects(sos);
			   }else{
				   
			   }
			}
		});
    	
    }
    
    private byte[] getImageBytes(Bitmap bm){
    	
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
	    return byteArray;
    	
    }
	
    private void showCursor(){
    	 if (activity != null) {
 	        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
 	    }
    }
    
	public void showKeyboard() {
	   /* if (activity != null) {
	        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	    }*/
		 InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		 if (inputMethodManager != null) {
		     inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		 }
		
	}

	public void hideKeyboard() {
	    if (activity != null) {
	        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    }
	}
	
	private void showAlertDialog(){		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
				.setMessage("The maximum words allowed to use are 100")
				.setCancelable(false)
				.setNegativeButton("TRY AGAIN",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
	}
	
	private Bitmap drawText(Bitmap bitmap, String mText){
		
		if (mText == null)
			return bitmap;
		if (mText != null && mText.equals(""))
			return bitmap;
		
		Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    
		float actualScale = (mutableBitmap.getWidth() * scale) / metrics.widthPixels;
		scale = actualScale;
		
		if (!top) {
			Canvas canvas = new Canvas(mutableBitmap);
			TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
			Rect bounds = new Rect();
			RectF rectF = new RectF();
			paint.getTextBounds(mText, 0, mText.length(), bounds);
			int y = (bitmap.getHeight() + bounds.height()) / 2;
			Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint1.setARGB(60, 0, 0, 0);
			rectF.set(0, y - (30 * scale), bitmap.getWidth(), (40 * scale + y));
			canvas.drawRect(rectF, paint1);
			paint.setColor(getResources().getColor(R.color.firstPageTexColor));
			paint.setTextSize(scale * 20);
			paint.setShadowLayer(1f, 0f, 1f, Color.TRANSPARENT);
			StaticLayout mTextLayout = new StaticLayout(mText, paint, metrics.widthPixels, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
			canvas.save();
			canvas.translate(0, y - (30 * scale));
			mTextLayout.draw(canvas);
			canvas.restore();		

		}else{
			Canvas canvas = new Canvas(mutableBitmap);
			Rect bounds = new Rect();
			RectF rectF = new RectF();
			Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint1.setARGB(60, 0, 0, 0);
			rectF.set(0, 0, bitmap.getWidth(), 70 * scale);
			canvas.drawRect(rectF, paint1);
			
			TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
			paint.getTextBounds(mText, 0, mText.length(), bounds);
			paint.setColor(getResources().getColor(R.color.firstPageTexColor));
			paint.setTextSize(scale * 20);
			paint.setShadowLayer(1f, 0f, 1f, Color.TRANSPARENT);
			StaticLayout mTextLayout = new StaticLayout(mText, paint, metrics.widthPixels, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
			canvas.save();
			canvas.translate(0, 0);
			mTextLayout.draw(canvas);
			canvas.restore();			
		}
		
		return mutableBitmap;
		
	}

}
