package com.streamsdk.search;

import java.util.ArrayList;
import java.util.List;

import com.streamsdk.chat.R;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;

public class SearchImageActivity  extends ListActivity implements SearchDoneCallback{

	List<String> images;
	SearchImageAdapter adapter;
	Activity activity;
	
	public void onCreate(Bundle savedInstanceState) {
	 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchimagelist_layout);
		activity = this;
	    images = new ArrayList<String>();
		adapter = new SearchImageAdapter(images, activity);
		setListAdapter(adapter);
		SearchThread st = new SearchThread(0);
		st.setSearchDoneCallback(this);
		new Thread(st).start();
	}
	
	private void update(){
		
		runOnUiThread(new Runnable(){
			public void run() {
		        adapter.notifyDataSetChanged();	
			}
	    });
	}
	
	public void searchDone(String url, boolean finishAll) {
	   images.add(url);
	   update();
	}

}
