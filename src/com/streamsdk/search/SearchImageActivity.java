package com.streamsdk.search;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.R;

public class SearchImageActivity  extends ListActivity implements SearchDoneCallback, OnScrollListener{

	List<String> images;
	SearchImageAdapter adapter;
	Activity activity;
	boolean loading = false;
	int startPage=0;
	
	public void onPause(){
	   super.onPause();
       ImageCache.getInstance().clearTempImages();		
    }
	
	public void onCreate(Bundle savedInstanceState) {
	 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchimagelist_layout);
		activity = this;
	    images = new ArrayList<String>();
		adapter = new SearchImageAdapter(images, activity);
		setListAdapter(adapter);
		 getListView().setOnScrollListener(this); 
		loading = true;
		SearchThread st = new SearchThread(startPage);
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
	   if (finishAll){
		   loading = false;
		   startPage = startPage + 8;
	   }
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		boolean loadMore =  firstVisibleItem + visibleItemCount + 1 >= adapter.getCount() && adapter.getCount()!=0 && startPage < 56;
		if (loadMore && !loading){
			SearchThread st = new SearchThread(startPage);
			st.setSearchDoneCallback(this);
			new Thread(st).start();
			loading = true;
		}
		
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		
		
	}

}
