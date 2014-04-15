package com.streamsdk.chat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.streamsdk.cache.FileCache;
import com.streamsdk.chat.domain.SendMap;

public class SendMapViewActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{
	
	
	private LocationClient mLocationClient;
	  private GoogleMap mMap;
	  String address = "";
	
	 private static final LocationRequest REQUEST = LocationRequest.create()
	            .setInterval(5000)         // 5 seconds
	            .setFastestInterval(16)    // 16ms = 60fps
	            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	 protected void onResume() {
		 super.onResume();
		 setUpLocationClientIfNeeded();
		 setUpMapIfNeeded();
		 mLocationClient.connect();
	 }
	 
	 public void onPause() {
		 super.onPause();
	     if (mLocationClient != null) {
	            mLocationClient.disconnect();
	     } 
	 }
	 
	 public void shareLocation(){
		 SnapshotReadyCallback callback = new SnapshotReadyCallback() {
	            public void onSnapshotReady(Bitmap snapshot) {
	            	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	            	snapshot.compress(Bitmap.CompressFormat.JPEG, 100, stream);
	            	File mapFile = FileCache.getInstance().generateMediaOutputFilePath(".jpg");
	            	try {
	                    FileOutputStream fos = new FileOutputStream(mapFile);
	                    fos.write(stream.toByteArray());
	                    fos.close();
	                } catch (FileNotFoundException e) {
	             
	                } catch (IOException e) {}
	            	SendMap sm = new SendMap();
	            	sm.setAddress(address);
	            	sm.setLat(String.valueOf(mLocationClient.getLastLocation().getLatitude()));
	            	sm.setLon(String.valueOf(mLocationClient.getLastLocation().getLongitude()));
	            	sm.setPath(mapFile.getAbsolutePath());
	                ApplicationInstance.getInstance().setMapTaken(sm);
	            	setResult(RESULT_OK);
			        finish();  
	            }
	      };
	      
	  	 mMap.snapshot(callback);
	 }
	 
    public boolean onCreateOptionsMenu(Menu menu){
		 
		 final SearchView searchView = new SearchView(getActionBar().getThemedContext());
		 searchView.setQueryHint("enter location");
			
		   searchView.setOnQueryTextListener(new OnQueryTextListener() {
			public boolean onQueryTextSubmit(final String query) {
				//getActionBar().selectTab(searchTab);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				searchView.clearFocus();
				searchView.setQuery("", false);
				//showDialog("searching location... " + query);
				address = "";
				Geocoder geo = new Geocoder(getApplicationContext());
		            try {
						List<Address> addes = geo.getFromLocationName(query, 5);
						 for (Address ad : addes){
						    	int line = ad.getMaxAddressLineIndex();
						    	int index = 0;
						    	while(line >=0 ){
						    		String lineStr = ad.getAddressLine(index);
						    		if (line != 0)
						    		   address = address + lineStr + "\n";
						    		else
						    		   address = address + lineStr;
						    	
						    		line--;
						    		index++;
						    	}
						    	double lat = ad.getLatitude();
						    	double longt = ad.getLongitude();
						    	LatLng ll = new LatLng(lat, longt);
						        Marker maker = mMap.addMarker(new MarkerOptions()
					                           .position(ll)
					                           .title(address));
						        maker.showInfoWindow();
						        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
						    	break;
						    }
						
						
					} catch (IOException e1) {
						
					}
			 
	            
				return false;
			}
			
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		 
		 menu.add("Search").setIcon(R.drawable.ic_search).setActionView(searchView).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		 menu.add("Refresh").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
					
		 return true;
	 }
	 
	 
	 public void showMyLocation() {
	      if (mLocationClient != null && mLocationClient.isConnected()) {
	            LatLng ll = new LatLng(mLocationClient.getLastLocation().getLatitude(), mLocationClient.getLastLocation().getLongitude());
	            CustomInfoWindowAdapter ca = new CustomInfoWindowAdapter(this);
	            ca.setButtonShow();
	            mMap.setInfoWindowAdapter(ca);
	            
	            address = "";
	            Geocoder geo = new Geocoder(getApplicationContext());
	            try {
					List<Address> all = geo.getFromLocation(mLocationClient.getLastLocation().getLatitude(), mLocationClient.getLastLocation().getLongitude(), 5);
				    for (Address ad : all){
				    	int line = ad.getMaxAddressLineIndex();
				    	int index = 0;
				    	while(line >=0 ){
				    		String lineStr = ad.getAddressLine(index);
				    		if (line != 0)
				    		   address = address + lineStr + "\n";
				    		else
				    		   address = address + lineStr;
				    		line--;
				    		index++;
				    	}
				    	break;
				    }
	            
	            } catch (IOException e) {
				       Log.i("", "");
				}
	            
	            
	            Marker maker = mMap.addMarker(new MarkerOptions()
               .position(ll)
               .title(address));
	            maker.showInfoWindow();
	            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
	            mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
					public void onInfoWindowClick(Marker arg0) {
				         shareLocation();	
					}
				});
	            
	          
	      }
	 }
	  
	 
	protected  void onCreate(Bundle savedInstanceState) {
		
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.mapview_layout);
	     getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		String title = (String)item.getTitle();
		switch (itemId) {
		  case android.R.id.home:
			   onBackPressed();
			   return true;
			case 0:
				 if (title.equals("Refresh")){
			        showMyLocation();
				 }else{
					 
				 }
			     return true;   
		  default:
			 return super.onOptionsItemSelected(item);
		}
	}
	
	
	 private void setUpMapIfNeeded() {
	        if (mMap == null) {
	        	MapFragment mf = (MapFragment)getFragmentManager().findFragmentById(R.id.mapView);
	            mMap = mf.getMap();
	        }
	 }
	
	 private void setUpLocationClientIfNeeded() {
	        if (mLocationClient == null) {
	            mLocationClient = new LocationClient(getApplicationContext(), this,  // ConnectionCallbacks 
	            		this); // OnConnectionFailedListener
	        }
	 }

	public void onConnectionFailed(ConnectionResult arg0) {
		Log.i("", "");
	}

	public void onConnected(Bundle arg0) {
		 mLocationClient.requestLocationUpdates(REQUEST, this);  // LocationListener
		 showMyLocation();
	}

	public void onDisconnected() {
		Log.i("", "");
	}

	@Override
	public void onLocationChanged(Location location) {
	     Log.i("", "");
	}

}
