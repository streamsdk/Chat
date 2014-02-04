package com.streamsdk.header;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.streamsdk.chat.R;

public class NamesAdapter extends ArrayAdapter<String> implements SectionIndexer, OnScrollListener, PinnedHeaderAdapter {
	
	private SectionIndexer mIndexer;
	private int mPinnedHeaderBackgroundColor;
	private int mPinnedHeaderTextColor;
    private Activity activity;
	
	public NamesAdapter(Activity act, int resourceId, int textViewResourceId, String[] objects) {
		super(act.getApplicationContext(), resourceId, textViewResourceId, objects);
		this.activity = act;
		this.mIndexer = new StringArrayAlphabetIndexer(objects, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		mPinnedHeaderBackgroundColor = act.getApplicationContext().getResources().getColor(R.color.pinned_header_background);
		mPinnedHeaderTextColor = act.getApplicationContext().getResources().getColor(R.color.pinned_header_text);
	}

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		bindSectionHeader(v, position);
		return v;
	}

    private void bindSectionHeader(View itemView, int position) {
    	final TextView headerView = (TextView) itemView.findViewById(R.id.header_text);
    	final View dividerView = itemView.findViewById(R.id.list_divider);
    	
        final int section = getSectionForPosition(position);
        if (getPositionForSection(section) == position) {
            String title = (String) mIndexer.getSections()[section];
            headerView.setText(title);
            headerView.setVisibility(View.VISIBLE);
	    	dividerView.setVisibility(View.GONE);
        } else {
        	headerView.setVisibility(View.GONE);
	    	dividerView.setVisibility(View.VISIBLE);
        }

        // move the divider for the last item in a section
        if (getPositionForSection(section + 1) - 1 == position) {
	    	dividerView.setVisibility(View.GONE);
        } else {
	    	dividerView.setVisibility(View.VISIBLE);
        }
    }
    
	public int getPositionForSection(int sectionIndex) {
        if (mIndexer == null) {
            return -1;
        }

        return mIndexer.getPositionForSection(sectionIndex);
    }
	
	public int getSectionForPosition(int position) {
        if (mIndexer == null) {
            return -1;
        }

        return mIndexer.getSectionForPosition(position);
    }
	
	@Override
	public Object[] getSections() {
        if (mIndexer == null) {
            return new String[] { " " };
        } else {
            return mIndexer.getSections();
        }
	}
      
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
        }		
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}

	@Override
	public int getPinnedHeaderState(int position) {
        if (mIndexer == null || getCount() == 0) {
            return PINNED_HEADER_GONE;
        }

        if (position < 0) {
            return PINNED_HEADER_GONE;
        }

        // The header should get pushed up if the top item shown
        // is the last item in a section for a particular letter.
        int section = getSectionForPosition(position);
        int nextSectionPosition = getPositionForSection(section + 1);
        
        if (nextSectionPosition != -1 && position == nextSectionPosition - 1) {
            return PINNED_HEADER_PUSHED_UP;
        }

        return PINNED_HEADER_VISIBLE;
	}

	@Override
	public void configurePinnedHeader(View v, int position, int alpha) {
		TextView header = (TextView) v;
		
		final int section = getSectionForPosition(position);
		final String title = (String) getSections()[section];
		
		header.setText(title);
		if (alpha == 255) {
			header.setBackgroundColor(mPinnedHeaderBackgroundColor);
			header.setTextColor(mPinnedHeaderTextColor);
		} else {
			header.setBackgroundColor(Color.argb(alpha, 
					Color.red(mPinnedHeaderBackgroundColor),
					Color.green(mPinnedHeaderBackgroundColor),
					Color.blue(mPinnedHeaderBackgroundColor)));
			header.setTextColor(Color.argb(alpha, 
					Color.red(mPinnedHeaderTextColor),
					Color.green(mPinnedHeaderTextColor),
					Color.blue(mPinnedHeaderTextColor)));
		}
	}
	
}
