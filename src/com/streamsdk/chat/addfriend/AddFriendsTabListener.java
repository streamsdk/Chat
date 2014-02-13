package com.streamsdk.chat.addfriend;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;


public class AddFriendsTabListener <T extends Fragment> implements TabListener{

	
	private Fragment mFragment;
	private final Activity mActivity;
	private final String mTag;
	private final Class<T> mClass;
	
    public AddFriendsTabListener(Activity activity, String tag, Class<T> clz) {
		mActivity = activity;
		mTag = tag;
		mClass = clz;
	}
	
    public void onTabReselected(Tab tab, FragmentTransaction ignoreft) {
		FragmentManager fragMgr = mActivity.getFragmentManager();
        FragmentTransaction ft = fragMgr.beginTransaction();

		if (mFragment == null) {
			// If not, instantiate and add it to the activity
			mFragment = Fragment.instantiate(mActivity, mClass.getName());
			ft.add(android.R.id.content, mFragment, mTag);		
		} else {
			ft.attach(mFragment);
		}
		if (mFragment  instanceof AddFriendsFragment){
			AddFriendsFragment aff =(AddFriendsFragment)mFragment;
			aff.dismissPopup();
		}
		ft.commit();
	}

	public void onTabSelected(Tab tab, FragmentTransaction ignoreft) {
		FragmentManager fragMgr = mActivity.getFragmentManager();
        FragmentTransaction ft = fragMgr.beginTransaction();

		if (mFragment == null) {
			// If not, instantiate and add it to the activity
			mFragment = Fragment.instantiate(mActivity, mClass.getName());
			ft.add(android.R.id.content, mFragment, mTag);
		} else {
			ft.attach(mFragment);
		}
		if (mFragment  instanceof AddFriendsFragment){
			AddFriendsFragment aff =(AddFriendsFragment)mFragment;
			aff.dismissPopup();
		}
	
		ft.commit();
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (mFragment != null) {
			if (mFragment  instanceof AddFriendsFragment){
				AddFriendsFragment aff =(AddFriendsFragment)mFragment;
				aff.dismissPopup();
			}
	        ft.detach(mFragment);
	    }
	}

}
