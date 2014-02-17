package com.streamsdk.chat;

import java.util.Timer;
import java.util.TimerTask;


import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecordingProgress extends DialogFragment{
	
	private static final int TIMER_FREQ = 1000;
	Timer progressBarAdvancer;
	TextProgressBar progressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	              Bundle savedInstanceState) {
		 
	        View view = inflater.inflate(R.layout.recording_layout, container);
	        
	        progressBar = (TextProgressBar) view.findViewById(R.id.progressBarWithText);
	        progressBar.setMax(100000);
	      

	        progressBarAdvancer = new Timer();
	        progressBarAdvancer.scheduleAtFixedRate(new TimerTask() {

	                public void run() {
	                	int p = progressBar.getProgress() + TIMER_FREQ;
	                    progressBar.setProgress(p);
	                    progressBar.setText(String.valueOf(p/1000) + "\"");

	                }
	            },
	            0, //Delay before first execution
	            TIMER_FREQ); 
	        
	        
	        
	        return view;
	    
	}
	
	public int getTime(){
		return progressBar.getProgress();
	}
	
	public void dismiss(){
		progressBarAdvancer.cancel();
		super.dismiss();
	}

}
