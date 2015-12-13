package org.break_out.breakout;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by Maximilian on 13.12.2015.
 */
public class LocationHelper {

    private static LocationHelper instance;

    private static ArrayList<Callback> _callbackList;


    private LocationHelper()
    {}

    /**
     *
     * @return instance of the LocationHelper
     */
    public static LocationHelper getInstance()
    {
        if(instance == null)
        {
            instance = new LocationHelper();
        }

        return instance;
    }

    private void sendLocationCallback(Location l)
    {
        for(Callback c : _callbackList)
        {
            c.onLocationObtained(l);
        }
    }


    /**
     * Callback interface to receive answer from the LocationHelper
     */
    public interface Callback
    {
        void onLocationObtained(Location l);
        void onServiceStatusChanged(String provider,boolean isActive);
    }
}
