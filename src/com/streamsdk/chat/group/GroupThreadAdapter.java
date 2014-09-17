package com.streamsdk.chat.group;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stream.api.StreamCategoryObject;
import com.stream.api.StreamObject;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.ApplicationInstance;
import com.streamsdk.chat.R;

public class GroupThreadAdapter extends BaseAdapter{

	Activity activity;
	StreamCategoryObject posts;
    	
	public GroupThreadAdapter(Activity ac){
		activity = ac;
		posts = ApplicationInstance.getInstance().getGroupPosts();
	}
	
	public int getCount() {
		if (posts == null)
			return 0;
	    List<StreamObject> sos = posts.getListOfStreamObject();
		return sos.size();
	}

	public Object getItem(int index) {
	    List<StreamObject> sos = posts.getListOfStreamObject();
	    return sos.get(index);
	}

	public long getItemId(int index) {
		return 0;
	}

	public View getView(int position, final View view, ViewGroup vg) {

		posts = ApplicationInstance.getInstance().getGroupPosts();
		LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder viewHolder;
		View v = null;
		StreamObject so = (StreamObject)getItem(position);
		String postedBy = (String)so.get("postedBy");
		if (view == null){
	    	v = inflater.inflate(R.layout.groupitems_layout, vg, false);
			viewHolder = new ViewHolder();
			viewHolder.posterName = (TextView)v.findViewById(R.id.txtGroupPosterName);
			viewHolder.imgGroup = (ImageView)v.findViewById(R.id.imgGroup);
		}else{
			v = view;
			viewHolder = (ViewHolder)view.getTag();
		}
		viewHolder.posterName.setText(postedBy);
		Bitmap bitmap = ImageCache.getInstance().getFriendImage(postedBy);
		if (bitmap != null)
		    viewHolder.imgGroup.setImageBitmap(bitmap);
		else{
			Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.yahoo_no_avatar);
			viewHolder.imgGroup.setImageBitmap(bm);
		}
		
		
		v.setTag(viewHolder);
		return v;
		
	}
	
	static class ViewHolder{
		TextView posterName;
		ImageView imgGroup;
	}

}
