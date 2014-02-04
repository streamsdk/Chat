package com.streamsdk.chat;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.streamsdk.cache.FileCache;

public class VideoFullScreen extends Activity {

    private String path = "";
    private String send = "";
    private VideoView mVideoView;
    Button sendButton = null;
    Button playButton = null;
    Button retakeButton = null;
    boolean play = false;
    String timeout = "";
    int count = 0;
    TextView timeText;
    Timer timer;
       
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        timeout = intent.getExtras().getString("duration");
        if (timeout != null){
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.video_layout);
        final Activity activity = this;
        path = intent.getExtras().getString("path");
        send = intent.getExtras().getString("send");
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        mVideoView.setVideoPath(path);
        MediaController mc = new MediaController(this);
        mc.setAnchorView(mVideoView);
        mVideoView.setMediaController(mc);
        mVideoView.requestFocus();
        sendButton = (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ApplicationInstance.getInstance().setRecordingVideoPath(path);
				setResult(RESULT_OK);
	            finish();
			}
		});
        
        playButton = (Button)findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 if (play){
					 mVideoView.start();
                     playButton.setText("PAUSE");			
				 }else{
					 mVideoView.pause();
					 playButton.setText("PLAY");			
				 }
				 play = !play;
			}
		});
        
        retakeButton = (Button)findViewById(R.id.retakeButton);
        retakeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				File file  = new File(path);
				file.delete();
		        Intent intent = new Intent(activity, AndroidVideoCapture.class);
		        startActivity(intent);
		        finish();
			}
		});
        
        mVideoView.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				if (timeout == null){
				  playButton.setText("PLAY");
				  play = true;
				}else{
			      finish();		
				}
			}
		});
        
        FrameLayout fl = (FrameLayout)findViewById(R.id.timeCountLayout);
        fl.setVisibility(View.GONE);
        if (timeout != null){
        	fl.setVisibility(View.VISIBLE);
            double counter = Double.parseDouble(timeout);
            count = (int)counter;
        	timer = new Timer();
            timer.schedule(new TickClass(), 0, 1000);
            fl.setVisibility(View.VISIBLE);
            timeText = (TextView)fl.findViewById(R.id.txtTimeCount);
          	sendButton.setVisibility(View.GONE);
            retakeButton.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);
        }
        
        if (send == null){
           mVideoView.start();
		   sendButton.setVisibility(View.GONE);
           retakeButton.setVisibility(View.GONE);
        }
        
        
       if (send != null){
           play = true;
           playButton.setText("PLAY");
           mVideoView.seekTo(1);
           sendButton.setVisibility(View.VISIBLE);
           retakeButton.setVisibility(View.VISIBLE);
       }
    }
    
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = (String)item.getTitle();
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case 0:
			if (title.equals("Save")) {
			   	File file = FileCache.getInstance().getOutputMediaFile();
			    File sourceFile = new File(path);
			   	try {
				    file.createNewFile();
				    copyFile(sourceFile, file);
			        
			    } catch (IOException e) {
				
			    }	    
			   
             } else {

			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	 
	public boolean onCreateOptionsMenu(Menu menu){
	   menu.add("Save").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	   return true;
	}
	
	 private class TickClass extends TimerTask{
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						timeText.setText(String.valueOf(count));
						count--;
						if (count == -1)
						    onBackPressed();
					}
				});
			}
	    }
}

