package com.streamsdk.chat;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.stream.api.StreamCallback;
import com.stream.api.StreamFile;
import com.stream.api.StreamUser;
import com.streamsdk.cache.ChatBackgroundDB;
import com.streamsdk.cache.FileCache;
import com.streamsdk.cache.FriendDB;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.cache.InvitationDB;
import com.streamsdk.cache.MessagingAckDB;
import com.streamsdk.cache.MessagingCountDB;
import com.streamsdk.cache.MessagingHistoryDB;
import com.streamsdk.chat.addfriend.AddFriendMainActivity;
import com.streamsdk.chat.addfriend.UsersLoadThread;
import com.streamsdk.chat.domain.FriendRequest;
import com.streamsdk.chat.handler.MessageHistoryHandler;
import com.streamsdk.chat.settings.PreferenceScreen;
import com.streamsdk.header.NamesBaseAdaper;
import com.streamsdk.header.PinnedHeaderListView;
import com.streamsdk.xmpp.XMPPConnectionService;

public class MyFriendsActivity extends ListActivity implements RefreshUI{

	private static String [] names = {};
	
	static {
		//Arrays.sort(names);
	}

	//private NamesAdapter mAdapter;
	private NamesBaseAdaper mAdapter;
	private Activity activity;
	private TextView friendRequestNotification;
	
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
		 ApplicationInstance.getInstance().setChatBackgroundDB(cdb);
		 ApplicationInstance.getInstance().setMessagingHistoryDB(mdb);
		 ApplicationInstance.getInstance().setFriendDB(fdb);
		 ApplicationInstance.getInstance().setInivitationDB(idb);
		 ApplicationInstance.getInstance().setMessagingCountDB(mcdb);
		 ApplicationInstance.getInstance().setMessagingAckDB(mackdb);
	}
	
	protected void onResume(){
	   super.onResume();
	   ApplicationInstance.getInstance().setRefreshUI(this);
	   ApplicationInstance.getInstance().setVisiable(true);
	   FriendDB db = ApplicationInstance.getInstance().getFriendDB();
	   if (db == null){
		   reiniDB();
	   }
	   
	   
	   List<String> tempNames = ApplicationInstance.getInstance().getFriendDB().getFriends();
	   if (tempNames.size() != names.length){
		   names = ApplicationInstance.getInstance().getFriendDB().getFriendsArray();
		 //  mAdapter = new NamesAdapter(this, R.layout.list_item, android.R.id.text1, names);
		   mAdapter = new NamesBaseAdaper(activity, convertToLowerCase(names));
		   setListAdapter(mAdapter);
	   }
	   updateData();   
	   Set<String> nIds = ApplicationInstance.getInstance().getNotificationIds();
	   if (nIds.size() > 0){
		   for (String nid : nIds){
			   NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			   mNotificationManager.cancel(Integer.parseInt(nid));
		   }
	   }
	 
	   ImageCache.getInstance().removeAll();
	  
	   Log.i("", "");	
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		activity =  this;
		
		 FriendDB db = ApplicationInstance.getInstance().getFriendDB();
		 if (db == null){
			   reiniDB();
			   db =  ApplicationInstance.getInstance().getFriendDB();
		  }
	
		names = db.getFriendsArray();
		
		loadMetadataAndProfileImage(ApplicationInstance.getInstance().getLoginName());
		
		List<FriendRequest> frs = db.getFriendRequestList();
		for (FriendRequest fr : frs){
			loadMetadataAndProfileImage(fr.getFriendName());
		}
		
		ApplicationInstance.getInstance().setContext(getApplicationContext());
		
		new Thread(new MessageHistoryHandler(ApplicationInstance.getInstance().getLoginName(), getApplicationContext())).start();
		//new Thread(new ConnectionCheck(this)).start();
		new Thread(new UsersLoadThread()).start();
		
		setContentView(R.layout.mainpage_layout);
	    
		setTitle(ApplicationInstance.getInstance().getLoginName());
	   // mAdapter = new NamesAdapter(this, R.layout.list_item, android.R.id.text1, convertToLowerCase(names)); 
	    mAdapter = new NamesBaseAdaper(activity, convertToLowerCase(names));
	    friendRequestNotification = (TextView)findViewById(R.id.friendRequestNotifi);
	    friendRequestNotification.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				friendRequestNotification.setVisibility(View.GONE);
			}
		});
	    
	    setListAdapter(mAdapter);
	    setupListView();
	    
	    Intent intent = new Intent(this, XMPPConnectionService.class);
	    startService(intent);
	}
	
	private void loadMetadataAndProfileImage(String friendName){
		final StreamUser sUser = new StreamUser();
		sUser.loadUserMetadataInbackground(friendName, new StreamCallback() {
			public void result(boolean succeed, String errorMessage) {
				Map<String, String> userMetadata = sUser.getUserMetadata();
				ApplicationInstance.getInstance().updateFriendMetadata(sUser.getUserName(), userMetadata);
				String fileId = userMetadata.get(ApplicationInstance.PROFILE_IMAGE);
				if (fileId != null && !fileId.equals("")){
					boolean exists = FileCache.getInstance().generateProfileImagePathIfDoesNotExists(fileId);
					File profileImageFile;
					Bitmap bitmap;
					if (!exists){
					    profileImageFile = FileCache.getInstance().loadFile(fileId);
						StreamFile sf = new StreamFile();
						InputStream in = sf.getFileObject(fileId);
						FileCache.getInstance().writeFileToDisk(profileImageFile, in);
						bitmap = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
					}else{
						profileImageFile = FileCache.getInstance().loadFile(fileId);
					}
					
				    bitmap = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
					
				    if (bitmap != null){
				      if (!sUser.getUserName().equals(ApplicationInstance.getInstance().getLoginName()))
						ImageCache.getInstance().putNew(sUser.getUserName(), bitmap);
				      else
				    	ImageCache.getInstance().addPermnent(ApplicationInstance.getInstance().getLoginName(), bitmap);
				        ApplicationInstance.getInstance().addUserProfile(sUser.getUserName(), fileId);
					}
				    
					refresh();	
				}
			}
		});
	}
	
	private String[] convertToLowerCase(String strings[]){
		String newString[] = new String[strings.length];
		int index = 0;
		for (String str : strings){
			newString[index] = str.toLowerCase();
		    index++;
		}
		return newString;
	}
	
	private void setupListView() {
        PinnedHeaderListView listView = (PinnedHeaderListView) findViewById(android.R.id.list);
	    listView.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_item_header, listView, false));
	    listView.setOnScrollListener(mAdapter);
	    listView.setDividerHeight(0);
	    listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				String name = (String)mAdapter.getItem(position);
				//TODO: may need convert the name to lower case?
				Intent intent = new Intent(activity, MainActivity.class);
	            intent.putExtra("receiver", name);    
	            startActivity(intent);
			}
	    });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	         ApplicationInstance.getInstance().setCurrentChatListener(null);  
	     }
	    return super.onKeyDown(keyCode, event);
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		String title = (String) item.getTitle();
		if (title.equals("Refresh")) {
			Intent intent = new Intent(activity, AddFriendMainActivity.class);
			startActivity(intent);
		}
		if (title.equals("Settings")) {
			Intent intent = new Intent(activity, PreferenceScreen.class);
			startActivityForResult(intent, ApplicationInstance.FINISH_ALL);
		}

		return super.onOptionsItemSelected(item);

	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch(requestCode) {
		 case  ApplicationInstance.FINISH_ALL:
		      if(resultCode == RESULT_OK){        		    	  
		    	  finish();
		       }
		       break; 
		}
	}
	
   public boolean onCreateOptionsMenu(Menu menu){
		 menu.add("Refresh").setIcon(R.drawable.addfri).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 menu.add("Settings").setIcon(R.drawable.set).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 return true;
   }
		
   
   private void updateData(){
		
		runOnUiThread(new Runnable(){
			public void run() {
			  mAdapter.notifyDataSetChanged();
			}
		});
   }
	
	 public void refresh() {
		 updateData();	     	 
	 }

}

