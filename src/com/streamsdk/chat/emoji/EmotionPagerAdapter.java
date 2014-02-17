package com.streamsdk.chat.emoji;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;

import com.streamsdk.chat.EditTextEmojSelected;
import com.streamsdk.chat.R;

public class EmotionPagerAdapter extends PagerAdapter{

	List<String> emoticons;
	private int num = 30;
	private Activity activity;
	private EditTextEmojSelected ete;
	
	public EmotionPagerAdapter(Activity activity, List<String> emo, EditTextEmojSelected se){
		this.activity = activity;
		this.emoticons = emo;
		this.ete = se;
	}
	
	public void setTotalIconsPerPage(int n){
		num = n;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return (int) Math.ceil((double) emoticons.size()/ (double) num);
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		
		View layout = activity.getLayoutInflater().inflate(R.layout.emoticons_grid, null);

		int initialPosition = position * num;
		
		ArrayList<String> emoticonsInAPage = new ArrayList<String>();

		for (int i = initialPosition; i < initialPosition + num && i < emoticons.size(); i++) {
			emoticonsInAPage.add(emoticons.get(i));
		}
		
		GridView grid = (GridView) layout.findViewById(R.id.emoticons_grid);
		EmotionGridAdapter adapter = new EmotionGridAdapter(activity.getApplicationContext(), emoticonsInAPage, ete);
		grid.setAdapter(adapter);
		
		((ViewPager) collection).addView(layout);

		return layout;
	}
	
	
	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}
