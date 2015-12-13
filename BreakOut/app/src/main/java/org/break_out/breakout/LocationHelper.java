package org.break_out.breakout;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Maximilian Duehr on 13.12.2015.
 */
public class LocationHelper {

    private final static String TAG = "LocationHelper";
    private static final long TWO_MINUTES = 1000 * 60 * 2;
    private static final float MIN_DISTANCE = 10;
    private static LocationHelper instance;
    private static ArrayList<Callback> _callbackList;
    private static Location _lastKnownLocation;
    private static long _lastTimestamp;
    private LocationManager _locationManager;
    private LocationListener _locationListener;

    private boolean _isLocating;
    private boolean _networkAvailable;
    private boolean _gpsAvailable;


    private LocationHelper() {
    }

    /**
     * @return instance of the LocationHelper
     */
    public static LocationHelper getInstance() {
        if (instance == null) {
            instance = new LocationHelper();
            _callbackList = new ArrayList<Callback>();
            _lastTimestamp = 0;
        }

        return instance;
    }

    /**
     * request to start obtaining the location (fastest result will be picked)
     *
     * @param c Context of the calling activity
     */
    public void requestObtainingLocation(Context c) {
        if (!_isLocating) {
            if (_locationManager == null) {
                _locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
            }
            _isLocating = true;

            //update status booleans
            _networkAvailable = _locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            _gpsAvailable = _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (_locationListener == null) {
                _locationListener = new BreakOutLocationListener();
            }

            //get fastest obtained location
            try {
                if (_networkAvailable) {
                    _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, MIN_DISTANCE, _locationListener);
                }
                if (_gpsAvailable) {
                    _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, MIN_DISTANCE, _locationListener);
                }


            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * stop locationg request
     */
    public void stopObtainingLocation() {
        try {
            _locationManager.removeUpdates(_locationListener);
            _isLocating = false;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * add class callback
     *
     * @param c
     */
    public void addCallback(Callback c) {
        _callbackList.add(c);
    }

    /**
     * remove class callback
     *
     * @param c class that does not require updates from this class anymore
     * @return
     */
    public boolean removeCallback(Callback c) {
        return _callbackList.remove(c);
    }

    /**
     * update the booleans representing provier availability
     *
     * @param provider
     * @param status
     */
    private void updateProviderAvailability(String provider, boolean status) {
        switch (provider) {
            case LocationManager.GPS_PROVIDER: {
                _gpsAvailable = status;
                break;
            }
            case LocationManager.NETWORK_PROVIDER: {
                _networkAvailable = status;
                break;
            }
        }
    }

    /**
     * Inform all Callbacks about the newly obtained Location object
     *
     * @param l obtained Location
     */
    private void sendLocationCallback(Location l) {
        for (Callback c : _callbackList) {
            c.onLocationObtained(l);
        }
    }

    /**
     * Inform all Callbacks about the new positioning provider status
     *
     * @param provider
     * @param isActive
     */
    private void sendServiceStatusCallback(String provider, boolean isActive) {
        for (Callback c : _callbackList) {
            c.onServiceStatusChanged(provider, isActive);
        }
    }


    /**
     * Callback interface to receive answer from the LocationHelper
     */
    public interface Callback {
        void onLocationObtained(Location l);

        void onServiceStatusChanged(String provider, boolean isActive);
    }

    /**
     * own Locationlistener class to handle location updates
     */
    private class BreakOutLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            _lastKnownLocation = location;
            _lastTimestamp = System.currentTimeMillis();
            sendLocationCallback(_lastKnownLocation);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == LocationProvider.AVAILABLE) {
                sendServiceStatusCallback(provider, true);
                updateProviderAvailability(provider, true);
            } else {
                sendServiceStatusCallback(provider, false);
                updateProviderAvailability(provider, true);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            updateProviderAvailability(provider, true);
            sendServiceStatusCallback(provider, true);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateProviderAvailability(provider, false);
            sendServiceStatusCallback(provider, false);

        }
    }
}
