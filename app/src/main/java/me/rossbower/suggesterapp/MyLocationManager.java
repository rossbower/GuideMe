package me.rossbower.suggesterapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;


public class MyLocationManager implements LocationListener {

    public interface OnLocChanged {
        public void locationChanged(Location location);
    }

    private OnLocChanged onLocChanged;
    private Context context;
    private LocationManager locationManager = null;
    private Location prevLoc = null;

    public MyLocationManager(OnLocChanged onLocChanged,
                             Context context) {
        this.onLocChanged = onLocChanged;
        this.context = context;
    }

    public void startLocationMonitoring() {
        locationManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE);

        try {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, this);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                    0, 0, this);
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        onLocChanged.locationChanged(location);
    }

    public void stopLocationMonitoring() {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}