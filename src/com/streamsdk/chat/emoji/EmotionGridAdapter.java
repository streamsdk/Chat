package com.streamsdk.chat.emoji;

import java.util.List;

import com.streamsdk.chat.EditTextEmojSelected;
import com.streamsdk.chat.R;
import com.streamsdk.chat.R.id;
import com.streamsdk.chat.R.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class EmotionGridAdapter extends BaseAdapter{

	private Context context = null;
	private List<String> icons;
	private EditTextEmojSelected ete;
	public EmotionGridAdapter(Context context, List<String> icons, EditTextEmojSelected ete){
		this.context = context;
		this.icons = icons;
		this.ete = ete;
	}
	
	@Override
	public int getCount() {
		return icons.size();
	}

	@Override
	public Object getItem(int position) {
		return icons.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.emoticons_item, null);			
		}
	
	   final String hexString = icons.get(position);
	   int id = context.getResources().getIdentifier("emoji_" + hexString,"drawable", "com.streamsdk.chat");
	   if (id!=0){
	      ImageView image = (ImageView) v.findViewById(R.id.item);
	      Drawable drawable = context.getResources().getDrawable(id);
	      image.setImageDrawable(drawable);
	      image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ete.clicked(hexString);		
			}
		});
	   }else{
		   Log.i("", "");
	   }
	   return v;
	}

}
