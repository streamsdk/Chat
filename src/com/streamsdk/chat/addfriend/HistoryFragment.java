package com.streamsdk.chat.addfriend;

import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

public class HistoryFragment extends ListFragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.historylist_layout, container, false);
		List<String> history = ApplicationInstance.getInstance().getInivitationDB().getInvitations();
		HistoryAdapter ha = new HistoryAdapter(history, this.getActivity());

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
}
