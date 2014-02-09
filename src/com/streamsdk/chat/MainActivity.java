package com.streamsdk.chat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.stream.api.JsonUtils;
import com.stream.api.StreamCallback;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.cache.FriendDB;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.cache.InvitationDB;
import com.streamsdk.cache.MessagingAckDB;
import com.streamsdk.cache.MessagingCountDB;
import com.streamsdk.cache.MessagingHistoryDB;
import com.streamsdk.chat.domain.IM;
import com.streamsdk.chat.emoji.EmojiEditText;
import com.streamsdk.chat.emoji.EmojiParser;
import com.streamsdk.chat.emoji.EmotionPagerAdapter;
import com.streamsdk.chat.handler.AudioHandler;
import com.streamsdk.chat.handler.ImageHandler;


public class MainActivity extends FragmentActivity implements EditTextEmojSelected, ChatListener, RefreshUI{

	private String receiver = "jacky";
	
    private boolean speak = true;
	ChatWindowAdapter adapter;
	List<IM> messages;
	ListView view;
	private MediaRecorder mRecorder = null;
	private String currentRecordingFileName = "";
	RecordingProgress rp;
	private PopupWindow popupWindow;
	private View popUpView;
	private LinearLayout parentLayout;
	private int keyboardHeight;
	private LinearLayout emoticonsCover;
	private boolean isKeyBoardVisible;
	private EmotionPagerAdapter eAdapter;
	private EmojiEditText messageText;
	private LinearLayout moreOptions;
	private boolean isMoreOptionShown = true;
	private ImageView moreButtons;
	static final int REQUEST_IMAGE_CAPTURE = 0;
	static final int REQUEST_IMAGE_PICK = 1;
	static final int REQUEST_VIDEO_CAPTURE = 2;
	static final int MAX_VIDEO_SIZE = 15000000;
	

	@Override
    public void onPause()
    {
        super.onPause();
        ApplicationInstance.getInstance().setVisiable(false);
    }
	
	private void reiniDB(){
		 MessagingHistoryDB mdb = new MessagingHistoryDB(this);
		 FriendDB fdb = new FriendDB(this);
		 InvitationDB idb = new InvitationDB(this);
		 MessagingCountDB mcdb = new MessagingCountDB(this);
		 MessagingAckDB mackdb = new MessagingAckDB(this);
		 ApplicationInstance.getInstance().setMessagingHistoryDB(mdb);
		 ApplicationInstance.getInstance().setFriendDB(fdb);
		 ApplicationInstance.getInstance().setInivitationDB(idb);
		 ApplicationInstance.getInstance().setMessagingCountDB(mcdb);
		 ApplicationInstance.getInstance().setMessagingAckDB(mackdb);
	}
	
	protected void onResume(){
		super.onResume();
	    ApplicationInstance.getInstance().setVisiable(true);
	    /*if (!StreamXMPP.getInstance().isConnected()){
	    	 new Thread(new ReconnectThread(this)).start();
		}*/
	}
	
	@Override
	protected  void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Activity activity = this;
		ApplicationInstance.getInstance().setCurrentChatListener(this);
	   
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    Intent intent = getIntent();
        receiver = intent.getExtras().getString("receiver");
      
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		setContentView(R.layout.activity_main);
		view = (ListView)findViewById(R.id.listView1);
		view.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				  Log.i("clicked here", "main chat panel clicked");
	               dismissKeyboard();				
			}
		});
		messages = new ArrayList<IM>();
		adapter = new ChatWindowAdapter(messages, this, metrics);
		view.setAdapter(adapter);
		popUpView = getLayoutInflater().inflate(R.layout.emoticons_popup, null);
        parentLayout = (LinearLayout)findViewById(R.id.parentLayout);
        emoticonsCover = (LinearLayout) findViewById(R.id.footer_for_emoticons);
        moreOptions = (LinearLayout)findViewById(R.id.footer_for_more);
        
        
       final ImageView emoticonsButton = (ImageView) findViewById(R.id.emoticons_button);
       moreButtons = (ImageView)findViewById(R.id.more_button);
       moreButtons.setOnClickListener(new OnClickListener() {
		   public void onClick(View v) {
			   if (isMoreOptionShown){
                   moreOptions.setVisibility(View.VISIBLE);
                   moreButtons.setImageResource(R.drawable.close);
                   popupWindow.dismiss();
			   }
			   else{
                   moreOptions.setVisibility(View.GONE);
                   moreButtons.setImageResource(R.drawable.plus256);
			   }
			   isMoreOptionShown = !isMoreOptionShown;
		   }
	   });
       
        
		final ImageView button = (ImageView)findViewById(R.id.btnSend);
		messageText = (EmojiEditText)findViewById(R.id.txtMessage);
		messageText.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
		messageText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
		        if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
		
		//this button is used for sending normal messaging
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				IM im = new IM();
				String mt = messageText.getText().toString();
				if (mt.equals("")){
					return;
				}
				messageText.setText("");
				String body = ParseMsgUtil.convertEditTextToParsableFormat(mt, getApplicationContext());
				String parsed = EmojiParser.getInstance(getApplicationContext()).parseEmoji(body);
				im.setChatMessage(parsed);
				im.setSelf(true);
				im.setFrom(ApplicationInstance.getInstance().getLoginName());
				im.setTo(receiver);
				long chatTime = System.currentTimeMillis();
				im.setChatTime(chatTime);
				ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
				messages.add(im);
				Message packet = new Message();
		        packet.setTo(ApplicationInstance.APPID + receiver + ApplicationInstance.HOST_PREFIX);
		        String packetBody = JsonUtils.buildPlainTextMessage(body, ApplicationInstance.getInstance().getLoginName(), String.valueOf(chatTime));
		        packet.setBody(packetBody);
		        ApplicationInstance.getInstance().getMessagingAckDB().insertTextMessage(im);
				StreamXMPP.getInstance().sendPacket(packet);
			 	adapter.notifyDataSetChanged();
			}
		});
		
		final ImageButton change = (ImageButton)findViewById(R.id.changeStyle);
		final Button speakButton = (Button)findViewById(R.id.speakButton);
		change.setOnClickListener(new OnClickListener() {		
			public void onClick(View arg0) {
			 	if (speak){
					change.setBackgroundResource(R.drawable.keyboard512);
					speakButton.setVisibility(View.VISIBLE);
					moreButtons.setVisibility(View.GONE);
					button.setVisibility(View.GONE);
					messageText.setVisibility(View.GONE);
					moreOptions.setVisibility(View.GONE);
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(messageText.getWindowToken(), 0);
					popupWindow.dismiss();
				}else{
					change.setBackgroundResource(R.drawable.microphonefat);
				    speakButton.setVisibility(View.GONE);
				    button.setVisibility(View.VISIBLE);
					messageText.setVisibility(View.VISIBLE);
					moreButtons.setVisibility(View.VISIBLE);
			        dismissMoreOptionPanel();
				}
			    speak = !speak;
			}
		});
		
		final float popUpheight = getResources().getDimension(R.dimen.keyboard_height);
		changeKeyboardHeight((int) popUpheight);
		
		emoticonsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			    setMoreOptionVisibility(View.GONE);
			    dismissMoreOptionPanel();
				if (!popupWindow.isShowing()) {
					popupWindow.setHeight((int) (keyboardHeight));
					if (isKeyBoardVisible) {
						emoticonsCover.setVisibility(LinearLayout.GONE);
					} else {
						emoticonsCover.setVisibility(LinearLayout.VISIBLE);
					}
					popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
				} else {
					popupWindow.dismiss();
				}
			}
		});
		
		
		ImageView pickPhoto = (ImageView)findViewById(R.id.pickpic_button);
		pickPhoto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(pickPhoto , REQUEST_IMAGE_PICK);
			}
		});
		
		ImageView takePic = (ImageView)findViewById(R.id.takepic_button);
		takePic.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				/*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			    	    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			    }*/
				Intent takePictureIntent = new Intent(activity, AndroidPhotoCapture.class);
		  	    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
		});
		
		ImageView takeVideo = (ImageView)findViewById(R.id.takevideo_button);
		takeVideo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent recordVideoIntent = new Intent(activity, AndroidVideoCapture.class);
				startActivityForResult(recordVideoIntent, REQUEST_VIDEO_CAPTURE);
			}
		});
		
		checkKeyboardHeight(parentLayout);
		setSpeakButtonAction(speakButton);
		deleteCountHistory();
		readHistory();
		
		try{
		    enablePopUpView();
		}catch(Throwable t){
			Log.i("", "why null pointer throw here");
		}
	}
	
	private void deleteCountHistory(){
		MessagingCountDB mcb = ApplicationInstance.getInstance().getMessagingCountDB();
		if (mcb == null){
			reiniDB();
		}
		int res = ApplicationInstance.getInstance().getMessagingCountDB().delete(receiver);
		if (res > 0)
		   ApplicationInstance.getInstance().getRefreshUI().refresh();
	}
	
	private void dismissKeyboard(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(messageText.getWindowToken(), 0);
		popupWindow.dismiss();
	}
	
	private void readHistory(){
		List<IM> imHistory = ApplicationInstance.getInstance().getMessagingHistoryDB().getIMHistoryForUser(receiver, ApplicationInstance.getInstance().getLoginName());
	    for (IM im : imHistory){
	        if (im.isImage() || im.isVideo() || im.isVoice())
	           receiveFile(im);
	        else
	           receiveMessage(im);
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
	
   protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		if (imageReturnedIntent == null && ApplicationInstance.getInstance().getRecordingVideoPath() != null){
			sendVideoIM(ApplicationInstance.getInstance().getRecordingVideoPath());
			return;
		}
		if (imageReturnedIntent == null && ApplicationInstance.getInstance().getPhotoTakenPath() != null){
			sendImageIM(ApplicationInstance.getInstance().getPhotoTakenPath());
			return;
		}
		switch(requestCode) {
		
		 case REQUEST_IMAGE_CAPTURE:
		      if(resultCode == RESULT_OK){        		    	  
		    	  sendImageIM(imageReturnedIntent); 
		       }
		       break; 
		case REQUEST_IMAGE_PICK:
		      if(resultCode == RESULT_OK){  
		    	  sendImageIM(imageReturnedIntent);
		       }
		       break;
		case REQUEST_VIDEO_CAPTURE:
		      if(resultCode == RESULT_OK){  
		    	  sendVideoIM(imageReturnedIntent);
		       }
		       break;
		}
	}

    private boolean isVideoSizeExceedTheMax(String path){
    	File file = new File(path);
    	Log.i("video size", String.valueOf(file.length()));
    	if (file.length() > MAX_VIDEO_SIZE){
    		return true;
    	}
    	return false;	
    	
    }
   
    private void sendVideoIM(String path){
    	dismissMoreOptionPanel();
    	if (isVideoSizeExceedTheMax(path)){
    		return;
    	}
    	final IM im = ImageHandler.buildImageIMMessage(path);
    	int timeout = ApplicationInstance.getInstance().getPhotoTimeout();
    	if (timeout != -1){
    		im.setDisappear(true);
    		im.setTimeout(String.valueOf(timeout));
    	}
    	ApplicationInstance.getInstance().setRecordingVideoPath(null);
    	ApplicationInstance.getInstance().setPhotoTimeout(-1);
    	final long chatTime = System.currentTimeMillis(); 
		im.setChatTime(chatTime);
		messages.add(im);
		updateData();
		File file = new File(path);
		Map<String, Object> metaData = new HashMap<String, Object>();
		String type = im.isImage() ? "photo" : "video";
		StreamXMPP.getInstance().sendFile(new StreamCallback() {
			public void result(boolean succeed, String errorMessage) {
				if (succeed){
					im.setFrom(ApplicationInstance.getInstance().getLoginName());
			    	im.setTo(receiver);
			    	ApplicationInstance.getInstance().getMessagingAckDB().insertVideoImage(im, errorMessage);
			    	ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
				}
			}
		}, null, file, metaData, ApplicationInstance.APPID + receiver, ApplicationInstance.getInstance().getLoginName(), type, timeout, chatTime);
    }
    
    private void sendVideoIM(Intent intent){
    	String path = intent.getStringExtra("path");
    	sendVideoIM(path);
    }
    
    private void dismissMoreOptionPanel(){
    	isMoreOptionShown = true;
		moreOptions.setVisibility(View.GONE);
		moreButtons.setImageResource(R.drawable.plus256);
    }
    
    private void sendImageIM(String path){
    	dismissMoreOptionPanel();
    	final IM im = ImageHandler.buildImageIMMessage(path);
		if (im.isVideo()){
			sendVideoIM(path);
		}else{
			ApplicationInstance.getInstance().setPhotoTakenPath(null);
			final int timeout = ApplicationInstance.getInstance().getPhotoTimeout();
			ApplicationInstance.getInstance().setPhotoTimeout(-1);
			long chatTime = System.currentTimeMillis(); 
			im.setChatTime(chatTime);
			if (timeout != -1){
	    		im.setDisappear(true);
	    		im.setTimeout(String.valueOf(timeout) + "s");
	    	}
			messages.add(im);
			updateData();
			byte imageButes[] = ImageCache.getInstance().getImageBytes(path);
			if (imageButes != null){
			    //int len = imageButes.length;
			    Map<String, Object> metaData = new HashMap<String, Object>();
			    String type = im.isImage() ? "photo" : "video";
			    StreamXMPP.getInstance().sendBytes(new StreamCallback() {
				    public void result(boolean succeed, String errorMessage) {
					    if (succeed){
					    	im.setFrom(ApplicationInstance.getInstance().getLoginName());
					    	im.setTo(receiver);
					    	ApplicationInstance.getInstance().getMessagingAckDB().insertImageMessage(im, errorMessage);
					    	ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
					    }
				    }
			     }, null, imageButes, metaData, ApplicationInstance.APPID + receiver, ApplicationInstance.getInstance().getLoginName(), type, timeout, chatTime);
			 }
		}
    }
   
	private void sendImageIM(Intent imageReturnedIntent) {
		String path = ImageHandler.getImgPath(imageReturnedIntent.getData(), this);
		if (path.endsWith(".mp4")){
			//sendImageIM(path);
		    Intent intent = new Intent(this, VideoFullScreen.class);
		    intent.putExtra("path", path);
		    intent.putExtra("send", "true");
		    startActivityForResult(intent, REQUEST_IMAGE_PICK);
		}else{
		    Intent intent = new Intent(this, FullScreenImageActivity.class);
	        intent.putExtra("path", path);
	        intent.putExtra("send", "true");
	        startActivityForResult(intent, REQUEST_IMAGE_PICK);
		}		
	}
	
	public String getReceiver(){
		return receiver;
	}
	
	
	private void setMoreOptionVisibility(int visiable){
		isMoreOptionShown = !isMoreOptionShown;
		moreOptions.setVisibility(visiable);
	}
	
	/**
	 * Checking keyboard height and keyboard visibility
	 */
	int previousHeightDiffrence = 0;
	private void checkKeyboardHeight(final View parentLayout) {

		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						
						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);
						int screenHeight = parentLayout.getRootView().getHeight();
						int heightDifference = screenHeight - (r.bottom);
						if (previousHeightDiffrence - heightDifference > 50) {							
							popupWindow.dismiss();
						}
						previousHeightDiffrence = heightDifference;
						if (heightDifference > 100) {
							isKeyBoardVisible = true;
							changeKeyboardHeight(heightDifference);

						} else {
							isKeyBoardVisible = false;
						}
					}
				});

	}
	
	private void changeKeyboardHeight(int height) {

		if (height > 100) {
			keyboardHeight = height;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, keyboardHeight);
			emoticonsCover.setLayoutParams(params);
		}
	}
	
	/**
	 * Defining all components of emoticons keyboard
	 */
	private void enablePopUpView() {

		ViewPager pager = (ViewPager) popUpView.findViewById(R.id.emoticons_pager);
		pager.setOffscreenPageLimit(3);
		
		ArrayList<String> paths = new ArrayList<String>();

		HashMap<String, ArrayList<String>> emos = EmojiParser.getInstance(getApplicationContext()).getEmoMap();
		ArrayList<String> people = emos.get("people");
		ArrayList<String> nature = emos.get("nature");
		ArrayList<String> objects = emos.get("objects");
		ArrayList<String> places = emos.get("places");
		ArrayList<String> symbols = emos.get("symbols");
		ArrayList<String> others = emos.get("others");
		
		paths.addAll(people);
		paths.addAll(nature);
		paths.addAll(objects);
		paths.addAll(places);
		paths.addAll(symbols);
		paths.addAll(others);
		
		eAdapter = new EmotionPagerAdapter(MainActivity.this, paths, this);
		pager.setAdapter(eAdapter);
		popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT, (int) keyboardHeight, false);
		popupWindow.setOnDismissListener(new OnDismissListener() {
			public void onDismiss() {
				emoticonsCover.setVisibility(LinearLayout.GONE);
			}
		});
	}
	
		
	private void setSpeakButtonAction(Button speakButton){
		
		speakButton.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View arg0) {
				startRecording();
				return false;
			}
		});
		
		speakButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction() ) { 
			       case MotionEvent.ACTION_DOWN: 
			    	    Log.i("", "");
			    	    break;
				 
			       case MotionEvent.ACTION_UP:
			    	    Log.i("", "");
			    	    stopRecording();
			            break;
				}
				return false;
			}
		});
		
	}
	
	private void stopRecording() {
	 if (mRecorder != null){
         
		 mRecorder.stop();
         mRecorder.release();
         mRecorder = null;
         dismissRecordingProgress();
         
         File file = new File(currentRecordingFileName);
         Map<String, Object> metaData = new HashMap<String, Object>();
         //start sending this as a file
     	final IM voiceIm = AudioHandler.buildIMMessage(currentRecordingFileName, rp.getTime()/1000);
     	voiceIm.setFrom(ApplicationInstance.getInstance().getLoginName());
     	voiceIm.setTo(receiver);
     	
     	long chatTime = System.currentTimeMillis();
		voiceIm.setChatTime(chatTime);
		messages.add(voiceIm);
		updateData();
		currentRecordingFileName = "";
	
         StreamXMPP.getInstance().sendFile(new StreamCallback() {
			     public void result(boolean succeed, String errorMessage) {
					if (succeed) {
						ApplicationInstance.getInstance().getMessagingHistoryDB().insert(voiceIm);
						ApplicationInstance.getInstance().getMessagingAckDB().insertVoiceMessage(voiceIm, errorMessage);
					}
			}
		 }, null, file, metaData, ApplicationInstance.APPID + receiver,  ApplicationInstance.getInstance().getLoginName(), String.valueOf(rp.getTime()/1000), -1, chatTime);
	   }
    }
	
	private void startRecording() {
		long currentTime = System.currentTimeMillis();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setAudioChannels(1);
        File file = new File(AudioHandler.getOutputFilePath(), String.valueOf(currentTime));
        try {
			file.createNewFile();
		} catch (IOException e1) {
		
		}
        currentRecordingFileName = file.getAbsolutePath();
        mRecorder.setOutputFile(currentRecordingFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("", "prepare() failed");
        }

        mRecorder.start();
        showRecordingProgress();
        
    }
	
	
	public String buildEmji(){
		
		String str = new String(Character.toChars(Integer.parseInt("1f60d", 16)));		
		return str + str + "fdsajfjsadk";
	}
	
	private void showRecordingProgress(){
		 FragmentManager fm = getSupportFragmentManager();
		 rp = new RecordingProgress();
		 rp.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		 rp.show(fm, "");
	}
	
	private void dismissRecordingProgress(){
		 rp.dismiss();
	}
	
	
	public void refresh(){
		List<IM> imHistory = ApplicationInstance.getInstance().getMessagingHistoryDB().getIMHistoryForUser(receiver, ApplicationInstance.getInstance().getLoginName());
		int currentSize = messages.size();
		int historySize = imHistory.size();
		if (historySize > currentSize){
			int offLineData = historySize - currentSize;
			int dataIndex = historySize - 1;
			while (offLineData!=0){
				IM im = imHistory.get(dataIndex);
				messages.add(im);
				updateData();
				offLineData--;
				dataIndex--;
			}
		}
	}
	
	private void updateData(){
		
		runOnUiThread(new Runnable(){
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	public void receiveMessage(IM im){
		messages.add(im);
		updateData();
	}
	
	public void receiveFile(IM im){
		messages.add(im);
		updateData();		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void clicked(String hexString) {
		  
		String str = new String(Character.toChars(Integer.parseInt(hexString, 16)));
		int cursorPosition = messageText.getSelectionStart();		
		messageText.getText().insert(cursorPosition, str);
        
	}

}
