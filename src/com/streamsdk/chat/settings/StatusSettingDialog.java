package com.streamsdk.chat.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.streamsdk.cache.StatusDB;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

@SuppressLint("ValidFragment")
public class StatusSettingDialog extends DialogFragment{
	
	Activity activity;
	int textCount = 100;
	Dialog dialog;
	NewStatusListener newStatusListener;
	
	public StatusSettingDialog(NewStatusListener nsl){
		newStatusListener = nsl;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
      activity = getActivity();
	  dialog = super.onCreateDialog(savedInstanceState);
	  dialog.setTitle("Your New Status ("  + textCount + ")");
	  return dialog;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.newstatus_layout, container);
         Button cancel = (Button)view.findViewById(R.id.cancelNewstatus);
         cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    dismiss();	
			}
		 });
     
		final EditText et = (EditText)view.findViewById(R.id.enterStatus);
		et.addTextChangedListener(new TextWatcher() {          
             public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
             }                       
             
             public void beforeTextChanged(CharSequence s, int start, int count,
                     int after) {
             }                       
             public void afterTextChanged(Editable s) {
                 int length = s.length();
                 if ((textCount - length)>=0)
                    dialog.setTitle("Your New Status ("  + (textCount - length) + ")");
                 else{
                	s.delete(100,  101);
                 }
               }
          });
		
	    Button save = (Button)view.findViewById(R.id.saveNewstatus);
        save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			   String newStatus = et.getText().toString();
			   if (newStatus!=null && newStatus.length() > 0){
			      StatusDB sdb = ApplicationInstance.getInstance().getStatusDB();
			      try{
			    	  sdb.insert(newStatus);
				  }catch(Throwable t){
					  Log.i("", "");
				  }
			      newStatusListener.updateResult();
			      dismiss();
			   }
			}
		});
		 
		 
		 return view; 
	}

}
