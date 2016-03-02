package org.break_out.breakout;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orm.SugarContext;

import org.break_out.breakout.manager.BOLocationManager;

/**
 * Created by Maximilian Dühr on 01.03.2016.
 */
public class LocationService extends Service {
    private static final String TAG = "LocationService";
    BOLocationManager _locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"OnBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            SugarContext.init(this);
        } catch(Exception e) {
            e.printStackTrace();
        }

        //Toast.makeText(this,"Location obtained!",Toast.LENGTH_SHORT).show();

        _locationManager = BOLocationManager.getInstance(this);
        BOLocation lastLocation =_locationManager.getLocation(this, new BOLocationManager.BOLocationRequestListener(){
            @Override
            public void onLocationObtained(BOLocation currentLocation) {
                _locationManager.stopListeningForChanges(this);
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        SugarContext.terminate();
        super.onDestroy();
    }
}
