package com.streamsdk.chat.addfriend;

import java.util.List;
import java.util.Map;

import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AbsListView.OnScrollListener;

public class AlluserFragment extends ListFragment implements OnScrollListener, LoadingDoneCallback{
	
	AllUserAdapter allUserAdapter;
	boolean loading = false;
	View rootView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.alluserlist_layout, container, false);
		allUserAdapter = new AllUserAdapter(getActivity());
		setListAdapter(allUserAdapter);
		return rootView;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
    	  super.onActivityCreated(savedInstanceState);
		  getListView().setOnScrollListener(this);    
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

		  LinearLayout sll = (LinearLayout)rootView.findViewById(R.id.spinnerSection);
		  ImageView iv = (ImageView)sll.findViewById(R.id.sp);
		  sll.setVisibility(View.VISIBLE);
		  iv.startAnimation(rotation);
		  
	}
	
	
	private void stopAnimation(){
		  LinearLayout sll = (LinearLayout)rootView.findViewById(R.id.spinnerSection);
		  ImageView iv = (ImageView)sll.findViewById(R.id.sp);
		  sll.setVisibility(View.GONE);
		  iv.setAnimation(null);
	}
	  

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		boolean loadMore =  firstVisibleItem + visibleItemCount + 1 >= allUserAdapter.getCount() && allUserAdapter.getCount()!=0;
		List<Map<String, String>> users = ApplicationInstance.getInstance().getAllUsers();
		Map<String, String> currentLastUser = users.get(users.size() - 1);
		String nextMarker = currentLastUser.get("nextmarker");
		if (nextMarker != null && loadMore && !loading){
			UsersLoadThread userLoad = new UsersLoadThread(nextMarker, 100);
			userLoad.setCallback(this);
			loading = true;
			showAnimation();
			new Thread(userLoad).start();
		}
		
	}
	
	private void updateData(){
			this.getActivity().runOnUiThread(new Runnable(){
					public void run() {
						allUserAdapter.reload();
						allUserAdapter.notifyDataSetChanged();
						stopAnimation();
						loading = false;
					}
			});
	}
	
	public void loadUsersDone() {
		updateData();
	}
	
     public void onScrollStateChanged(AbsListView arg0, int arg1) {
		
	}

}
