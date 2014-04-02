package com.streamsdk.chat.settings;

import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

public class StatusSettingActivity extends Activity{
	
	public static final int CHECKBOXTAG = 555566;
	LinearLayout statusLayout;
	@Override
    public void onPause(){
		super.onPause();
		ApplicationInstance.getInstance().setVisiable(false);
    }
	
	protected void onResume(){
		super.onResume();
		ApplicationInstance.getInstance().setVisiable(true);		
	}
	
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		 setContentView(R.layout.status_settings);
		 
		 statusLayout = (LinearLayout)findViewById(R.id.statusSettingLayout);
		 int size = statusLayout.getChildCount();
		 for (int i=0; i < size; i++){
			 View v = statusLayout.getChildAt(i);
			 if (v instanceof CheckedTextView){
				CheckedTextView ctv = (CheckedTextView)v;
				ctv.setTag(Integer.valueOf(555566 + i));
				ctv.setOnClickListener(new View.OnClickListener() {
					public void onClick(View checkedView) {
						uncheckAll();
						Integer checkedValue = (Integer)checkedView.getTag();
						Log.i("", String.valueOf(checkedValue.intValue()));
						CheckedTextView checked = (CheckedTextView)checkedView;
						checked.setChecked(true);
						ApplicationInstance.getInstance().setCurrentStatus(checked.getText().toString());
					}
				});
			 }
		 }
	}
	
	private void uncheckAll(){
		 statusLayout = (LinearLayout)findViewById(R.id.statusSettingLayout);
		 int size = statusLayout.getChildCount();
		 for (int i=0; i < size; i++){
			 View v = statusLayout.getChildAt(i);
			 if (v instanceof CheckedTextView){
			     CheckedTextView ctv = (CheckedTextView)v;
				 ctv.setChecked(false);		
			 }
		 }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			 ApplicationInstance.getInstance().setCurrentChatListener(null);
			 onBackPressed();
			 return true;
		default:
			 return super.onOptionsItemSelected(item);
		}
	}
	

}
