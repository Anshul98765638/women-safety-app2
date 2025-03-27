package com.example.safety_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import com.google.android.gms.location.*;

public class LocationHelper {

    private FusedLocationProviderClient fusedLocationClient;
    private Context context;
    private LocationCallback locationCallback;
    private LocationResultListener locationResultListener;

    public interface LocationResultListener {
        void onLocationReceived(double latitude, double longitude);
    }

    public LocationHelper(Context context, LocationResultListener listener) {
        this.context = context;
        this.locationResultListener = listener;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        locationResultListener.onLocationReceived(latitude, longitude);
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                        break;
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
}
