package com.streamsdk.chat.addfriend;


import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.stream.api.QueryResultsCallback;
import com.stream.api.StreamCallback;
import com.stream.api.StreamObject;
import com.stream.api.StreamQuery;
import com.stream.api.StreamUser;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;
import com.streamsdk.chat.domain.FriendRequest;

public class AddFriendMainActivity extends Activity{
	
	ProgressDialog pd;
	Tab addTab;
	Tab searchTab;
	Tab historyTab;
	
	public void onCreate(Bundle savedInstanceState) {
	 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_navigation);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		 
		addTab =  getActionBar().newTab().setText("Add")
			        .setTabListener(new AddFriendsTabListener<AddFriendsFragment>(this, "", AddFriendsFragment.class));
			        
	    /* searchTab =  getActionBar().newTab().setText("Search")
			        .setTabListener(new AddFriendsTabListener<SearchFriendFragment>(this, "", SearchFriendFragment.class));*/
			        
	     historyTab =  getActionBar().newTab().setText("History")
			        .setTabListener(new AddFriendsTabListener<HistoryFragment>(this, "", HistoryFragment.class));
			        
	     getActionBar().addTab(addTab);
		// getActionBar().addTab(searchTab);
	     getActionBar().addTab(historyTab);
	
	}
	
	
    private void showDialog(String message) {
		pd = ProgressDialog.show(this, "", message, true, true);
	}
	
	public boolean onCreateOptionsMenu(final Menu menu){
		
		 final SearchView searchView = new SearchView(getActionBar().getThemedContext());
		 searchView.setQueryHint("search friends");
			
		   searchView.setOnQueryTextListener(new OnQueryTextListener() {
			public boolean onQueryTextSubmit(final String query) {
				//getActionBar().selectTab(searchTab);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				searchView.clearFocus();
				searchView.setQuery("", false);
				showDialog("Searching Friend... " + query);
				StreamUser su = new StreamUser();
				su.searchUser(query, new StreamCallback() {
					public void result(boolean succeed, String errorMessage) {
					      pd.dismiss();
					      if (succeed){
					    	 Message message = new Message();
					    	 Bundle data = new Bundle();
					    	 data.putString("userName", query);
					    	 message.setData(data);
					    	 ApplicationInstance.getInstance().getHandler("search").sendMessage(message);
					      }
					}
				});
				return false;
			}
			
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		
		 menu.add("Refresh").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 menu.add("Search").setIcon(R.drawable.ic_search).setActionView(searchView).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		 return true;
    }
	
	private void refreshFriendRequest(){
		
		StreamQuery sq = new StreamQuery(ApplicationInstance.getInstance().getLoginName());
		sq.setQueryLogicAnd(false);
		sq.whereEqualsTo("status", "friend");
		sq.whereEqualsTo("status", "request");
		sq.findInBackground(new QueryResultsCallback() {
			public void results(List<StreamObject> streamObjects) {
				List<FriendRequest> frs = new ArrayList<FriendRequest>();
				 for (StreamObject so : streamObjects){
			    	  FriendRequest fr = new FriendRequest();
			    	  String status = (String)so.get("status");
			    	  if (status!=null){
			    		  fr.setFriendName(so.getId());
			    		  fr.setStatus(status);
			    	  }
			    	  frs.add(fr);
			      }
				
				ApplicationInstance.getInstance().getFriendDB().syncUpdate(frs);
				ApplicationInstance.getInstance().getHandler("add").sendEmptyMessage(0);
			}
		});
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		String title = (String)item.getTitle();
		switch (itemId) {
		case android.R.id.home:
			 onBackPressed();
			 return true;
		case 0:
			 if (title.equals("Refresh")){
		        refreshFriendRequest();
			 }else{
				 
			 }
		     return true;
		default:
			 return super.onOptionsItemSelected(item);
		}
	}

}
