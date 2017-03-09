package org.break_out.breakout.model;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.break_out.breakout.manager.BOLocationManager;

import java.io.FileDescriptor;

public class BOBackgroundLocatingService extends Service {
    //TODO: More battery-friendly location tracking
    private static boolean isRunning = false;
    private static final String TAG = "BOBckgrndLcatingService";
    private static BOLocationManager _boLocationManager;
    private static LocationManager _locationManager;
    private BackgroundLocationListener listenerNetwork;
    private BackgroundLocationListener listenerGPS;

    private static final int MIN_DISTANCE = 5 * 1000;
    private static final long MIN_TIME = 3 * 1000 * 60 * 60;
    public BOBackgroundLocatingService() {
    }

    private class BackgroundLocationListener implements LocationListener {
        Location lastLocation;

        public BackgroundLocationListener(String provider){
            lastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            BOLocation loc = BOLocationManager.createLocation(System.currentTimeMillis()/1000,location.getLatitude(),location.getLongitude());
            loc.setIsPosted(false);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        Log.d(TAG,"service oncreate");
        if(listenerGPS==null) {
            listenerGPS = new BackgroundLocationListener(LocationManager.GPS_PROVIDER);
        }
        if(listenerNetwork==null){
            listenerNetwork = new BackgroundLocationListener(LocationManager.NETWORK_PROVIDER);
        }
        if(_locationManager == null) {
            _locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
        try{
            _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DISTANCE,listenerGPS);
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE,listenerGPS);
        }catch (SecurityException e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.d(TAG,"onDestroy");
        if(_locationManager!=null) {
            try {
                if(listenerGPS!=null) {
                    _locationManager.removeUpdates(listenerGPS);
                }
                if(listenerNetwork!=null){
                    _locationManager.removeUpdates(listenerNetwork);
                }
            }catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
}
