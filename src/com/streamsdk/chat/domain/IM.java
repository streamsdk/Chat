package com.streamsdk.chat.domain;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.streamsdk.cache.ImageCache;
import com.streamsdk.util.BitmapUtils;

public class IM {

	private long chatTime;
	private String chatMessage;
	private String from;
	private String to;
	private boolean isSelf = false;
	private boolean isVoice = false;
	private boolean isImage = false;
	private boolean isVideo = false;
	private boolean disappear = false;
	private String voiceImFileName = "";
	private int recordingTime;
	private String selfSendImagePath;
	private String receivedFilePath;
	private String timeout = "";
	private String viewed = "";
	private String requestStatus = "";
	private String requestType = "";
	private String primaryKey = "";
	private String thumbNailId;
	private String bodyJSON;
	
	public String getBodyJSON() {
		return bodyJSON;
	}

	public void setBodyJSON(String bodyJSON) {
		this.bodyJSON = bodyJSON;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getReceivedFilePath() {
		return receivedFilePath;
	}

	public void setReceivedFilePath(String receivedFilePath) {
		this.receivedFilePath = receivedFilePath;
	}

	public Bitmap getSelfSendImage(){
		return ImageCache.getInstance().getImage(selfSendImagePath);
	}
		

	public String getSelfSendImagePath(){
		return selfSendImagePath;
	}
	
	public void storeSendImage(String path){
	   
	   if (isVideo && ImageCache.getInstance().getImage(path) == null){
		   Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
		   if (thumb != null){
		      Bitmap resizedBitmap = Bitmap.createScaledBitmap(thumb, 230, 230, false);
		      ImageCache.getInstance().putNew(path, resizedBitmap);
		   }
	   }
		
	   if (isImage && ImageCache.getInstance().getImage(path) == null){
		   Bitmap sBitMap = BitmapUtils.loadImageForFullScreen(path,  230, 230, 300);
		   ImageCache.getInstance().putNew(path, sBitMap);
		}
		selfSendImagePath = path;
	}
	
	public Bitmap getReceivedFriendImageBitmap() {
		return ImageCache.getInstance().getImage(receivedFilePath);
	}
	
	public boolean isVideo() {
		return isVideo;
	}
	public void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}
	public boolean isImage() {
		return isImage;
	}
	public void setImage(boolean isImage) {
		this.isImage = isImage;
	}
	public int getRecordingTime() {
		return recordingTime;
	}
	public void setRecordingTime(int recordingTime) {
		this.recordingTime = recordingTime;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public long getChatTime() {
		return chatTime;
	}
	public void setChatTime(long chatTime) {
		this.chatTime = chatTime;
	}
	public String getChatMessage() {
		return chatMessage;
	}
	public void setChatMessage(String chatMessage) {
		this.chatMessage = chatMessage;
	}
	public boolean isSelf() {
		return isSelf;
	}
	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}
	public boolean isVoice() {
		return isVoice;
	}
	public void setVoice(boolean isVoice) {
		this.isVoice = isVoice;
	}
	public String getVoiceImFileName() {
		return voiceImFileName;
	}
	public void setVoiceImFileName(String voiceImFileName) {
		this.voiceImFileName = voiceImFileName;
	}
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	public boolean isDisappear() {
		return disappear;
	}
	public void setDisappear(boolean disappear) {
		this.disappear = disappear;
	}
	public String getViewed() {
		return viewed;
	}
	public void setViewed(String viewed) {
		this.viewed = viewed;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getThumbNailId() {
		return thumbNailId;
	}

	public void setThumbNailId(String thumbNailId) {
		this.thumbNailId = thumbNailId;
	}
	
	public boolean equals(Object obj) {
        return chatTime == ((IM)obj).getChatTime() && primaryKey.equals(((IM)obj).getPrimaryKey());
	}
	
	public int hashCode(){
		return String.valueOf(chatTime).hashCode() + primaryKey.hashCode();
    }
	
}
