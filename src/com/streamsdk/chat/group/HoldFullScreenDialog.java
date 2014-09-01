package com.streamsdk.chat.group;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.streamsdk.chat.R;

public class HoldFullScreenDialog extends DialogFragment{
   
	  int first = 0;
	
	  static HoldFullScreenDialog newInstance(){
		  HoldFullScreenDialog f = new HoldFullScreenDialog();
		  return f;
	  }
	  
	  @Override
	  public Dialog onCreateDialog(final Bundle savedInstanceState) {

	      // the content
	      final RelativeLayout root = new RelativeLayout(getActivity());
	      root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

	      // creating the fullscreen dialog
	      Dialog dialog = new Dialog(getActivity());
	      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	      dialog.setContentView(root);
	      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
	      dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
          return dialog;   
	      
	  }
	
	
	
	 
	  
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		  View v = inflater.inflate(R.layout.holdfullscreen_layout, container, false);
		  return v;
	  }
	  
		
		 


}
