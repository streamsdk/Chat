package com.streamsdk.chat.addfriend;

import java.util.List;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.MainActivity;
import com.streamsdk.chat.R;

public class HistoryFragment extends ListFragment {

	HistoryAdapter ha;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.historylist_layout, container, false);
		List<String> history = ApplicationInstance.getInstance().getInivitationDB().getInvitations();
		ha = new HistoryAdapter(history, this.getActivity());

		if (history != null && history.size() > 0) {
			setListAdapter(ha);
			updateData(ha);
		} else {
			TextView tv = (TextView) v.findViewById(R.id.txtNoHistory);
			tv.setVisibility(View.VISIBLE);
		}
	
		return v;
	}

	private void updateData(final BaseAdapter adapter) {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	public void onListItemClick(ListView l, View v, int position, long id){
		String name = (String)ha.getItem(position);
		Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("receiver", name);    
        startActivity(intent);
	}
	
}
