package com.streamsdk.header;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

public class NamesBaseAdaper extends BaseAdapter implements SectionIndexer, OnScrollListener, PinnedHeaderAdapter{

	private SectionIndexer mIndexer;
	private int mPinnedHeaderBackgroundColor;
	private int mPinnedHeaderTextColor;
    private Activity activity;
	private String objects[];
    
	public NamesBaseAdaper(Activity act, String[] o){
		this.activity = act;
		this.mIndexer = new StringArrayAlphabetIndexer(o, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		this.objects = o;
		mPinnedHeaderBackgroundColor = act.getApplicationContext().getResources().getColor(R.color.pinned_header_background);
		mPinnedHeaderTextColor = act.getApplicationContext().getResources().getColor(R.color.pinned_header_text);
		
	}
	
	public int getCount() {
		return objects.length;
	}

	public Object getItem(int position) {
		return objects[position];
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		String friendName = (String)getItem(position);
		ViewHolder viewHolder;
		View v = null;
		if (view == null){
			v = inflater.inflate(R.layout.myfriends_layout, parent, false);
			viewHolder = new ViewHolder();
		    viewHolder.textView = (TextView)v.findViewById(R.id.txtMyfriendFriendName);
		    viewHolder.imageView = (ImageView)v.findViewById(R.id.imgMainPageAvatar);
		    viewHolder.messagingCountLayout = (FrameLayout)v.findViewById(R.id.messageCountLayout);
		}else{
			v = view;
			viewHolder = (ViewHolder)view.getTag();
		}
		
		viewHolder.textView.setText(friendName);
		Bitmap bitmap = ImageCache.getInstance().getFriendImage(friendName);
		if (bitmap != null)
		    viewHolder.imageView.setImageBitmap(bitmap);
		else{
			Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.yahoo_no_avatar);
			viewHolder.imageView.setImageBitmap(bm);
		}
		
		Map<String, String> mCounts = ApplicationInstance.getInstance().getMessagingCountDB().getMessagingCount(friendName);
		if (mCounts != null){
			viewHolder.messagingCountLayout.setVisibility(View.VISIBLE);
			TextView mCountView = (TextView)viewHolder.messagingCountLayout.findViewById(R.id.txtMessageCount);
			mCountView.setText(String.valueOf(mCounts.size()));
		}else{
			viewHolder.messagingCountLayout.setVisibility(View.GONE);
		}
		
		
		v.setTag(viewHolder);
		
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

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
        }		
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		
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
	
	public Object[] getSections() {
        if (mIndexer == null) {
            return new String[] { " " };
        } else {
            return mIndexer.getSections();
        }
	}
	
    static class ViewHolder{
		TextView textView;
		ImageView imageView;
		FrameLayout messagingCountLayout;
	}

}
