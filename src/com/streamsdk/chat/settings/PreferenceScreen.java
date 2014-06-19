package com.streamsdk.chat.settings;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.stream.api.StreamCallback;
import com.stream.api.StreamFile;
import com.stream.api.StreamUser;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.cache.FileCache;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.cache.StatusDB;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.FirstPageActivity;
import com.streamsdk.chat.R;
import com.streamsdk.chat.handler.ImageHandler;
import com.streamsdk.util.BitmapUtils;
import com.streamsdk.util.UpdateUtils;

public class PreferenceScreen extends Activity{
	
	static final int REQUEST_IMAGE_PICK = 1;
	ImageView profileImageView;
	Activity activity;
	Button setStatus;
	LinearLayout userInfo;
	PopupWindow popupWindow;
	View popUpView;
	Map<String, String> userMetadata;
	Map<String, String> updatedMetadata;
	
	@Override
    public void onPause(){
		super.onPause();
		ApplicationInstance.getInstance().setVisiable(false);
    }
	
	protected void onResume(){
		super.onResume();
		ApplicationInstance.getInstance().setVisiable(true);
		if (setStatus != null){
			 setStatus.setText(ApplicationInstance.getInstance().getCurrentStatus());
		}
	}
	
	private void insertStatus(){
		 StatusDB sdb = ApplicationInstance.getInstance().getStatusDB();
		 sdb.insert("Hey there! I am using CoolChat.");
		 sdb.insert("Available");
		 sdb.insert("Busy");
		 sdb.insert("At school");
		 sdb.insert("At work");
		 sdb.insert("Sleeping");
		 SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
	     SharedPreferences.Editor editor = settings.edit();
	     editor.putString("status", "Hey there! I am using CoolChat.");
	     editor.commit();
	}
	
	private boolean shouldInsertStatus(){
		 SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
		 String inserted = settings.getString("status", "");
		 return inserted.equals("");
	}
	
	private String getCurrentStatus(){
		 SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
		 String status = settings.getString("status", "Hey there! I am using CoolChat.");
		 return status;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 userMetadata = ApplicationInstance.getInstance().getFriendMetadata(ApplicationInstance.getInstance().getLoginName());
		 updatedMetadata = new HashMap<String, String>();
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		 setContentView(R.layout.settings_layout);
		 activity = this;
		 
		 //basic user info
		 userInfo = (LinearLayout)findViewById(R.id.preBasicUserinfo);
		 TextView userView = (TextView)userInfo.findViewById(R.id.preUsername);
		 userView.setText(ApplicationInstance.getInstance().getLoginName());
		 profileImageView = (ImageView)userInfo.findViewById(R.id.preProfileImage);
		 profileImageView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(pickPhoto , REQUEST_IMAGE_PICK);	
			}
		});
		 Bitmap bitmap = ImageCache.getInstance().getPermImage(ApplicationInstance.getInstance().getLoginName());
		 if (bitmap != null)
			 profileImageView.setImageBitmap(bitmap);
		 else{
			Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.yahoo_no_avatar);
			profileImageView.setImageBitmap(bm);
		  }
		 
		 
		 setStatus = (Button)userInfo.findViewById(R.id.userStatus);
		 if (shouldInsertStatus()){
			 insertStatus();
		 }
         ApplicationInstance.getInstance().setCurrentStatus(getCurrentStatus());
		 setStatus.setText(ApplicationInstance.getInstance().getCurrentStatus());
		 setStatus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        Intent intent = new Intent(activity, StatusSettingActivity.class);
		        startActivity(intent);
			}
		 });
		 
		 
		//new profile setting page 
		RelativeLayout rl = (RelativeLayout)userInfo.findViewById(R.id.bloodTypePicker);
		rl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    showBloodTypeSetting();	
			}
		});
		
		RelativeLayout rHeight = (RelativeLayout)userInfo.findViewById(R.id.rHeight);
		rHeight.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    showHeightSetting();	
			}
		});
		
		RelativeLayout age = (RelativeLayout)userInfo.findViewById(R.id.agePicker);
		age.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    showAgeSetting();	
			}
		});
		
		RelativeLayout rBodyType = (RelativeLayout)userInfo.findViewById(R.id.bodyTypePicker);
		rBodyType.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    showBodyTypeSetting();	
			}
		});
		
		RelativeLayout rFasionType = (RelativeLayout)userInfo.findViewById(R.id.fasionTypePicker);
		rFasionType.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    showFashionTypeSetting();	
			}
		});
		
		RelativeLayout rDietType = (RelativeLayout)userInfo.findViewById(R.id.dietTypePicker);
		rDietType.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    showDietTypeSetting();	
			}
		});
		
		RelativeLayout rCharacterType = (RelativeLayout)userInfo.findViewById(R.id.characterTypePicker);
		rCharacterType.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    showCharacterTypeSetting();
			}
		});
		     
		 //invitation section
		 /*LinearLayout invitationLayout = (LinearLayout)findViewById(R.id.inviLayout);
		 Button inviSMS = (Button)invitationLayout.findViewById(R.id.inviSMSButton);
		 inviSMS.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
				 smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			     smsIntent.setType("vnd.android-dir/mms-sms");
			     String smsBody = "I am using CoolChat now. Download CoolChat from Apple App store or Google Play Store. My user name is " + ApplicationInstance.getInstance().getLoginName() + ". Add me as your friend";
			     smsIntent.putExtra("sms_body", smsBody);
			     startActivity(Intent.createChooser(smsIntent, "SMS:"));
			}
		 });
		 
		 Button inviEmButton = (Button)invitationLayout.findViewById(R.id.inviEmailButton);
		 inviEmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String body = "I am using CoolChat now. Download CoolChat from Apple App store or Google Play Store. My user name is " + ApplicationInstance.getInstance().getLoginName() + ". Add me as your friend";
				String subject =  "Invitation To CoolChat";   
				String uri="mailto:?subject=" + subject + "&body=" + body;
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri data = Uri.parse(uri);
				intent.setData(data);
				startActivity(intent);
			}
		});
		 
		
		 //TC section
		 LinearLayout tcLayout = (LinearLayout)findViewById(R.id.tcLayout);
		 Button tsButton = (Button)tcLayout.findViewById(R.id.tsButton);
		 tsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(activity, WebViewActivity.class);
			    intent.putExtra("url", "http://streamsdk.com/coolchat/termsofuse.html");
				startActivity(intent);
			}
		 });
		 
		
		 Button ppButton = (Button)findViewById(R.id.ppButton);
		 ppButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(activity, WebViewActivity.class);
			    intent.putExtra("url", "http://streamsdk.com/coolchat/privacypolicy.html");
				startActivity(intent);
			}
		 });*/
		
		 TextView logout = (TextView)findViewById(R.id.logout);
		 logout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		        alertDialogBuilder
						.setMessage("All your local data will be removed, are you sure to log out this account?")
						.setCancelable(false)
						.setPositiveButton("No",new DialogInterface.OnClickListener() {
					         public void onClick(DialogInterface dialog,int id) {
					        	 dialog.cancel();
							  }
				         })
						.setNegativeButton("Log Out",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
								UpdateUtils.updateOffline(ApplicationInstance.getInstance().getLoginName());
								SharedPreferences settings = getSharedPreferences(ApplicationInstance.USER_INFO, 0);
							    SharedPreferences.Editor editor = settings.edit();
							    editor.remove("username");
							    editor.remove("password");
							    editor.commit();
							    FileCache.getInstance().deleteAllFiles();
								ApplicationInstance.getInstance().getMessagingHistoryDB().deleteAll();
								ApplicationInstance.getInstance().getFriendDB().deleteAll();
								ApplicationInstance.getInstance().getChatBackgroundDB().deleteAll();
								ApplicationInstance.getInstance().getInivitationDB().deleteAll();
								ApplicationInstance.getInstance().getMessagingCountDB().deleteAll();
								Intent intent = new Intent(activity, FirstPageActivity.class);
								startActivity(intent);
								setResult(RESULT_OK);
								finish();
								ApplicationInstance.getInstance().logout();
								new Thread(new Runnable(){
									public void run(){
									   StreamXMPP.getInstance().disconnect();
									}
								}).start();
							}
						});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
			  }
		  });
	}
	
	private void reinitilize(){
		popUpView = getLayoutInflater().inflate(R.layout.numberpicker_layout, null);
		popUpView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);
        Button buttonCancel = (Button)popUpView.findViewById(R.id.numPickerButtonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			     popupWindow.dismiss();	
			}
	    });
	}
	
	private void showCharacterTypeSetting(){
	
		reinitilize();
		final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
        final TextView bTxt = (TextView)userInfo.findViewById(R.id.characterTypeTxt);
        np.setMinValue(0);
        np.setMaxValue(12);
        final String values[] = { "Gentle", "Pure", "Honest", "Optimistic", "Sociable", "Interesting", "Shy", "Cold", "Romantic", "Lonely", "Easy-going", "Decisive", "Stern"};
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(true);
		TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
		tv.setText("Select Type");
		popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
		   
        Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 String type = userMetadata.get(ApplicationInstance.CHARACTER_TYPE);
				 int indexValue = np.getValue();
				 String selectedValue = values[indexValue];
				 if (type == null || (!type.equals(selectedValue))){
					 updatedMetadata.put(ApplicationInstance.CHARACTER_TYPE, selectedValue);
					 bTxt.setText(selectedValue);
				 }
				 popupWindow.dismiss();
			}
		});
       
    }
	
	
	private void showDietTypeSetting(){
		
		    reinitilize();	
		    final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
	        final TextView bTxt = (TextView)userInfo.findViewById(R.id.dietTypeTxt);
	        np.setMinValue(0);
	        np.setMaxValue(3);
	        final String values[] = { "Vegetarian", "Healthy", "Meat and Potatoes", "Whatever"};
	        np.setDisplayedValues(values);
	        np.setWrapSelectorWheel(true);
			TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
			tv.setText("Select Diet");
			popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
			   
	        Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
	        buttonOK.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					 String type = userMetadata.get(ApplicationInstance.DIET);
					 int indexValue = np.getValue();
					 String selectedValue = values[indexValue];
					 if (type == null || (!type.equals(selectedValue))){
						 updatedMetadata.put(ApplicationInstance.DIET, selectedValue);
						 bTxt.setText(selectedValue);
					 }
					 popupWindow.dismiss();
				}
			});
	       
	}
	
	
	
	private void showFashionTypeSetting(){
	
		    reinitilize();
		    final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
	        final TextView bTxt = (TextView)userInfo.findViewById(R.id.fasionTypeTxt);
	        np.setMinValue(0);
	        np.setMaxValue(13);
	        final String values[] = { "Casual", "Street", "Model", "Beach", "Uniform", "Cute", "Sexy", "Gorgeous", "Goblin", "Wild", "Visual", "Mori", "Moe", "Akiba Style"};
	        np.setDisplayedValues(values);
	        np.setWrapSelectorWheel(true);
			TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
			tv.setText("Select Type");
			popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
			   
	        Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
	        buttonOK.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					 String type = userMetadata.get(ApplicationInstance.FASION_TYPE);
					 int indexValue = np.getValue();
					 String selectedValue = values[indexValue];
					 if (type == null || (!type.equals(selectedValue))){
						 updatedMetadata.put(ApplicationInstance.FASION_TYPE, selectedValue);
						 bTxt.setText(selectedValue);
					 }
					 popupWindow.dismiss();
				}
			});
	       
	}
	
	
   private void showBodyTypeSetting(){
		
	    reinitilize();
	    final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
        final TextView bTxt = (TextView)userInfo.findViewById(R.id.bodyTypeTxt);
        np.setMinValue(0);
        np.setMaxValue(8);
        final String values[] = { "Secret", "Bony", "Slim", "Average figure", "Galmourous", "Muscular", "Baby fat", "Chubby", "Plump"};
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(true);
		TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
		tv.setText("Select Type");
		popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
		   
        Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 String type = userMetadata.get(ApplicationInstance.BODY_TYPE);
				 int indexValue = np.getValue();
				 String selectedValue = values[indexValue];
				 if (type == null || (!type.equals(selectedValue))){
					 updatedMetadata.put(ApplicationInstance.BODY_TYPE, selectedValue);
					 bTxt.setText(selectedValue);
				 }
				 popupWindow.dismiss();
			}
		});
       
	}
	
	
	private void showAgeSetting(){
		
		reinitilize();
		final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
        final TextView bTxt = (TextView)userInfo.findViewById(R.id.ageTxt);
        np.setMinValue(0);
        np.setMaxValue(63);
        final String values[] = new String[64];
        for (int i=0; i < 64; i++){
        	values[i] = String.valueOf(i + 12);
        }
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(true);
		TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
		tv.setText("Select Age");
		popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
		
		Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
	        buttonOK.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					 String type = userMetadata.get(ApplicationInstance.AGE);
					 int indexValue = np.getValue();
					 String selectedValue = values[indexValue];
					 if (type == null || (!type.equals(selectedValue))){
						 updatedMetadata.put(ApplicationInstance.AGE, selectedValue);
						 bTxt.setText(selectedValue);
					 }
					 popupWindow.dismiss();
				}
			});
		
	}
	
    private void showHeightSetting(){
		
    	reinitilize();
    	final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
        final TextView bTxt = (TextView)userInfo.findViewById(R.id.txtHeight);
        np.setMinValue(0);
        np.setMaxValue(7);
        final String values[] = { "<155cm", "155~160cm", "161~165cm", "166~170cm", "171~175cm", "176~180cm", "181~185cm", ">185cm"};
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(true);
		TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
		tv.setText("Select Height");
		popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
		   
        Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 String type = userMetadata.get(ApplicationInstance.HEIGHT);
				 int indexValue = np.getValue();
				 String selectedValue = values[indexValue];
				 if (type == null || (!type.equals(selectedValue))){
					 updatedMetadata.put(ApplicationInstance.HEIGHT, selectedValue);
					 bTxt.setText(selectedValue);
				 }
				 popupWindow.dismiss();
			}
		});
    
    }
	
	
	private void showBloodTypeSetting(){
		
		reinitilize();
		final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
        final TextView bTxt = (TextView)userInfo.findViewById(R.id.txtBloodType);
        np.setMinValue(0);
        np.setMaxValue(4);
        final String values[] = { "A", "B", "O", "AB", "Other"};
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(true);
		TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
		tv.setText("Select Type");
		popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
		   
        Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 String type = userMetadata.get(ApplicationInstance.BLOOD_TYPE);
				 int indexValue = np.getValue();
				 String selectedValue = values[indexValue];
				 if (type == null || (!type.equals(selectedValue))){
					 updatedMetadata.put(ApplicationInstance.BLOOD_TYPE, selectedValue);
					 bTxt.setText(selectedValue);
				 }
				 popupWindow.dismiss();
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch(requestCode) {
		
		   case REQUEST_IMAGE_PICK:
		      if(resultCode == RESULT_OK){  
		    	  String path = ImageHandler.getImgPath(imageReturnedIntent.getData(), this);
		    	  Bitmap bitmap = BitmapUtils.loadImageForFullScreen(path, 230, 230, 300);
		    	  profileImageView.setImageBitmap(bitmap);
		    	  ImageCache.getInstance().addPermnent(ApplicationInstance.getInstance().getLoginName(), bitmap);
		    	  byte profileImageBytes[] = ImageCache.getInstance().getImagePem(ApplicationInstance.getInstance().getLoginName());
		    	  final StreamFile sf = new StreamFile();
		    	  sf.postBytes(profileImageBytes, new StreamCallback() {
					public void result(boolean succeed, String errorMessage) {
					      if (succeed){
					    	  StreamUser su = new StreamUser();
					    	  su.updateUserMetadata(ApplicationInstance.PROFILE_IMAGE, sf.getId());
					    	  su.updateUserMetadataInBackground(ApplicationInstance.getInstance().getLoginName());
					      }
					}
				});
		      }
		      break;
		}
		
	}

}
