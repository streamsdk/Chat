package com.streamsdk.chat;

import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements InfoWindowAdapter{

	private View view;
	
	public CustomInfoWindowAdapter(Activity activity){
		view = activity.getLayoutInflater().inflate(R.layout.custominfo_layout, null);
	}
	
	public View getInfoContents(Marker arg0) {
		return null;
	}

	public View getInfoWindow(Marker marker) {
		 render(marker, view);
         return view;
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
         
         String snippet = marker.getSnippet();
         TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
         if (snippet != null && snippet.length() > 12) {
             SpannableString snippetText = new SpannableString(snippet);
             snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
             snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
             snippetUi.setText(snippetText);
         } else {
             snippetUi.setText("");
         }
     
     }

	
}

