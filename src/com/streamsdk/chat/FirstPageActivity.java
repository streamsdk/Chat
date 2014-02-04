package com.streamsdk.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FirstPageActivity extends Activity{

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.firstpage_layout);
	 	ApplicationInstance.getInstance().setFirstPageActivity(this);
		
	    final Activity activity = this;
	    LinearLayout ll = (LinearLayout)findViewById(R.id.firstLinPageLayout);
	    TextView loginView = (TextView)ll.findViewById(R.id.loginTextView);
	    loginView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
               Intent intent = new Intent(activity, LoginActivity.class);
               startActivityForResult(intent, 0);
			}
		});
	    
	}
	
	
	 protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
			super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
			switch(requestCode) {
			
			 case 0:
			      if(resultCode == RESULT_OK){        		    	  
			        finish();
			      }
			      break; 
			}
     }
}
