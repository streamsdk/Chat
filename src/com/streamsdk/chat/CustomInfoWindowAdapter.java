package com.streamsdk.chat;

import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements InfoWindowAdapter{

	private View view;
	private boolean buttonShow = false;
	
	
	public CustomInfoWindowAdapter(Activity ac){
		view = ac.getLayoutInflater().inflate(R.layout.custominfo_layout, null);
	}
	
	public View getInfoContents(Marker arg0) {
		return null;
	}

	public View getInfoWindow(Marker marker) {
		 render(marker, view);
         return view;
	}
	
	public void setButtonShow(){
		buttonShow = true;
	}
	
	 private void render(Marker marker, View view) {
        
         String title = marker.getTitle();
         TextView titleUi = ((TextView) view.findViewById(R.id.title));
         if (title != null) {
             // Spannable string allows us to edit the formatting of the text.
             SpannableString titleText = new SpannableString(title);
             titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
             titleUi.setText(titleText);
         } else {
             titleUi.setText("");
         }
        
         
         if (buttonShow){
             TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
             snippetUi.setVisibility(View.VISIBLE);
         }
     }

	
}

