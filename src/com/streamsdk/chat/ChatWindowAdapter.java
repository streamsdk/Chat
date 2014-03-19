package com.streamsdk.chat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.streamsdk.cache.FileCache;
import com.streamsdk.cache.ImageCache;
import com.streamsdk.chat.domain.IM;

public class ChatWindowAdapter extends BaseAdapter{

	List<IM> messages;
	Activity activity;
	MediaPlayer mPlayer;
	DisplayMetrics metrics;
	Object mActionMode;
	
	public ChatWindowAdapter(List<IM> messages, Activity activity, DisplayMetrics metrics){
		this.messages = messages;
		this.activity = activity;
		this.metrics = metrics;
	}
	
	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int arg0) {
		return messages.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
	private void startPlaying(String fileName) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("", "prepare() failed");
        }
    }
	
	private String dateConvert(long millionSeconds) {
	        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mm");
	        Date resultdate = new Date(millionSeconds);
	        String str = sdf.format(resultdate);
	        return str;
	}

	@Override
	public View getView(final int postion, View convertView, ViewGroup parent) {
		View v = null;
		ViewHolder viewHolder;
		LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final IM im = (IM)getItem(postion);
		boolean showTime = true;
		if (postion > 0){
			IM previousIm = (IM)getItem(postion - 1);
		    long previousTime = previousIm.getChatTime();
		    long currentChatTime = im.getChatTime();
		    showTime = (currentChatTime - previousTime) > (1000 * 60);
		}
		
		if (convertView == null) // we have to inflate a new layout
		{
			v = inflater.inflate(R.layout.rowchat_layout, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.l = (FrameLayout) v.findViewById(R.id.layoutHolder);
			viewHolder.txtTimestamp = (TextView) v.findViewById(R.id.txtTimestamp);
			viewHolder.txtMessageFriend = (TextView) v.findViewById(R.id.txtMessageFriend);
			viewHolder.imgAvatarFriend = (ImageView) v.findViewById(R.id.imgAvatarFriend);
			viewHolder.txtMessageSelf = (TextView) v.findViewById(R.id.txtMessageSelf);
			viewHolder.imgAvatarSelf = (ImageView) v.findViewById(R.id.imgAvatarSelf);
			viewHolder.bSelfVoicePlay = (Button) v.findViewById(R.id.playSelf);
			viewHolder.bPlayFriend = (Button)v.findViewById(R.id.playFriend);
			viewHolder.selfPickImage = (VideoIconImageView)v.findViewById(R.id.txtImageSelf); 
			viewHolder.friendPickImage = (VideoIconImageView)v.findViewById(R.id.txtImageFriend);
		}
		else
		{
			v = convertView;
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (showTime){
		   viewHolder.txtTimestamp.setVisibility(View.VISIBLE);
		   long chatTime = im.getChatTime();
		   String chatTimeString = dateConvert(chatTime);
		   viewHolder.txtTimestamp.setText(chatTimeString);
		}else{
			viewHolder.txtTimestamp.setVisibility(View.GONE);			   
		}
		
		if (im.isSelf()){
		   
		   Bitmap bm = ImageCache.getInstance().getFriendImage(ApplicationInstance.getInstance().getLoginName());
		   if (bm != null){
			   viewHolder.imgAvatarSelf.setImageBitmap(bm); 
		   }
		   if ((!im.isVoice() && !im.isImage() && !im.isVideo()) || im.isDisappear()){
			   viewHolder.txtMessageFriend.setVisibility(View.GONE);
			   viewHolder.imgAvatarFriend.setVisibility(View.GONE);
			   viewHolder.bSelfVoicePlay.setVisibility(View.GONE);
			   viewHolder.bPlayFriend.setVisibility(View.GONE);
			   viewHolder.selfPickImage.setVisibility(View.GONE);
			   viewHolder.friendPickImage.setVisibility(View.GONE);
			   viewHolder.txtMessageSelf.setVisibility(View.VISIBLE);
			   viewHolder.imgAvatarSelf.setVisibility(View.VISIBLE);
			   
			   RelativeLayout.LayoutParams params =  (android.widget.RelativeLayout.LayoutParams) viewHolder.imgAvatarSelf.getLayoutParams();
			   params.addRule(RelativeLayout.ALIGN_BOTTOM, viewHolder.txtMessageSelf.getId());
			   params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			   viewHolder.imgAvatarSelf.setLayoutParams(params);
			   
			   SpannableStringBuilder ssb = ParseMsgUtil.convetToHtml(im.getChatMessage(), activity.getApplicationContext());
			   viewHolder.txtMessageSelf.setText(ssb);
			   viewHolder.txtMessageSelf.setTextColor(Color.BLACK);
			   if (im.isDisappear()){
				   String word = im.isImage() ? "Photo" : "Video";
				   viewHolder.txtMessageSelf.setText(word + " for you");
				   viewHolder.txtMessageSelf.setTextColor(activity.getResources().getColor(R.color.redLogin));
			   }else{
				   viewHolder.txtMessageSelf.setOnLongClickListener(new View.OnLongClickListener() {
					public boolean onLongClick(View view) {
						 if (!im.isImage() && !im.isVideo()){
						    if (mActionMode != null) {
				                return false;
				            }
				            mActionMode = activity.startActionMode(mActionModeCallback);
				            view.setSelected(true);
				            ApplicationInstance.getInstance().setCurrentEditedIm(im);
				         }
						return true;
					 }
				   });
			   }
			
		   }else if(im.isImage() || im.isVideo()){
			   
			   if (im.isVideo())
				   viewHolder.selfPickImage.setVideo(true);
			   else{
				   viewHolder.selfPickImage.setVideo(false);
			   }
			   
			   viewHolder.txtMessageFriend.setVisibility(View.GONE);
			   viewHolder.imgAvatarFriend.setVisibility(View.GONE);
			   viewHolder.bSelfVoicePlay.setVisibility(View.GONE);
			   viewHolder.bPlayFriend.setVisibility(View.GONE);
			   viewHolder.txtMessageSelf.setVisibility(View.GONE);
			   viewHolder.friendPickImage.setVisibility(View.GONE);
			   viewHolder.imgAvatarSelf.setVisibility(View.VISIBLE);
			   viewHolder.selfPickImage.setVisibility(View.VISIBLE);
			   viewHolder.selfPickImage.setImageBitmap(im.getSelfSendImage());
			   viewHolder.selfPickImage.setOnClickListener(new View.OnClickListener() {
				    public void onClick(View v) {
				    	if (im.isImage()){
				    	   Intent intent = new Intent(activity.getApplicationContext(), FullScreenImageActivity.class);
				    	   intent.putExtra("path", im.getSelfSendImagePath());
				    	   activity.startActivity(intent);
				    	}
				    	if (im.isVideo()){
				    		Intent intent = new Intent(activity.getApplicationContext(),VideoFullScreen.class);
				    		intent.putExtra("path", im.getSelfSendImagePath());
					    	activity.startActivity(intent);
					    }
				    }
			   });
			   
			   viewHolder.selfPickImage.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					 if (mActionMode != null) {
			                return false;
			            }
			         mActionMode = activity.startActionMode(mActionModeMediaCallback);
			         view.setSelected(true);
			         ApplicationInstance.getInstance().setCurrentEditedIm(im);
					 return true;
				}
			  });
			   
			   RelativeLayout.LayoutParams params =  (android.widget.RelativeLayout.LayoutParams) viewHolder.imgAvatarSelf.getLayoutParams();
			   params.addRule(RelativeLayout.ALIGN_BOTTOM, viewHolder.selfPickImage.getId());
			   params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			   viewHolder.imgAvatarSelf.setLayoutParams(params);
			   
			   
		   }else{
			   viewHolder.txtMessageFriend.setVisibility(View.GONE);
			   viewHolder.imgAvatarFriend.setVisibility(View.GONE);
			   viewHolder.txtMessageSelf.setVisibility(View.GONE);
			   viewHolder.bPlayFriend.setVisibility(View.GONE);
			   viewHolder.selfPickImage.setVisibility(View.GONE);
			   viewHolder.friendPickImage.setVisibility(View.GONE);
			   viewHolder.imgAvatarSelf.setVisibility(View.VISIBLE);
			   viewHolder.bSelfVoicePlay.setVisibility(View.VISIBLE);
			   viewHolder.bSelfVoicePlay.setWidth(metrics.widthPixels/4);
			   viewHolder.bSelfVoicePlay.setText(String.valueOf(im.getRecordingTime()) + "\"");
			   
			   viewHolder.bSelfVoicePlay.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
			           startPlaying(im.getVoiceImFileName());
					}
				});
		   }
			
		}else{
		   
		   Bitmap fAvtar = ImageCache.getInstance().getFriendImage(im.getFrom());
		   if (fAvtar != null){
			  viewHolder.imgAvatarFriend.setImageBitmap(fAvtar); 
		   }
		   if ((!im.isVoice() && !im.isImage() && !im.isVideo()) || im.isDisappear()){
			  viewHolder.txtMessageFriend.setVisibility(View.VISIBLE);
			  viewHolder.imgAvatarFriend.setVisibility(View.VISIBLE);
			  viewHolder.txtMessageSelf.setVisibility(View.GONE);
			  viewHolder.imgAvatarSelf.setVisibility(View.GONE);
			  viewHolder.bSelfVoicePlay.setVisibility(View.GONE);
			  viewHolder.bPlayFriend.setVisibility(View.GONE);
			  viewHolder.selfPickImage.setVisibility(View.GONE);
			  viewHolder.friendPickImage.setVisibility(View.GONE);
			  
			  RelativeLayout.LayoutParams params =  (android.widget.RelativeLayout.LayoutParams) viewHolder.imgAvatarFriend.getLayoutParams();
			  params.addRule(RelativeLayout.ALIGN_BOTTOM, viewHolder.txtMessageFriend.getId());
			  params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			  viewHolder.imgAvatarFriend.setLayoutParams(params);
			    
			  SpannableStringBuilder ssb = ParseMsgUtil.convetToHtml(im.getChatMessage(), activity.getApplicationContext());
			  viewHolder.txtMessageFriend.setText(ssb);
			  viewHolder.txtMessageFriend.setTextColor(Color.BLACK);
			  if (im.isDisappear()){
				  String word = im.isImage() ? "Photo" : "Video";
				  String readWord = im.getViewed().equals("NO") ? "Click to view" : "Deleted";
				  viewHolder.txtMessageFriend.setText(word + " for you. " + readWord);
				  viewHolder.txtMessageFriend.setTextColor(activity.getResources().getColor(R.color.redLogin));
			  }else{
				  viewHolder.txtMessageFriend.setOnLongClickListener(new View.OnLongClickListener() {
						public boolean onLongClick(View view) {
							 if (!im.isImage() && !im.isVideo()){
							    if (mActionMode != null) {
					                return false;
					            }
					            mActionMode = activity.startActionMode(mActionModeCallback);
					            view.setSelected(true);
					            ApplicationInstance.getInstance().setCurrentEditedIm(im);
					         }
							return true;
						 }
				   });
			  }
			  viewHolder.txtMessageFriend.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
			        if (im.isDisappear() && im.getViewed().equals("NO")){
			        	Log.i("", "");
			          if (im.isImage()){
			        	 ((IM)getItem(postion)).setViewed("YES");
			        	  ApplicationInstance.getInstance().getMessagingHistoryDB().updateViewStatus(String.valueOf(im.getChatTime()));
			        	  Intent intent = new Intent(activity.getApplicationContext(), FullScreenImageActivity.class);
				    	  intent.putExtra("path", im.getReceivedFilePath());
				    	  intent.putExtra("duration", im.getTimeout());
				    	  activity.startActivity(intent);
				      }
			          if (im.isVideo()){
			        	  ((IM)getItem(postion)).setViewed("YES");
			        	  ApplicationInstance.getInstance().getMessagingHistoryDB().updateViewStatus(String.valueOf(im.getChatTime()));
			        	  Intent intent = new Intent(activity.getApplicationContext(),VideoFullScreen.class);
				    	  intent.putExtra("path", im.getReceivedFilePath());
				    	  intent.putExtra("duration", im.getTimeout());
				    	  activity.startActivity(intent);
				 	  }
				    }
				  }
			  });
			
		   }else if (im.isImage() || im.isVideo()){
			
			  if (im.isVideo()){
				  viewHolder.friendPickImage.setVideo(true);
			  }else{
				  viewHolder.friendPickImage.setVideo(false);
			  }
			  viewHolder.txtMessageFriend.setVisibility(View.GONE);
			  viewHolder.bSelfVoicePlay.setVisibility(View.GONE);
			  viewHolder.bPlayFriend.setVisibility(View.GONE);
			  viewHolder.txtMessageSelf.setVisibility(View.GONE);
			  viewHolder.imgAvatarSelf.setVisibility(View.GONE);
			  viewHolder.selfPickImage.setVisibility(View.GONE);
			  viewHolder.imgAvatarFriend.setVisibility(View.VISIBLE);
			  viewHolder.friendPickImage.setVisibility(View.VISIBLE);
			  viewHolder.friendPickImage.setImageBitmap(im.getReceivedFriendImageBitmap());
			  viewHolder.friendPickImage.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
				   if (im.isImage()){
					  Intent intent = new Intent(activity.getApplicationContext(), FullScreenImageActivity.class);
			    	  intent.putExtra("path", im.getReceivedFilePath());
			    	  activity.startActivity(intent);
				   }
				   if (im.isVideo()){
					  Intent intent = new Intent(activity.getApplicationContext(),VideoFullScreen.class);
			    	  intent.putExtra("path", im.getReceivedFilePath());
				      activity.startActivity(intent);
				   }
				}
			  });
			  
			  viewHolder.friendPickImage.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						 if (mActionMode != null) {
				                return false;
				            }
				         mActionMode = activity.startActionMode(mActionModeMediaCallback);
				         view.setSelected(true);
				         ApplicationInstance.getInstance().setCurrentEditedIm(im);
						 return true;
					}
				  });
			  
			  RelativeLayout.LayoutParams params =  (android.widget.RelativeLayout.LayoutParams) viewHolder.imgAvatarFriend.getLayoutParams();
			  params.addRule(RelativeLayout.ALIGN_BOTTOM, viewHolder.friendPickImage.getId());
			  params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			  viewHolder.imgAvatarFriend.setLayoutParams(params);
			  
			}else{
			  viewHolder.txtMessageSelf.setVisibility(View.GONE);
			  viewHolder.imgAvatarSelf.setVisibility(View.GONE);
			  viewHolder.txtMessageFriend.setVisibility(View.GONE);
			  viewHolder.bSelfVoicePlay.setVisibility(View.GONE);
			  viewHolder.selfPickImage.setVisibility(View.GONE);
			  viewHolder.friendPickImage.setVisibility(View.GONE);
			  viewHolder.imgAvatarFriend.setVisibility(View.VISIBLE);
			  viewHolder.bPlayFriend.setVisibility(View.VISIBLE);
			  viewHolder.bPlayFriend.setWidth(metrics.widthPixels/4);
			  viewHolder.bPlayFriend.setText(im.getRecordingTime() + "\"");
			  viewHolder.bPlayFriend.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
			           startPlaying(im.getVoiceImFileName());
					}
				});			
			}
		}
		
		
		v.setTag(viewHolder);
		
		return v;
	}
	
	static class ViewHolder
	{
		FrameLayout l;
		TextView txtTimestamp;
		TextView txtMessageFriend;
		ImageView imgAvatarFriend;
		TextView txtMessageSelf;
		ImageView imgAvatarSelf;
		VideoIconImageView  friendPickImage;
		VideoIconImageView selfPickImage;
		Button bSelfVoicePlay;
		Button bPlayFriend;
	}
	
	private ActionMode.Callback mActionModeMediaCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.mediacontext_menu, menu);
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        switch (item.getItemId()) {
	            //hi
	            case R.id.mediamenu_delete:
	            	IM im = ApplicationInstance.getInstance().getCurrentEditedIm();
	            	if (im != null){
	            		messages.remove(im);
	            		if (im.isSelf()){
	            			String path = im.getSelfSendImagePath();
	            			if (path.contains(FileCache.COOL_CHAT)){
	            			   File file = new File(path);
	            			   file.delete();
	            			}
	            		}else{
	            			String path = im.getReceivedFilePath();
	            			if (path.contains(FileCache.COOL_CHAT)){
	            			   File file = new File(path);
	            			   file.delete();
	            			}
	            		}
		                ApplicationInstance.getInstance().getCurrentChatListener().removeRecord(String.valueOf(im.getChatTime()));
		                notifyDataSetChanged();
	            	}
	            	ApplicationInstance.getInstance().setCurrentEditedIm(null);
	            	mode.finish(); 
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            default:
	                return false;
	        }
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	    	 mActionMode = null;
	    }
	};
	
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.context_menu, menu);
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.menu_copy:
	            	ClipboardManager clipboard =  (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
	            	IM im = ApplicationInstance.getInstance().getCurrentEditedIm();
		            if (im != null){
	            	   ClipData clip = ClipData.newPlainText("", im.getChatMessage());
                       clipboard.setPrimaryClip(clip);
	            	}
                    ApplicationInstance.getInstance().setCurrentEditedIm(null);
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            case R.id.menu_delete:
	            	im = ApplicationInstance.getInstance().getCurrentEditedIm();
	            	if (im != null){
	            		messages.remove(im);
		                ApplicationInstance.getInstance().getCurrentChatListener().removeRecord(String.valueOf(im.getChatTime()));
		                notifyDataSetChanged();
	            	}
	            	ApplicationInstance.getInstance().setCurrentEditedIm(null);
	            	mode.finish(); // Action picked, so close the CAB
	                return true;
	            default:
	                return false;
	        }
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	    	 mActionMode = null;
	    }
	};
	
	

}
