package com.streamsdk.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stream.api.StreamCallback;
import com.stream.api.StreamCategoryObject;
import com.stream.api.StreamObject;
import com.stream.api.StreamUser;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.chat.domain.FriendRequest;
import com.streamsdk.xmpp.ApplicationXMPPListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SignupActivity extends Activity{

	ProgressDialog pd;
	String userName;
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		final Activity activity = this;
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.signupactivity_layout);
		
		LinearLayout ll = (LinearLayout)findViewById(R.id.signupLayout);
		final EditText signupText = (EditText)ll.findViewById(R.id.signupUserName);
		signupText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		        public void onFocusChange(View v, boolean hasFocus) {
		            if (hasFocus) {
		                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		            }
		        }
		});
		    
		final Handler handler = new Handler(new Handler.Callback() {
				public boolean handleMessage(Message message) {
					showAlertDialog();
					return true;
				}
		 });
		    
		final EditText passwordText = (EditText)ll.findViewById(R.id.signupPassword);
		TextView enter = (TextView)ll.findViewById(R.id.signupButtonTextView);
		enter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			
				showDialog("Adding you as a new user, please wait...");
				userName = signupText.getText().toString();
				final String password = passwordText.getText().toString();
				StreamUser su = new StreamUser();
			    Map<String, String> metaData = new HashMap<String, String>();
			    metaData.put(ApplicationInstance.PROFILE_IMAGE, "");
			    metaData.put("name", userName);
			    metaData.put("password", password);
				su.signUp(userName, password, metaData, new StreamCallback() {
					public void result(boolean succeed, String errorMessage) {
			             if (succeed){
			            	ApplicationInstance.getInstance().setLoginName(userName);
						    ApplicationInstance.getInstance().setPassword(password);
						    saveUserInfo();
							//TODO:AUTOMATICALLY ADD FRIENDS
						    StreamCategoryObject sco = new StreamCategoryObject(userName);
						    sco.createNewStreamCategory();
						    StreamObject so = new StreamObject();
						    so.setId(userName + ApplicationInstance.messageHistory);
						    so.createNewStreamObject();
						    addAsFriend(userName, "jacky");addAsFriend(userName, "busy");addAsFriend(userName, "apple");addAsFriend(userName, "cormac");addAsFriend(userName, "android");
						    addAsFriendRequest(userName, "fatboy");addAsFriendRequest(userName, "yang");addAsFriendRequest(userName, "dog");
						    establishXMPP();
							pd.dismiss();
							Intent intent = new Intent(activity,MyFriendsActivity.class);
							startActivity(intent);
							finish();
							ApplicationInstance.getInstance().getFirstPageActivity().finish();
			             }else{
			                pd.dismiss();
						    handler.sendEmptyMessage(0); 
			             }
					}
				});
			}
		});
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			 onBackPressed();
			 return true;
		default:
			 return super.onOptionsItemSelected(item);
		}
	}
	
	private void addAsFriendRequest(String myUserName, String friendUserName){
		
		StreamObject myObject = new StreamObject();
		myObject.setId(friendUserName);
		myObject.put("status", "request");
		myObject.setToCategoriedObject(myUserName);
		myObject.updateObjectInBackground(new StreamCallback() {
			public void result(boolean succeed, String errorMessage) {}
		});
		
		StreamObject friendObject = new StreamObject();
		friendObject.setId(myUserName);
		friendObject.setToCategoriedObject(friendUserName);
		friendObject.put("status", "request");
		friendObject.updateObjectInBackground(new StreamCallback() {
			public void result(boolean succeed, String errorMessage) {}
		});
		
		List<FriendRequest> frs = new ArrayList<FriendRequest>();
		FriendRequest fr = new FriendRequest();
		fr.setFriendName(friendUserName);
		fr.setStatus("request");
		frs.add(fr);
		ApplicationInstance.getInstance().getFriendDB().syncUpdate(frs);
					
	}
	
	private void addAsFriend(String myUserName, String friendUserName){
		
		StreamObject myObject = new StreamObject();
		myObject.setId(friendUserName);
		myObject.put("status", "friend");
		myObject.setToCategoriedObject(myUserName);
		myObject.updateObjectInBackground(new StreamCallback() {
			public void result(boolean succeed, String errorMessage) {}
		});
		
		StreamObject friendObject = new StreamObject();
		friendObject.setId(myUserName);
		friendObject.setToCategoriedObject(friendUserName);
		friendObject.put("status", "friend");
		friendObject.updateObjectInBackground(new StreamCallback() {
			public void result(boolean succeed, String errorMessage) {}
		});
		
		List<FriendRequest> frs = new ArrayList<FriendRequest>();
		FriendRequest fr = new FriendRequest();
		fr.setFriendName(friendUserName);
		fr.setStatus("friend");
		frs.add(fr);
		ApplicationInstance.getInstance().getFriendDB().syncUpdate(frs);
	}
	
   private void saveUserInfo(){
	   SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
	   SharedPreferences.Editor editor = settings.edit();
       editor.putString("username", ApplicationInstance.getInstance().getLoginName());
       editor.putString("password", ApplicationInstance.getInstance().getPassword());
       editor.commit();
   }
	
   private void establishXMPP(){
		try {
		  if (!StreamXMPP.getInstance().isConnected()){	
			  StreamXMPP.getInstance().login(ApplicationInstance.APPID + ApplicationInstance.getInstance().getLoginName(), ApplicationInstance.getInstance().getPassword());
			  ApplicationXMPPListener.getInstance().addListenerForAllUsers();
	    	  ApplicationXMPPListener.getInstance().addFileReceiveListener();
		  }
		 } catch (Exception e) {
			Log.i("", e.getMessage());
		 }
	}
	
	private void showDialog(String message) {
		pd = ProgressDialog.show(this, "", message, true, true);
    }
	
	private void showAlertDialog(){		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
				.setMessage(userName + " is already a registered user, please change your user name")
				.setCancelable(false)
				.setNegativeButton("TRY AGAIN",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
	}
	
}
