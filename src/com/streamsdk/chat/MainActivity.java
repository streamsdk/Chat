package com.streamsdk.chat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaRecorder;
import android.os.Bundle;
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
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stream.api.JsonUtils;
import com.stream.api.StreamCallback;
import com.stream.api.StreamObject;
import com.stream.xmpp.PacketSendCallback;
import com.stream.xmpp.StreamXMPP;
import com.streamsdk.cache.ChatBackgroundDB;
import com.streamsdk.cache.FriendDB;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.cache.InvitationDB;
import com.streamsdk.cache.MessagingAckDB;
import com.streamsdk.cache.MessagingCountDB;
import com.streamsdk.cache.MessagingHistoryDB;
import com.streamsdk.cache.StatusDB;
import com.streamsdk.chat.domain.IM;
import com.streamsdk.chat.domain.SendMap;
import com.streamsdk.chat.emoji.EmojiEditText;
import com.streamsdk.chat.emoji.EmojiParser;
import com.streamsdk.chat.emoji.EmotionPagerAdapter;
import com.streamsdk.chat.handler.AudioHandler;
import com.streamsdk.chat.handler.ImageHandler;
import com.streamsdk.chat.settings.ChatSettingsDialog;
import com.streamsdk.search.SearchImageActivity;


public class MainActivity extends Activity implements EditTextEmojSelected, ChatListener, RefreshUI{

	private String receiver = "jacky";
	
    private boolean speak = true;
	ChatWindowAdapter adapter;
	List<IM> messages = new ArrayList<IM>();;
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
	private TableLayout moreOptions;
	private boolean isMoreOptionShown = true;
	private ImageView moreButtons;
	static final int REQUEST_IMAGE_CAPTURE = 0;
	static final int REQUEST_IMAGE_PICK = 1;
	static final int REQUEST_VIDEO_CAPTURE = 2;
	static final int SHARE_MAP = 3;
	static final int MAX_VIDEO_SIZE = 15000000;
	String thumbnailFileId = "";
	int downX, upX;

	@Override
    public void onPause()
    {
        super.onPause();
        ApplicationInstance.getInstance().setVisiable(false);
        ApplicationInstance.getInstance().setCurrentChatListener(null);
        ApplicationInstance.getInstance().setRefreshUI(null);
    }
	
	private void reiniDB(){
		 MessagingHistoryDB mdb = new MessagingHistoryDB(this);
		 FriendDB fdb = new FriendDB(this);
		 InvitationDB idb = new InvitationDB(this);
		 MessagingCountDB mcdb = new MessagingCountDB(this);
		 MessagingAckDB mackdb = new MessagingAckDB(this);
		 ChatBackgroundDB cdb = new ChatBackgroundDB(this);
		 StatusDB sdb = new StatusDB(this);
		 ApplicationInstance.getInstance().setChatBackgroundDB(cdb);
		 ApplicationInstance.getInstance().setMessagingHistoryDB(mdb);
		 ApplicationInstance.getInstance().setFriendDB(fdb);
		 ApplicationInstance.getInstance().setInivitationDB(idb);
		 ApplicationInstance.getInstance().setMessagingCountDB(mcdb);
		 ApplicationInstance.getInstance().setMessagingAckDB(mackdb);
		 ApplicationInstance.getInstance().setStatusDB(sdb);
	}
	
	protected void onResume(){
		super.onResume();
	    ApplicationInstance.getInstance().setVisiable(true);
		ApplicationInstance.getInstance().setCurrentChatListener(this);
		ApplicationInstance.getInstance().setRefreshUI(this);
		deleteCountHistory();
	    dismissMoreOptionPanel();
	    Object objectImageResource = ApplicationInstance.getInstance().getChatBackgroundDB().selectChatBackground(receiver);
	    ApplicationInstance.getInstance().setCurrentChatbackgroundReceiver(receiver);
	    if (objectImageResource != null){
	    	if (objectImageResource instanceof Integer){
	        	view.setBackgroundResource((Integer)objectImageResource);
	       }
	    }
	    updateData();
	}
	
	@Override
	protected  void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Activity activity = this;
		ApplicationInstance.getInstance().setRefreshUI(this);
	  
	    Intent intent = getIntent();
        receiver = intent.getExtras().getString("receiver");
      //  setTitle(receiver);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowCustomEnabled(true); 
        getActionBar().setCustomView(R.layout.window_title);
        TextView chatName = (TextView)findViewById(R.id.chatName);
        chatName.setText(receiver);
        TextView onlineInfo = (TextView)findViewById(R.id.onlineInfo);
        updateLastSeen(onlineInfo);
        DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		setContentView(R.layout.activity_main);
		view = (ListView)findViewById(R.id.listView1);
		view.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				  Log.i("clicked here", "main chat panel clicked");
	              dismissKeyboard();	
	              dismissMoreOptionPanel();
			}
		});
		
		adapter = new ChatWindowAdapter(messages, this, metrics);
		view.setAdapter(adapter);
		popUpView = getLayoutInflater().inflate(R.layout.emoticons_popup, null);
        parentLayout = (LinearLayout)findViewById(R.id.parentLayout);
        emoticonsCover = (LinearLayout) findViewById(R.id.footer_for_emoticons);
        moreOptions = (TableLayout)findViewById(R.id.footer_for_more);
        
        
       final ImageView emoticonsButton = (ImageView) findViewById(R.id.emoticons_button);
       moreButtons = (ImageView)findViewById(R.id.more_button);
       moreButtons.setOnClickListener(new OnClickListener() {
		   public void onClick(View v) {
			   if (isMoreOptionShown){
                   moreOptions.setVisibility(View.VISIBLE);
                   moreButtons.setImageResource(R.drawable.close128);
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
		        Map<String, String> metaData = ApplicationInstance.getInstance().getFriendMetadata(receiver);
		        if (metaData != null && metaData.containsKey(ApplicationInstance.TOEKN)){
		        	packetBody = addTokenToBody(metaData.get(ApplicationInstance.TOEKN), packetBody);
		        }
		        packet.setBody(packetBody);
		        ApplicationInstance.getInstance().getMessagingAckDB().insertTextMessage(im, body);
				StreamXMPP.getInstance().sendPacket(packet);
			 	adapter.notifyDataSetChanged();
			}
		});
		
		final ImageButton change = (ImageButton)findViewById(R.id.changeStyle);
		final Button speakButton = (Button)findViewById(R.id.speakButton);
		change.setOnClickListener(new OnClickListener() {		
			public void onClick(View arg0) {
			 	if (speak){
					change.setBackgroundResource(R.drawable.keyboard150);
					speakButton.setVisibility(View.VISIBLE);
					moreButtons.setVisibility(View.GONE);
					button.setVisibility(View.GONE);
					messageText.setVisibility(View.GONE);
					moreOptions.setVisibility(View.GONE);
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(messageText.getWindowToken(), 0);
					popupWindow.dismiss();
				}else{
					change.setBackgroundResource(R.drawable.microphonefat150);
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
		
		
		ImageView takeMap = (ImageView)findViewById(R.id.takemap_button);
		takeMap.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			     Intent intent = new Intent(activity, SendMapViewActivity.class);
			     startActivityForResult(intent, SHARE_MAP);
			}
		});
		
		ImageView search = (ImageView)findViewById(R.id.searchimage_button);
		search.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			    Intent intent = new Intent(activity, SearchImageActivity.class);
				startActivityForResult(intent,REQUEST_IMAGE_PICK);
			}
		});
		
		checkKeyboardHeight(parentLayout);
		setSpeakButtonAction(speakButton);
		//deleteCountHistory();
		readHistory();
		
		try{
		    enablePopUpView();
		}catch(Throwable t){
			Log.i("", "why null pointer throw here");
		}
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	private void updateLastSeen(final TextView tv){
		final StreamObject so = new StreamObject();
		so.loadStreamObject(receiver + "status", new StreamCallback() {
			public void result(boolean succeed, String errorMessage) {
				
					String lastSeen = (String)so.get("lastseen");
					String online = (String)so.get("online");
					String displayString = "";
					long lastSeenLong = 0;
					if (lastSeen == null || lastSeen.equals("")){
						Random rand = new Random(); 
						int pickedNumber = rand.nextInt(24 * 3600 * 1000); 
						lastSeenLong = System.currentTimeMillis() - pickedNumber; 
					}
					boolean isOnline = false;
					if (online != null && online.equals("YES")){
						displayString = "online";
						isOnline = true;
					}
				   if (lastSeenLong == 0)	
				      lastSeenLong = Long.parseLong(lastSeen);
				   if (lastSeen != null && lastSeen.length() == 10){
					   lastSeenLong = lastSeenLong * 1000;
					}
				   if (!isOnline){
				      SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mm");
				      Date resultdate = new Date(lastSeenLong);
				      displayString = sdf.format(resultdate);
				   }
				   if (!displayString.equals("")){
						final String displayStr = displayString;
						runOnUiThread(new Runnable(){
							public void run() {
								if (!displayStr.equals("online"))
								    tv.setText("last seen " + displayStr);
								else
								    tv.setText(displayStr);
							}
						});
					}
				
			}
		});
	}
	
	private String addTokenToBody(String token, String json){
		
		try {
			JSONObject jsonWithToken = new JSONObject(json);
			jsonWithToken.put(ApplicationInstance.TOEKN, token);
			return jsonWithToken.toString();
		} catch (JSONException e) {
	
		}
		return json;
	}
	
	private void deleteCountHistory(){
		MessagingCountDB mcb = ApplicationInstance.getInstance().getMessagingCountDB();
		if (mcb == null){
			Log.i("delete count null", "reiniti db");
			reiniDB();
		}
		Log.i("in delete count", "delete count");
		int res = ApplicationInstance.getInstance().getMessagingCountDB().delete(receiver);
		if (res > 0){
		   ApplicationInstance.getInstance().getRefreshUI().refresh();
		}
	}
	
	private void dismissKeyboard(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(messageText.getWindowToken(), 0);
		if (popupWindow != null)
		     popupWindow.dismiss();
	}
	
	private void readHistory(){
		MessagingHistoryDB mhdb = ApplicationInstance.getInstance().getMessagingHistoryDB();
		if (mhdb == null){
			Log.i("read history null", "returned");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
		Log.i("read history ok", "read history ok");
		try{
		   List<IM> imHistory = ApplicationInstance.getInstance().getMessagingHistoryDB().getIMHistoryForUser(receiver, ApplicationInstance.getInstance().getLoginName());
	       for (IM im : imHistory){
	          if (im.isImage() || im.isVideo() || im.isVoice())
	              receiveFile(im);
	          else
	              receiveMessage(im);
	        }
		}catch(Throwable t){
			//for some reason this is failed with null pointer
		}
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			 ApplicationInstance.getInstance().setCurrentChatListener(null);
			 onBackPressed();
			 return true;
		case 0:
		     showChatSettingDialog();
		default:
			 return super.onOptionsItemSelected(item);
		}
	}
	
	public void removeHistory(){
		 messages.clear();
		 updateData();
		 ApplicationInstance.getInstance().getMessagingHistoryDB().delete(receiver, ApplicationInstance.getInstance().getLoginName());
	}
	
	public void removeRecord(String chatTime){
		 ApplicationInstance.getInstance().getMessagingHistoryDB().deleteHistory(chatTime);
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
		case SHARE_MAP:
			 if (resultCode == RESULT_OK){
		          sendMapIM(ApplicationInstance.getInstance().getMapTaken());		 
			 }
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
   
    private void sendVideoIM(final String path){
    	dismissMoreOptionPanel();
    	if (isVideoSizeExceedTheMax(path)){
    		ApplicationInstance.getInstance().setRecordingVideoPath(null);
        	ApplicationInstance.getInstance().setPhotoTimeout(-1);
  	        Toast.makeText(getApplicationContext(), "Exceed the maximum allowed video size 15MB", Toast.LENGTH_LONG).show();
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
		im.setFrom(ApplicationInstance.getInstance().getLoginName());
    	im.setTo(receiver);
    	ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
		updateData();
		File file = new File(path);
		Map<String, Object> metaData = new HashMap<String, Object>();
		String type = im.isImage() ? "photo" : "video";
		metaData.put("from", im.getFrom());
		metaData.put("to", im.getTo());
		metaData.put("type", type);
		metaData.put("disappear", String.valueOf(im.isDisappear()));
		final byte bytes[] = !im.isDisappear() ? ImageCache.getInstance().getImageBytes(path) : null;
		Map<String, String> usermetaData = ApplicationInstance.getInstance().getFriendMetadata(receiver);
        final String token = usermetaData != null ? usermetaData.get(ApplicationInstance.TOEKN) : "";
		StreamXMPP.getInstance().sendFile(new PacketSendCallback() {
			public void sendData(Object object) {
				String tid = (String)object;
				if (tid != null && !tid.equals("")){
					im.setThumbNailId(tid);
				  try{
					ApplicationInstance.getInstance().getMessagingAckDB().updateVideoImageTid(im, tid);
				  }catch(Throwable t){}
				}
			}
		}, new StreamCallback() {
			public void result(boolean succeed, String errorMessage) {
				if (succeed){
					ApplicationInstance.getInstance().getMessagingAckDB().insertVideoImage(im, errorMessage);	
				}
			}
		}, null, file, metaData, ApplicationInstance.APPID + receiver, ApplicationInstance.getInstance().getLoginName(), type, timeout, chatTime, bytes, token);
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
    
    private void sendMapIM(SendMap sm){
    	
    	dismissMoreOptionPanel();
    	final IM im = ImageHandler.buildMapIMMessage(sm);
    	long chatTime = System.currentTimeMillis(); 
		im.setChatTime(chatTime);
		im.setFrom(ApplicationInstance.getInstance().getLoginName());
    	im.setTo(receiver);
    	messages.add(im);
    	ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
    	updateData();
    	ApplicationInstance.getInstance().setMapTaken(null);
    	byte imageButes[] = ImageCache.getInstance().getImageBytes(sm.getPath());
		Map<String, String> usermetaData = ApplicationInstance.getInstance().getFriendMetadata(receiver);
        String token = usermetaData != null ? usermetaData.get(ApplicationInstance.TOEKN) : "";
		if (imageButes != null){
		    Map<String, Object> metaData = new HashMap<String, Object>();
		    metaData.put("lat", sm.getLat());
			metaData.put("lon", sm.getLon());
			metaData.put("address", sm.getAddress());
			StreamXMPP.getInstance().sendBytes(new StreamCallback() {
			    public void result(boolean succeed, String errorMessage) {
				    if (succeed){
				    	Log.i("", "");
				    	ApplicationInstance.getInstance().getMessagingAckDB().insertMapMessage(im, errorMessage);
				    }
			    }
		     }, null, imageButes, metaData, ApplicationInstance.APPID + receiver, ApplicationInstance.getInstance().getLoginName(), "map", -1, chatTime, "", token);
		 }
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
		 	im.setFrom(ApplicationInstance.getInstance().getLoginName());
	    	im.setTo(receiver);
	    	ApplicationInstance.getInstance().getMessagingHistoryDB().insert(im);
	     	updateData();
			byte imageButes[] = ImageCache.getInstance().getImageBytes(path);
			Map<String, String> usermetaData = ApplicationInstance.getInstance().getFriendMetadata(receiver);
	        String token = usermetaData != null ? usermetaData.get(ApplicationInstance.TOEKN) : "";
			
			if (imageButes != null){
			    //int len = imageButes.length;
			    Map<String, Object> metaData = new HashMap<String, Object>();
			    String type = im.isImage() ? "photo" : "video";
			    metaData.put("from", im.getFrom());
				metaData.put("to", im.getTo());
				metaData.put("type", type);
				metaData.put("disappear", String.valueOf(im.isDisappear()));
			    StreamXMPP.getInstance().sendBytes(new StreamCallback() {
				    public void result(boolean succeed, String errorMessage) {
					    if (succeed){
					    	ApplicationInstance.getInstance().getMessagingAckDB().insertImageMessage(im, errorMessage);
					    }
				    }
			     }, null, imageButes, metaData, ApplicationInstance.APPID + receiver, ApplicationInstance.getInstance().getLoginName(), type, timeout, chatTime, "", token);
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
		    intent.putExtra("fromgallery", "true");
		    startActivityForResult(intent, REQUEST_IMAGE_PICK);
		}else{
		    Intent intent = new Intent(this, FullScreenImageActivity.class);
	        intent.putExtra("path", path);
	        intent.putExtra("send", "true");
	        intent.putExtra("fromgallery", "true");
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
				int x = (int) event.getX();
				switch (event.getAction() ) {
				   case MotionEvent.ACTION_DOWN: 
			    	    Log.i("", "");
			    	    downX  = x;
			    	    Log.i("x down", String.valueOf(x));
			    	    break;
				   case MotionEvent.ACTION_UP:
			    	    Log.i("", "");
			            upX = x;
			    	    Log.i("x up", String.valueOf(x));
			    	    if (upX < 0 || (upX - downX) < -150){
			    	    	Log.i("diff smaller", "stop recording");
			    	    	stopRecording();
			    	    }else{
			    	    	stopRecordingAndSend();
			    	    }
			    	    downX = 0;
			    	    upX = 0;
			    	    break;
			     }
				 return false;
			}
		});
		
	}
	
	private void stopRecordingAndSend(){
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
	     	
	     	metaData.put("from", voiceIm.getFrom());
			metaData.put("to", voiceIm.getTo());
			metaData.put("type", "voice");
			
	     	long chatTime = System.currentTimeMillis();
			voiceIm.setChatTime(chatTime);
			messages.add(voiceIm);
			updateData();
			currentRecordingFileName = "";
			Map<String, String> usermetaData = ApplicationInstance.getInstance().getFriendMetadata(receiver);
	        String token = usermetaData != null ? usermetaData.get(ApplicationInstance.TOEKN) : "";
			
	        StreamXMPP.getInstance().sendFile(null, new StreamCallback() {
				     public void result(boolean succeed, String errorMessage) {
						if (succeed) {
							ApplicationInstance.getInstance().getMessagingHistoryDB().insert(voiceIm);
							ApplicationInstance.getInstance().getMessagingAckDB().insertVoiceMessage(voiceIm, errorMessage);
						}
				}
			 }, null, file, metaData, ApplicationInstance.APPID + receiver,  ApplicationInstance.getInstance().getLoginName(), String.valueOf(rp.getTime()/1000), -1, chatTime, null, token);
		   }
	}
	
	private void stopRecording() {
	   
	   if (mRecorder != null){
     	   mRecorder.stop();
           mRecorder.release();
           mRecorder = null;
           dismissRecordingProgress();
           File file = new File(currentRecordingFileName);
           file.delete();
           currentRecordingFileName = "";
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
		 FragmentManager fm = getFragmentManager();
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
	
	public boolean onCreateOptionsMenu(Menu menu){
		 menu.add("Settings").setIcon(R.drawable.set).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 return true;
    }
	
	private void showChatSettingDialog(){
		
		FragmentManager fm = getFragmentManager();
		ChatSettingsDialog csd = new ChatSettingsDialog();
		csd.show(fm, "chatsettings");
	}
   
	@Override
	public void clicked(String hexString) {
		  
		String str = new String(Character.toChars(Integer.parseInt(hexString, 16)));
		int cursorPosition = messageText.getSelectionStart();		
		messageText.getText().insert(cursorPosition, str);
        
	}

}
