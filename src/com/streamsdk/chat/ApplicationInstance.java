package com.streamsdk.chat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.streamsdk.cache.ChatBackgroundDB;
import com.streamsdk.cache.FriendDB;
import com.streamsdk.cache.InvitationDB;
import com.streamsdk.cache.MessagingAckDB;
import com.streamsdk.cache.MessagingCountDB;
import com.streamsdk.cache.MessagingHistoryDB;

public class ApplicationInstance {
	
	//public  static final String APPID = "0093D2FD61600099DE1027E50C6C3F8D";
	//public  static final String APPID = "7E95CF60694890DCD4CEFBF79BC3BAE4";
	
	/*public static String APPID="0093D2FD61600099DE1027E50C6C3F8D";
	public static String cKey = "01D901D6EFBA42145E54F52E465F407B";
	public static String sKey = "4EF482C15D849D04BA5D7BC940526EA3";*/
	public static final String messageHistory = "messaginghistory";
	
	/*private static String APPID="C06B58F6700462E5597E94723D7B105C";
	private static String cKey = "2EC9482AF170B7B3A1558F7EC7C01C7A";
	private static String sKey = "78771227A606BFD96797E6281D518614";*/
	
	public static String APPID="7E95CF60694890DCD4CEFBF79BC3BAE4";
	public static String cKey = "4768674EDC06477EC63AEEF8FEAB0CF8";
	public static String sKey = "73B7C757A511B1574FDF63B3FEB638B7";
	public static String HOST_PREFIX = "@streamsdk.com";
	public static final String USER_INFO = "MyPrefsFile";
    public static final String PROFILE_IMAGE = "profileImageId";
    public static final String TOEKN = "token";
    public static final int FINISH_ALL= 1515;
	
	private static ApplicationInstance applicationInstance = null;
	private ChatListener chatListener =  null;
	private Map<String, Handler> handlers = null;
	private String loginName = "";
	private String password = "";
	private boolean visiable = false;
	private Context context;
	private Activity firstPageActivity;
	private String recordingVideoPath;
	private String photoTakenPath;
	
	private MessagingCountDB messagingCountDB;
	private MessagingAckDB messagingAckDB;
	private ChatBackgroundDB chatBackgroundDB;
	private MessagingHistoryDB messagingHistoryDB;
	private FriendDB friendDB;
	private InvitationDB inivitationDB; 
	
	private RefreshUI refreshUI;
	private int photoTimeout = -1;
	private Map<String, Map<String, String>> friendUserMetadata;
	private Set<String> notificationIds = new HashSet<String>();
	private long receiveStatusUpdatedTime = System.currentTimeMillis();
	private String currentChatbackgroundReceiver;
	
	
	public static ApplicationInstance getInstance(){
		
		if (applicationInstance == null){
			applicationInstance = new ApplicationInstance();
			return applicationInstance;
		}
		return applicationInstance;
	}
	
	public void logout(){
		loginName = "";
		password = "";
		visiable = false;
		context = null;
	    firstPageActivity = null;
		recordingVideoPath = null;
		photoTakenPath = null;
	}
	
	public synchronized void updateFriendMetadata(String id, Map<String, String> data){
		if (friendUserMetadata == null){
			friendUserMetadata = new HashMap<String, Map<String, String>>();
		}
		friendUserMetadata.put(id, data);
	}
	
	public synchronized Map<String, String> getFriendMetadata(String id){
	    if (friendUserMetadata != null)
	    	return friendUserMetadata.get(id);
	    return null;
	}
	
	public InvitationDB getInivitationDB() {
		return inivitationDB;
	}

	public void setInivitationDB(InvitationDB inivitationDB) {
		this.inivitationDB = inivitationDB;
	}

	public String getLoginName() {
		if (loginName != null)
			return loginName.toLowerCase();
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public void addHandler(String name, Handler handler){
		if (handlers == null){
			handlers = new HashMap<String, Handler>();
		}
		handlers.put(name, handler);
	}
	
	public Handler getHandler(String name){
		return handlers.get(name);
	}
	
	public FriendDB getFriendDB() {
		return friendDB;
	}

	public void setFriendDB(FriendDB friendDB) {
		this.friendDB = friendDB;
	}

	public MessagingHistoryDB getMessagingHistoryDB() {
		return messagingHistoryDB;
	}
	
	public boolean isVisiable() {
		return visiable;
	}

	public void setVisiable(boolean visiable) {
		this.visiable = visiable;
	}

	public void setMessagingHistoryDB(MessagingHistoryDB messagingHistoryDB) {
		this.messagingHistoryDB = messagingHistoryDB;
	}
	
	public void setCurrentChatListener(ChatListener cl){
		chatListener = cl;
	}
	
	public ChatListener getCurrentChatListener(){
		return chatListener;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	
	public Activity getFirstPageActivity() {
		return firstPageActivity;
	}

	public void setFirstPageActivity(Activity firstPageActivity) {
		this.firstPageActivity = firstPageActivity;
	}

	public String getRecordingVideoPath() {
		return recordingVideoPath;
	}

	public void setRecordingVideoPath(String recordingVideoPath) {
		this.recordingVideoPath = recordingVideoPath;
	}

	public String getPhotoTakenPath() {
		return photoTakenPath;
	}

	public void setPhotoTakenPath(String photoTakenPath) {
		this.photoTakenPath = photoTakenPath;
	}

	public MessagingCountDB getMessagingCountDB() {
		return messagingCountDB;
	}

	public void setMessagingCountDB(MessagingCountDB messagingCountDB) {
		this.messagingCountDB = messagingCountDB;
	}

	public RefreshUI getRefreshUI() {
		return refreshUI;
	}

	public void setRefreshUI(RefreshUI refreshUI) {
		this.refreshUI = refreshUI;
	}

	public int getPhotoTimeout() {
		return photoTimeout;
	}

	public void setPhotoTimeout(int photoTimeout) {
		this.photoTimeout = photoTimeout;
	}

	public MessagingAckDB getMessagingAckDB() {
		return messagingAckDB;
	}

	public void setMessagingAckDB(MessagingAckDB messagingAckDB) {
		this.messagingAckDB = messagingAckDB;
	}
	
	public void addNotifacaionIds(String id){
		notificationIds.add(id);
	}
	
	public Set<String> getNotificationIds(){
		return notificationIds;
	}

	public long getReceiveStatusUpdatedTime() {
		return receiveStatusUpdatedTime;
	}

	public void setReceiveStatusUpdatedTime(long receiveStatusUpdatedTime) {
		this.receiveStatusUpdatedTime = receiveStatusUpdatedTime;
	}
	
	public ChatBackgroundDB getChatBackgroundDB() {
		return chatBackgroundDB;
	}

	public void setChatBackgroundDB(ChatBackgroundDB chatBackgroundDB) {
		this.chatBackgroundDB = chatBackgroundDB;
	}

	public String getCurrentChatbackgroundReceiver() {
		return currentChatbackgroundReceiver;
	}

	public void setCurrentChatbackgroundReceiver(String currentChatbackgroundReceiver) {
		this.currentChatbackgroundReceiver = currentChatbackgroundReceiver;
	}
	
	
}
