package com.olegsmirnov.codalinefinalproject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

class MyLocationListener implements LocationListener {
    private Context context;
    private double longitude;
    private double latitude;

    MyLocationListener(Context context) {
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location loc) {
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();
        String Text = "My current location is: " + "Latitude = " + loc.getLatitude() + "Longitude =" + loc.getLongitude();
        Toast.makeText(context, Text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context,
                "Gps disabled, please turn it on to detect your location",
                Toast.LENGTH_SHORT ).show();
    }


    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(context,
                "Gps Enabled",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }
}
