package com.streamsdk.search;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.streamsdk.cache.FileCache;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

public class SearchImageActivity  extends ListActivity implements SearchDoneCallback, OnScrollListener{

	List<String> images;
	SearchImageAdapter adapter;
	SearchImageActivity activity;
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
	
	private void stopAnimation(){
		  LinearLayout sll = (LinearLayout)findViewById(R.id.spinnerSearchSection);
		  ImageView iv = (ImageView)sll.findViewById(R.id.sp);
		  sll.setVisibility(View.GONE);
		  iv.setAnimation(null);
	}
	
	private void showAnimation(){
		// Create an animation
		  RotateAnimation rotation = new RotateAnimation(
		      0f,
		      360f,
		      Animation.RELATIVE_TO_SELF,
		      0.5f,
		      Animation.RELATIVE_TO_SELF,
		      0.5f);
		  rotation.setDuration(1200);
		  rotation.setInterpolator(new LinearInterpolator());
		  rotation.setRepeatMode(Animation.RESTART);
		  rotation.setRepeatCount(Animation.INFINITE);

		  LinearLayout sll = (LinearLayout)findViewById(R.id.spinnerSearchSection);
		  ImageView iv = (ImageView)sll.findViewById(R.id.sp);
		  sll.setVisibility(View.VISIBLE);
		  iv.startAnimation(rotation);
		  
	}
	
	public void sendTo(Bitmap bitmap){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
		String id = String.valueOf(System.currentTimeMillis());
		FileCache.getInstance().writeFileToDisk(id, in);
		File file = FileCache.getInstance().loadFile(id);
		String path = file.getAbsolutePath();
		ApplicationInstance.getInstance().setPhotoTakenPath(path);
		setResult(RESULT_OK);
        finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			 clearCurrentSearch();
			 onBackPressed();
			 return true;
		default:
			 return super.onOptionsItemSelected(item);
		}
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
				stopAnimation();
				adapter.notifyDataSetChanged();	
			}
	    });
	}
	
	private void clearCurrentSearch(){
		startPage = 0;
		images.clear();
		ImageCache.getInstance().clearTempImages();
		adapter.clearList();
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
			showAnimation();
			SearchThread st = new SearchThread(startPage, searchTerm);
			st.setSearchDoneCallback(this);
			new Thread(st).start();
			loading = true;
		}
		
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		
		
	}

}
