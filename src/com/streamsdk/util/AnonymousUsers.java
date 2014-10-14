package com.streamsdk.util;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

import com.streamsdk.chat.ApplicationInstance;

public class AnonymousUsers {

	public static Set<String> getAllAnonymousUsers(Context context){
		Set<String> users = new HashSet<String>();
		SharedPreferences settings = context.getSharedPreferences(ApplicationInstance.ANO_USERS, 0);
		String userNames = settings.getString("users", null);
		if (userNames == null){
			return users;
		}else{
			String userNamesArray[] = userNames.split(",");
			for (String name :  userNamesArray)
				users.add(name);
		}
		return users;
	}
	
	private static boolean alreadyAdded(String userName, Context context){
		Set<String> users = getAllAnonymousUsers(context);
		if (users.contains(userName))
			return true;
		return false;
	}
	
	public static void addAnonymousUser(String user, Context context){
		if (alreadyAdded(user, context))
			return;
		SharedPreferences settings = context.getSharedPreferences(ApplicationInstance.ANO_USERS, 0);
	    SharedPreferences.Editor editor = settings.edit();
		String readIds = (String)settings.getString("users", null);
		if (readIds == null){
			editor.putString("users", user);
		}else{
			readIds = readIds + "," + user;
			editor.putString("users", readIds);
		}
		editor.commit();
	}
	
}
