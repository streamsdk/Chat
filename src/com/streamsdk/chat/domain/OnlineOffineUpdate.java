package com.streamsdk.chat.domain;

public class OnlineOffineUpdate {

	private long lastUpdatedTime;
	private boolean updateToOnline = false;
	private boolean updateToOffline = false;
	
	public long getLastUpdatedTime() {
		return lastUpdatedTime;
	}
	public void setLastUpdatedTime(long lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	public boolean isUpdateToOnline() {
		return updateToOnline;
	}
	public void setUpdateToOnline(boolean updateToOnline) {
		this.updateToOnline = updateToOnline;
	}
	public boolean isUpdateToOffline() {
		return updateToOffline;
	}
	public void setUpdateToOffline(boolean updateToOffline) {
		this.updateToOffline = updateToOffline;
	}
	
}
