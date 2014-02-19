package com.streamsdk.chat.settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

public class ChatSettingsDialog extends DialogFragment{
	
	Activity activity;
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	  activity = getActivity();
	  Dialog dialog = super.onCreateDialog(savedInstanceState);
	  dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	  return dialog;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.showoptionsfragmentsdialog_layout, container);
		 Button background = (Button)view.findViewById(R.id.changeChatBackground);
		 background.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				  dismiss();
			      Intent intent = new Intent(activity, ChatBackgroundGrid.class);
				  startActivity(intent);
			}
		});
		
		 Button clearHistory = (Button)view.findViewById(R.id.clearChatHistory);
		 clearHistory.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                dismiss();
				ApplicationInstance.getInstance().getCurrentChatListener().removeHistory();
		   }
		 });
		 
		return view;
	}
}
