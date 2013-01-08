package com.example.snsmap;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class LocationOverlay extends MyLocationOverlay{

	private LocationCallback callback;
	
	public LocationOverlay(Context context, MapView mapView) {
		super(context, mapView);
		callback = (LocationCallback)context;
	}	

	@Override
	public synchronized boolean runOnFirstFix(Runnable runnable) {
		callback.locationCallback();
		disableMyLocation();
		return super.runOnFirstFix(runnable);
	}



	@Override
	public synchronized void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		
		Log.d("mayloce", "myalocaaaaaaaaaaaaaaaaaaaa");
		disableMyLocation();
		callback.locationCallback();
	}
}
