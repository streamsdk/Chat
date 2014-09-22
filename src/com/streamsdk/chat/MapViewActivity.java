package com.streamsdk.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewActivity extends Activity{
	
    private String lat = "";
    private String longt = "";
    private String address = "";
    private GoogleMap mMap;
  
	protected  void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.mapview_layout);
		 Intent intent = getIntent();
         lat = intent.getExtras().getString("lat");
         longt = intent.getExtras().getString("longt");
		 address = intent.getExtras().getString("address");
         setUpMapIfNeeded();
		 showMyLocation();
	}
	
	
	private void showMyLocation(){
	
	     mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
	     LatLng ll = new LatLng(Double.parseDouble(lat), Double.parseDouble(longt));
	     Marker maker = mMap.addMarker(new MarkerOptions()
         .position(ll)
         .title(address));
        
         maker.showInfoWindow();
         mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15)); 
    	
	}
	
	 private void setUpMapIfNeeded() {
	        if (mMap == null) {
	        	MapFragment mf = (MapFragment)getFragmentManager().findFragmentById(R.id.mapView);
	            mMap = mf.getMap();
	        }
	 }


}
