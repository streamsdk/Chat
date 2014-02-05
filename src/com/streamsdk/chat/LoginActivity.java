package com.streamsdk.chat;

import java.util.ArrayList;
import java.util.List;

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

import com.stream.api.QueryResultsCallback;
import com.stream.api.StreamCallback;
import com.stream.api.StreamObject;
import com.stream.api.StreamQuery;
import com.stream.api.StreamUser;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.chat.domain.FriendRequest;
import com.streamsdk.xmpp.ApplicationXMPPListener;

public class LoginActivity extends Activity{
	
	ProgressDialog pd;
	
	private void establishXMPP(){
		
		try {
			
		  if (!StreamXMPP.getInstance().isConnected()){	
			 StreamXMPP.getInstance().login(ApplicationInstance.APPID + ApplicationInstance.getInstance().getLoginName(), ApplicationInstance.getInstance().getPassword());
			 SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
		     SharedPreferences.Editor editor = settings.edit();
             editor.putString("username", ApplicationInstance.getInstance().getLoginName());
             editor.putString("password", ApplicationInstance.getInstance().getPassword());
             editor.commit();
		      
			ApplicationXMPPListener.getInstance().addListenerForAllUsers();
	    	ApplicationXMPPListener.getInstance().addFileReceiveListener();
		  }
		} catch (Exception e) {
			Log.i("", e.getMessage());
		}
		
	}
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    final Activity activity = this;
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    setContentView(R.layout.loginactivity_layout); 
	    
	    LinearLayout ll = (LinearLayout)findViewById(R.id.loginLayout);
	    final EditText loginText = (EditText)ll.findViewById(R.id.loginUserName);
	    loginText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	        @Override
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
	    
	    final EditText passwordText = (EditText)ll.findViewById(R.id.loginPassword);
	    
	    TextView enter = (TextView)ll.findViewById(R.id.loginButtonTextView);
	
	    enter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog("Log you in, please wait...");
					
				final String userName = loginText.getText().toString();
				final String password = passwordText.getText().toString();
				StreamUser su = new StreamUser();
				su.login(userName, password, new StreamCallback() {
					public void result(boolean succeed, String errorMessage) {
						if (succeed) {
							
							ApplicationInstance.getInstance().setLoginName(userName);
							ApplicationInstance.getInstance().setPassword(password);
							establishXMPP();
							
							StreamQuery sq = new StreamQuery(ApplicationInstance.getInstance().getLoginName());
							sq.setQueryLogicAnd(false);
							sq.whereEqualsTo("status", "friend");
							sq.whereEqualsTo("status", "request");
							sq.findInBackground(new QueryResultsCallback() {
								public void results(List<StreamObject> streamObjects) {
									List<FriendRequest> frs = new ArrayList<FriendRequest>();
									for (StreamObject so : streamObjects) {
										FriendRequest fr = new FriendRequest();
										String status = (String) so.get("status");
										if (status != null) {
											fr.setFriendName(so.getId());
											fr.setStatus(status);
										}
										frs.add(fr);
									}

									ApplicationInstance.getInstance().getFriendDB().syncUpdate(frs);
									pd.dismiss();
									Intent intent = new Intent(activity,MyFriendsActivity.class);
									startActivity(intent);
									finish();
									ApplicationInstance.getInstance().getFirstPageActivity().finish();
								}
							});
						}else{
							pd.dismiss();
							handler.sendEmptyMessage(0);
						}
					}
				});
			
			}
			
		});
	}
	
	private void showAlertDialog(){		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
				.setMessage("Login failed, Invalid username or password")
				.setCancelable(false)
				.setNegativeButton("TRY AGAIN",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
	}
	
	private void showDialog(String message) {
			pd = ProgressDialog.show(this, "", message, true, true);
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

}
