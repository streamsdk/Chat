package com.streamsdk.chat.settings;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;

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
import android.view.KeyEvent;
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
import com.stream.api.ThreadPoolService;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.cache.FileCache;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.cache.StatusDB;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.FirstPageActivity;
import com.streamsdk.chat.R;
import com.streamsdk.chat.RefreshUI;
import com.streamsdk.chat.handler.ImageHandler;
import com.streamsdk.util.BitmapUtils;
import com.streamsdk.util.UpdateUtils;

public class PreferenceScreen extends Activity implements RefreshUI{
	
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
		 getUserMetaData();
		 updatedMetadata = new HashMap<String, String>();
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		 setContentView(R.layout.settings_layout);
		 activity = this;
		
		 setUserInfo();
		 setStatus();
		 buildProfileImages();
		 setProfileImageListener();
		 editListener();
		 initiLabels();
		 logout();
		     
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
		
		
	}
	
	protected void getUserMetaData(){
		 userMetadata = ApplicationInstance.getInstance().getFriendMetadata(ApplicationInstance.getInstance().getLoginName());
	}
	
	protected void setUserInfo(){
		 //basic user info
		 userInfo = (LinearLayout)findViewById(R.id.preBasicUserinfo);
		 TextView userView = (TextView)userInfo.findViewById(R.id.preUsername);
		 userView.setText(ApplicationInstance.getInstance().getLoginName());
	}
	
	protected void logout(){
		
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
								ApplicationInstance.getInstance().resetInstance();
							}
						});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
			  }
		  });
		
		
	}
	
	protected void setStatus(){
		
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
		
	}
	
	
	protected void editListener(){
		
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
				
				RelativeLayout rHobbyType = (RelativeLayout)userInfo.findViewById(R.id.hobbyTypePicker);
				rHobbyType.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					    showHobbyListSetting();
					}
				});
				
				RelativeLayout rOccupationType = (RelativeLayout)userInfo.findViewById(R.id.occupationTypePicker);
				rOccupationType.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					    showOccupationSetting();
					}
				});
				
				RelativeLayout rEyeColorType = (RelativeLayout)userInfo.findViewById(R.id.eyeColorTypePicker);
				rEyeColorType.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					    showEyeColor();
					}
				});
				
				RelativeLayout smoking = (RelativeLayout)userInfo.findViewById(R.id.smokingPicker);
				smoking.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					    showSmokingSetting();
					}
				});
				
				RelativeLayout drinking = (RelativeLayout)userInfo.findViewById(R.id.drinkingPicker);
				drinking.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					     showDrinkingSetting();
					}
				});
				
				RelativeLayout eth = (RelativeLayout)userInfo.findViewById(R.id.ethnicityPicker);
				eth.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					     showETH();
					}
				});
				
	}
	
	public synchronized void updateUI(boolean add){
		LinearLayout profileImageLayout = (LinearLayout)userInfo.findViewById(R.id.profileImageViewLayout);
		 int count = profileImageLayout.getChildCount();
		 for (int i=0; i<count; i++){
			 ImageView view = (ImageView)profileImageLayout.getChildAt(i);
			 if (i != 0){
				 Bitmap bitmap = ImageCache.getInstance().getPermImage(ApplicationInstance.getInstance().getLoginName() + i);
				 if (bitmap != null){
					 view.setImageBitmap(bitmap);
				 }
			 }else{
				 Bitmap bitmap = ImageCache.getInstance().getPermImage(ApplicationInstance.getInstance().getLoginName());
				 if (bitmap != null){
					 view.setImageBitmap(bitmap);
				 }
			 }
		 }
		 if (add){
			 ImageView iv = crateImageView();
			 profileImageLayout.addView(iv);
			 setProfileImageListener();
		 }
		 profileImageLayout.invalidate();
	}
	
	
	public void refresh(){
		runOnUiThread(new Runnable(){
			public void run() {
			   updateUI(false);
			}
		});	
	}
	
	public void refreshAndAdd(){
		runOnUiThread(new Runnable(){
			public void run() {
			   updateUI(true);
			}
		});	
	}
	
	protected void buildProfileImages(){
		
		 String profileImages = ProfileImageUtils.getProfileImages(userMetadata);
		 LinearLayout profileImageLayout = (LinearLayout)userInfo.findViewById(R.id.profileImageViewLayout);
		 //profileImageLayout.removeAllViews();
		 if (profileImages != null && !profileImages.equals("")){
			 if (profileImages.contains("|")){
				String images[] = profileImages.split("\\|");
				for (int i=0; i < images.length; i++){
					Bitmap bitmap = null;
					if (i == 0){
						bitmap = ImageCache.getInstance().getPermImage(ApplicationInstance.getInstance().getLoginName());
				    }else{
						bitmap = ImageCache.getInstance().getPermImage(ApplicationInstance.getInstance().getLoginName() + i);
					}
					if (bitmap != null){
						ImageView iv = crateImageView();
				        iv.setImageBitmap(bitmap);
				        profileImageLayout.addView(iv);
					}else{
						ImageView iv = crateImageView();
					    profileImageLayout.addView(iv);
					    if (i != 0){
					       DownloadProfileImageThread dpt = new DownloadProfileImageThread(this, images[i], i, ApplicationInstance.getInstance().getLoginName());
					       ThreadPoolService.getInstance().submitTask(dpt);
					   }
					}
				}
				ImageView iv = crateImageView();
			    profileImageLayout.addView(iv);
			 }else{
				Bitmap bitmap = ImageCache.getInstance().getPermImage(ApplicationInstance.getInstance().getLoginName());
		        ImageView iv = crateImageView();
		        iv.setImageBitmap(bitmap);
		        ImageView iv1 = crateImageView();
		        profileImageLayout.addView(iv);
		        profileImageLayout.addView(iv1);
		     }
		 }else{
			 ImageView iv = crateImageView();
		     profileImageLayout.addView(iv);
		 }
		 profileImageLayout.invalidate();
	}
	
	protected void setProfileImageListener(){
		
		LinearLayout profileImageLayout = (LinearLayout)userInfo.findViewById(R.id.profileImageViewLayout);
		int count = profileImageLayout.getChildCount();
		for (int i=0; i < count; i++){
			View v = profileImageLayout.getChildAt(i);
			v.setOnClickListener(null);
		}
		View v = profileImageLayout.getChildAt(count - 1);
		v.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					 Log.i("", "");
					 Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					 startActivityForResult(pickPhoto , REQUEST_IMAGE_PICK);	
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
	
	protected ImageView crateImageView(){
         LinearLayout.LayoutParams imagePrams = new LinearLayout.LayoutParams(300, 300);
	     ImageView iv = new ImageView(this);
	     iv.setLayoutParams(imagePrams);
	     iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
	     iv.setPadding(40, 0, 0, 0);
	     iv.setImageDrawable(getResources().getDrawable(R.drawable.yahoo_no_avatar));
	     return iv;
	}
	
	protected void initiLabels(){
		TextView bTxt = (TextView)userInfo.findViewById(R.id.characterTypeTxt);
        String type = userMetadata.get(ApplicationInstance.CHARACTER_TYPE);
        if (type != null){
        	bTxt.setText(type);
        }
        
        TextView bTxt1 = (TextView)userInfo.findViewById(R.id.dietTypeTxt);
		String type1 = userMetadata.get(ApplicationInstance.DIET);
	    if (type1 != null){
        	bTxt1.setText(type1);
        }
	    
	    TextView bTxt2 = (TextView)userInfo.findViewById(R.id.fasionTypeTxt);
		String type2 = userMetadata.get(ApplicationInstance.FASION_TYPE);
		if (type2 != null){
        	bTxt2.setText(type2);
        }
		
		TextView bTxt3 = (TextView)userInfo.findViewById(R.id.bodyTypeTxt);
	    String type3 = userMetadata.get(ApplicationInstance.BODY_TYPE);
		if (type3 != null){
	         bTxt3.setText(type3);
	    }
		 
		TextView bTxt4 = (TextView)userInfo.findViewById(R.id.ageTxt);
		String type4 = userMetadata.get(ApplicationInstance.AGE);
		if (type4 != null){
	         bTxt4.setText(type4);
	     }
		 
		TextView bTxt5 = (TextView)userInfo.findViewById(R.id.txtHeight);
		String type5 = userMetadata.get(ApplicationInstance.HEIGHT);
		if (type5 != null){
	        bTxt5.setText(type5);
	     }
		 
		TextView bTxt6 = (TextView)userInfo.findViewById(R.id.txtBloodType);
		String type6 = userMetadata.get(ApplicationInstance.BLOOD_TYPE);
		if (type6 != null){
	        bTxt6.setText(type6);
		}
		
		TextView bTxt7 = (TextView)userInfo.findViewById(R.id.hobbyTypeTxt);
		String type7 = userMetadata.get(ApplicationInstance.HOBBY_TYPE);
		if (type7 != null){
	        bTxt7.setText(type7);
		}
		
		TextView bTxt8 = (TextView)userInfo.findViewById(R.id.occupationTypeTxt);
		String type8 = userMetadata.get(ApplicationInstance.OCCUPATION_TYPE);
		if (type8 != null){
	        bTxt8.setText(type8);
		}
		
		TextView bTxt9 = (TextView)userInfo.findViewById(R.id.eyeColorTypeTxt);
		String type9 = userMetadata.get(ApplicationInstance.EYE_COLOR_TYPE);
		if (type9 != null){
	        bTxt9.setText(type9);
		}
		
		TextView bTxt10 = (TextView)userInfo.findViewById(R.id.smokingTxt);
		String type10 = userMetadata.get(ApplicationInstance.SMOKING);
		if (type10 != null){
	        bTxt10.setText(type10);
		}
		
		TextView bTxt11 = (TextView)userInfo.findViewById(R.id.drinkingTxt);
		String type11 = userMetadata.get(ApplicationInstance.DRINKING);
		if (type11 != null){
	        bTxt11.setText(type11);
		}
		
		TextView bTxt12 = (TextView)userInfo.findViewById(R.id.ethnicityTxt);
		String type12 = userMetadata.get(ApplicationInstance.ETH);
		if (type12 != null){
	        bTxt12.setText(type12);
		}
		
	}
	
	private void showETH(){
		
		reinitilize();
		final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
		final TextView bTxt = (TextView)userInfo.findViewById(R.id.ethnicityTxt);
        final String type = userMetadata.get(ApplicationInstance.ETH);
        final String values[] = {"Asian", "Black/African", "Latino/Hispanic", "Indian", "Middle Easter", "Native American", "Pacific Islander", "White/Caucasian", "Mixed", "Other"};
        np.setMinValue(0);
        np.setMaxValue(9);
    	np.setDisplayedValues(values);
	    np.setWrapSelectorWheel(true);
	    TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
	    tv.setText("Select Ethnicity");
	    popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
	    Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
	    buttonOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 int indexValue = np.getValue();
				 String selectedValue = values[indexValue];
				 if (type == null || (!type.equals(selectedValue))){
					 updatedMetadata.put(ApplicationInstance.ETH, selectedValue);
					 bTxt.setText(selectedValue);
				 }
				 popupWindow.dismiss();
			}
		});
		
	}
	
	private void showDrinkingSetting(){
		
		reinitilize();
		final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
		final TextView bTxt = (TextView)userInfo.findViewById(R.id.drinkingTxt);
        final String type = userMetadata.get(ApplicationInstance.DRINKING);
        final String values[] = {"No", "Sometimes", "Yes"};
        np.setMinValue(0);
        np.setMaxValue(2);
    	np.setDisplayedValues(values);
	    np.setWrapSelectorWheel(true);
	    TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
	    tv.setText("Drinking?");
	    popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
	    Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
	    buttonOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 int indexValue = np.getValue();
				 String selectedValue = values[indexValue];
				 if (type == null || (!type.equals(selectedValue))){
					 updatedMetadata.put(ApplicationInstance.DRINKING, selectedValue);
					 bTxt.setText(selectedValue);
				 }
				 popupWindow.dismiss();
			}
		});
	}
	
	private void showSmokingSetting(){
	
		reinitilize();
		final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
		final TextView bTxt = (TextView)userInfo.findViewById(R.id.smokingTxt);
        final String type = userMetadata.get(ApplicationInstance.OCCUPATION_TYPE);
        final String values[] = {"No", "Sometimes", "Yes"};
        np.setMinValue(0);
        np.setMaxValue(2);
    	np.setDisplayedValues(values);
	    np.setWrapSelectorWheel(true);
	    TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
	    tv.setText("Smoking?");
	    popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
	    Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
	    buttonOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 int indexValue = np.getValue();
				 String selectedValue = values[indexValue];
				 if (type == null || (!type.equals(selectedValue))){
					 updatedMetadata.put(ApplicationInstance.SMOKING, selectedValue);
					 bTxt.setText(selectedValue);
				 }
				 popupWindow.dismiss();
			}
		});
	    
	}
	
	private void showOccupationSetting(){
		
		reinitilize();
		final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
		final TextView bTxt = (TextView)userInfo.findViewById(R.id.occupationTypeTxt);
        final String type = userMetadata.get(ApplicationInstance.OCCUPATION_TYPE);
        np.setMinValue(0);
        np.setMaxValue(10);
        final String values[] = {"Clerk", "Technology", "Business", "Sales", "Service", "Teacher", "Self-employed", "Student", "Housewife", "Management", "Other"};
    	np.setDisplayedValues(values);
	    np.setWrapSelectorWheel(true);
	    TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
	    tv.setText("Select Occupation");
	    popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
	    Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
	    buttonOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 int indexValue = np.getValue();
				 String selectedValue = values[indexValue];
				 if (type == null || (!type.equals(selectedValue))){
					 updatedMetadata.put(ApplicationInstance.OCCUPATION_TYPE, selectedValue);
					 bTxt.setText(selectedValue);
				 }
				 popupWindow.dismiss();
			}
		});
   
   }
	
	private void showEyeColor(){
		
		reinitilize();
		final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
		final TextView bTxt = (TextView)userInfo.findViewById(R.id.eyeColorTypeTxt);
		final String type = userMetadata.get(ApplicationInstance.EYE_COLOR_TYPE);
		np.setMinValue(0);
        np.setMaxValue(7);
       final String values[] = {"Amber", "Black", "Blue", "Brown", "Gray", "Green", "Hazel", "Other"};
		np.setDisplayedValues(values);
	    np.setWrapSelectorWheel(true);
	    TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
	    tv.setText("Select Eye Color");
	    popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
	    Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
	    buttonOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 int indexValue = np.getValue();
				 String selectedValue = values[indexValue];
				 if (type == null || (!type.equals(selectedValue))){
					 updatedMetadata.put(ApplicationInstance.EYE_COLOR_TYPE, selectedValue);
					 bTxt.setText(selectedValue);
				 }
				 popupWindow.dismiss();
			}
		});
	}
	
	private void showHobbyListSetting(){
		
		reinitilize();
		final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
        final TextView bTxt = (TextView)userInfo.findViewById(R.id.hobbyTypeTxt);
        final String type = userMetadata.get(ApplicationInstance.HOBBY_TYPE);
        np.setMinValue(0);
        np.setMaxValue(10);
		final String values[] = {"Movies", "Doing Sports", "Music", "Karaoke", "Cooking", "Drinking", "Shopping", "Travel", "Art", "Reading", "Games"};
		np.setDisplayedValues(values);
	    np.setWrapSelectorWheel(true);
	    TextView tv = (TextView)popUpView.findViewById(R.id.numPickerText);
	    tv.setText("Select Hobby");
	    popupWindow.showAtLocation(userInfo, Gravity.BOTTOM, 0, 0);
	    Button buttonOK = (Button)popUpView.findViewById(R.id.numPickerButtonOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 int indexValue = np.getValue();
				 String selectedValue = values[indexValue];
				 if (type == null || (!type.equals(selectedValue))){
					 updatedMetadata.put(ApplicationInstance.HOBBY_TYPE, selectedValue);
					 bTxt.setText(selectedValue);
				 }
				 popupWindow.dismiss();
			}
		});
	    
	}
	
	private void showCharacterTypeSetting(){
	
		reinitilize();
		final NumberPicker np = (NumberPicker)popUpView.findViewById(R.id.numPicker);
        final TextView bTxt = (TextView)userInfo.findViewById(R.id.characterTypeTxt);
        final String type = userMetadata.get(ApplicationInstance.CHARACTER_TYPE);
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
			final String type = userMetadata.get(ApplicationInstance.DIET);
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
			final String type = userMetadata.get(ApplicationInstance.FASION_TYPE);
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
		final String type = userMetadata.get(ApplicationInstance.BODY_TYPE);
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
		final String type = userMetadata.get(ApplicationInstance.AGE);
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
		final String type = userMetadata.get(ApplicationInstance.HEIGHT);
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
		final  String type = userMetadata.get(ApplicationInstance.BLOOD_TYPE);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        	updateUserMetadata();
          }
	    return super.onKeyDown(keyCode, event);
	}

	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        case android.R.id.home:
        	updateUserMetadata();
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
      }
    }
	
	private void updateUserMetadata(){
		
		if (updatedMetadata.size() > 0){
			StreamUser user = new StreamUser();
			Set<String> keys = updatedMetadata.keySet();
			for (String key :  keys){
				user.updateUserMetadata(key, updatedMetadata.get(key));
			} 
			user.updateUserMetadataInBackground(ApplicationInstance.getInstance().getLoginName());
			Map<String, String> metadata = ApplicationInstance.getInstance().getFriendMetadata(ApplicationInstance.getInstance().getLoginName());
			metadata.putAll(updatedMetadata);
			ApplicationInstance.getInstance().updateFriendMetadata(ApplicationInstance.getInstance().getLoginName(), metadata);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch(requestCode) {
		
		   case REQUEST_IMAGE_PICK:
		      if(resultCode == RESULT_OK){
		    	
		    	  String path = ImageHandler.getImgPath(imageReturnedIntent.getData(), this);
		    	  final int imageIndex = currentImageIndex();
		    	  Bitmap bitmap = BitmapUtils.loadImageForFullScreen(path, 230, 230, 300);
		    	  byte profileImageBytes[] = null;
		    	  if (imageIndex != 0){
		    	     ImageCache.getInstance().addPermnent(ApplicationInstance.getInstance().getLoginName() + imageIndex, bitmap);
		    	     profileImageBytes = ImageCache.getInstance().getImagePem(ApplicationInstance.getInstance().getLoginName() + imageIndex);
		    	  }else{
		    		 ImageCache.getInstance().addPermnent(ApplicationInstance.getInstance().getLoginName(), bitmap);
		    		 profileImageBytes = ImageCache.getInstance().getImagePem(ApplicationInstance.getInstance().getLoginName());
				  }
		    	  refreshAndAdd();
		    	  
		    	  final StreamFile sf = new StreamFile();
		    	  sf.postBytes(profileImageBytes, new StreamCallback() {
					public void result(boolean succeed, String errorMessage) {
					      if (succeed){
					    	  StreamUser su = new StreamUser();
					    	  String profileImages = recreateImageIndex(sf.getId());
					    	  su.updateUserMetadata(ApplicationInstance.NEW_PROFILE_IMAGE, profileImages);
					    	  su.updateUserMetadataInBackground(ApplicationInstance.getInstance().getLoginName());
					    	  //update in memeory
					    	  userMetadata.put(ApplicationInstance.NEW_PROFILE_IMAGE, profileImages);
					    	  ApplicationInstance.getInstance().updateFriendMetadata(ApplicationInstance.getInstance().getLoginName(), userMetadata);
					    	  
					      }
					 }
				  });
		      }
		      break;
		}
		
	}
	
	private String recreateImageIndex(String fileId){
		  String profileImages = ProfileImageUtils.getProfileImages(userMetadata);
    	  if (profileImages != null && !profileImages.equals("")){
    		  profileImages = profileImages + "|" + fileId;
    	      return profileImages;
    	  }
    	  return fileId;
	}
	
	private int currentImageIndex(){
		  String profileImages = ProfileImageUtils.getProfileImages(userMetadata);
    	  if (profileImages != null && !profileImages.equals("")){
    		  if (profileImages.contains("|")){
    			  String pImage[] = profileImages.split("\\|");
    			  return pImage.length;    			  
    		  }else{
    			  return 1;
    		  }
    	  }
    	  return 0;
	}

}
