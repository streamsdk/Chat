package com.streamsdk.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.streamsdk.util.AnonymousUsers;

public class AnonymousScreen extends ListActivity{
	
	 AnoymousAdapter aa;
	 Activity activity;
	 
	 protected void onResume(){
		 super.onResume();
		 if (aa != null)
			updateData();
	 }
	 
	 public void onCreate(Bundle savedInstanceState){
		 
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.anonymouslist_layout);
		 activity = this;
		 Set<String> users = AnonymousUsers.getAllAnonymousUsers(getApplicationContext());
		 List<String> userList = new ArrayList<String>(users);
		 aa = new AnoymousAdapter(userList, this);
		 setListAdapter(aa);
		 getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				String name = (String)aa.getItem(position);
				Intent intent = new Intent(activity, MainActivity.class);
	            intent.putExtra("receiver", name);    
	            startActivity(intent);
			}
		 });
	 }
	 
	 private void updateData(){
		runOnUiThread(new Runnable(){
				public void run() {
					aa.notifyDataSetChanged();
				}
		});
	}

}
