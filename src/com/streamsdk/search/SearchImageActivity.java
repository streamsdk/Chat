package com.streamsdk.search;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.R;

public class SearchImageActivity  extends ListActivity implements SearchDoneCallback, OnScrollListener{

	List<String> images;
	SearchImageAdapter adapter;
	Activity activity;
	boolean loading = false;
	int startPage=0;
	ProgressDialog pd;
	SearchDoneCallback callback;
	String searchTerm;
	
	public void onPause(){
	   super.onPause();
       ImageCache.getInstance().clearTempImages();		
    }
	
	public void onCreate(Bundle savedInstanceState) {
	 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchimagelist_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		activity = this;
		callback = this;
	    images = new ArrayList<String>();
		adapter = new SearchImageAdapter(images, activity);
		setListAdapter(adapter);
		 getListView().setOnScrollListener(this); 
		loading = true;
		
	}
	
	private void showDialog(String message) {
			pd = ProgressDialog.show(this, "", message, true, true);
    }
		
	
	public boolean onCreateOptionsMenu(final Menu menu){
	
		final SearchView searchView = new SearchView(getActionBar().getThemedContext());
		searchView.setQueryHint("search images");
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
				public boolean onQueryTextSubmit(final String query) {
					//getActionBar().selectTab(searchTab);
					clearCurrentSearch();
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
					searchView.clearFocus();
					searchView.setQuery("", false);
					showDialog("Searching Image... " + query);
					searchTerm = query;
					SearchThread st = new SearchThread(startPage, searchTerm);
					st.setSearchDoneCallback(callback);
					new Thread(st).start();
					return false;
				}
				
				public boolean onQueryTextChange(String newText) {
					return false;
				}
			});
		
		
		menu.add("Search").setIcon(R.drawable.ic_search).setActionView(searchView).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		return true;
    
	}
	
	private void update(){
		runOnUiThread(new Runnable(){
			public void run() {
		        adapter.notifyDataSetChanged();	
			}
	    });
	}
	
	private void clearCurrentSearch(){
		startPage = 0;
		images.clear();
		ImageCache.getInstance().clearTempImages();
		update();
	}
	
	public void searchDone(String url, boolean finishAll) {
	   pd.dismiss();
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
			SearchThread st = new SearchThread(startPage, searchTerm);
			st.setSearchDoneCallback(this);
			new Thread(st).start();
			loading = true;
		}
		
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		
		
	}

}
