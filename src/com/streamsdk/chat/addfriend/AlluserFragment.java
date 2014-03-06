package com.streamsdk.chat.addfriend;

import com.streamsdk.chat.R;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AlluserFragment extends ListFragment{
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.alluserlist_layout, container, false);
		AllUserAdapter aa = new AllUserAdapter(getActivity());
		setListAdapter(aa);
		return v;
	}

}
