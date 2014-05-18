package com.streamsdk.search;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.R;

public class SearchImageAdapter extends BaseAdapter{

	private List<String> images;
	private SearchImageActivity activity;
	
	public SearchImageAdapter(List<String> im, SearchImageActivity ac){
		images = im;
		activity = ac;
	}
	
	public void clearList(){
		images.clear();
	}
	
	public int getCount() {
		return images.size();
	}

	public Object getItem(int index) {
		return images.get(index);
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View view, ViewGroup vg) {
		
		LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = null;
		ViewHolder viewHolder;
		if (view == null){
			v = inflater.inflate(R.layout.searchimage_layout, vg, false);
			viewHolder = new ViewHolder();
		    viewHolder.searchImage = (ImageView)v.findViewById(R.id.searchImage);
		}else{
			v = view;
			viewHolder = (ViewHolder)view.getTag();
		}
		String url = images.get(position);
		final Bitmap bitmap = ImageCache.getInstance().justGetImage(url);
		if (bitmap != null){
			viewHolder.searchImage.setImageBitmap(bitmap);
			viewHolder.searchImage.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
				     activity.sendTo(bitmap);	
				}
			});
		}else{
			
		}
		
		v.setTag(viewHolder);
		return v;
		
	}
	
	static class ViewHolder{
		ImageView searchImage;
	}

}