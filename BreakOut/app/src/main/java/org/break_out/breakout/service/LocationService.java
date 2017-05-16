package org.break_out.breakout.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.manager.BOLocationManager;
import org.break_out.breakout.model.BOLocation;

/**
 * Created by Maximilian Dühr on 01.03.2016.
 */
public class LocationService extends Service {
    private static final String TAG = "LocationService";
    BOLocationManager _locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "OnBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStart");
        _locationManager = BOLocationManager.getInstance(this);
        BOLocation lastLocation = _locationManager.getLocation(this, new BOLocationManager.BOLocationRequestListener() {
            @Override
            public void onLocationObtained(BOLocation currentLocation) {
                _locationManager.stopListeningForChanges(this);
                Log.d(TAG,"location got");
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
