package com.streamsdk.chat.settings;

import com.streamsdk.chat.R;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

public class ChatSettingsDialog extends DialogFragment{
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	  Dialog dialog = super.onCreateDialog(savedInstanceState);
	  dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	  return dialog;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.showoptionsfragmentsdialog_layout, container);
		 Button background = (Button)view.findViewById(R.id.changeChatBackground);
		 background.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		 
		return view;
	}
}
