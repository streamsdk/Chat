package com.streamsdk.chat.group;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
         et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View v, boolean hasFocus) {
                 if (!hasFocus) {
                     hideKeyboard();
                 } else {
                     showKeyboard();
                 }
             }
         });
         
         et.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable ed) {
				Log.i("", "");
			}
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				Log.i("", "");
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				Log.i("","");
			}
         });
         
         ImageView postPhoto = (ImageView)findViewById(R.id.postPhtotButton);
         postPhoto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String mText = et.getText().toString();
	            if (mText.length() > 140){
					showAlertDialog();
				}else{
				    Bitmap bm = drawText(bitmap, et.getText().toString());
				    iv.setImageBitmap(bm);
				    fl.setVisibility(View.GONE);
				}
			}
		 });
         
         fl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 showKeyboard();
			}
		});
         
         
         popupView = getLayoutInflater().inflate(R.layout.videodisappearoptions_layout, null);
         popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false); 
         
         ImageView txtOptions = (ImageView)findViewById(R.id.textOptionsButton);
         txtOptions.setOnClickListener(new View.OnClickListener() {
  		 public void onClick(View v) {
  			   popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
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
    
	}
	
	public void showKeyboard() {
	    if (activity != null) {
	        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
				.setMessage("The maximum words allowed to use are 40")
				.setCancelable(false)
				.setNegativeButton("TRY AGAIN",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
	}
	
	private String wrapString(String string, int charWrap) {
	    int lastBreak = 0;
	    int nextBreak = charWrap;
	    if (string.length() > charWrap) {
	        String setString = "";
	        do {
	            while (string.charAt(nextBreak) != ' ' && nextBreak > lastBreak) {
	                nextBreak--;
	            }
	            if (nextBreak == lastBreak) {
	                nextBreak = lastBreak + charWrap;
	            }
	            setString += string.substring(lastBreak, nextBreak).trim() + "\n";
	            lastBreak = nextBreak;
	            nextBreak += charWrap;

	        } while (nextBreak < string.length());
	        setString += string.substring(lastBreak).trim();
	        return setString;
	    } else {
	        return string;
	    }
	}
	
	private Bitmap drawText(Bitmap bitmap, String mText){
		
		
		
		Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		if (!top) {
			Canvas canvas = new Canvas(mutableBitmap);
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			Rect bounds = new Rect();
			RectF rectF = new RectF();
			int y = (bitmap.getHeight() + bounds.height()) / 2;
			paint.getTextBounds(mText, 0, mText.length(), bounds);
			Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint1.setARGB(60, 0, 0, 0);
			rectF.set(0, y - (30 * scale), bitmap.getWidth(), (30 * scale + y));
			canvas.drawRect(rectF, paint1);
			
			paint.setColor(getResources().getColor(R.color.firstPageTexColor));
			paint.setTextSize(scale * 20);
			paint.setShadowLayer(1f, 0f, 1f, Color.TRANSPARENT);
			canvas.drawText(mText, 0, y, paint);

		}else{
			Canvas canvas = new Canvas(mutableBitmap);
		
			RectF rectF = new RectF();
			Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint1.setARGB(60, 0, 0, 0);
			rectF.set(0, 0, bitmap.getWidth(), 60 * scale);
			canvas.drawRect(rectF, paint1);
			
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(getResources().getColor(R.color.firstPageTexColor));
			paint.setTextSize(scale * 20);
			
			paint.setShadowLayer(1f, 0f, 1f, Color.TRANSPARENT);
			
			int s = (int)paint.getFontSpacing();
			String str = wrapString(mText, 34);
			String st[] = str.split("\n");
			
			canvas.drawText(str, 0, 30 * scale, paint);
	
			
		}
		
		return mutableBitmap;
		
	}
	
	

}
