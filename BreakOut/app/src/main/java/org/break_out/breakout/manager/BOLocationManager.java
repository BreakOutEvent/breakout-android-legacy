package org.break_out.breakout.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.BOLocation;
import org.break_out.breakout.LocationService;
import org.break_out.breakout.R;
import org.break_out.breakout.constants.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Maximilian Dühr on 01.03.2016.
 */
public class BOLocationManager  {
    private static final String TAG = "BOLocationManager";
    private static final int MIN_DISTANCE = 5;
    private static final int TEN_MINUTES = 1*1000*60*10;
    private static final int ALARM_INTERVAL = TEN_MINUTES;
    private static final int REQUESTCODE_ALARMINTENT = 0;
    private static boolean _isLocating;
    private static boolean _networkAvailable = false;
    private static boolean _gpsAvailable = false;
    private static Context _context;
    private static LocationManager _locationManager;
    private static ArrayList<BOLocationListener> _listenerList = new ArrayList<>();
    private static ArrayList<BOLocationServiceListener> _statusListenerList = new ArrayList<>();
    private static BOLocationManager _instance;

    private static SharedPreferences _preferences;

    private static AlarmManager _alarmManager;

    private BOLocationManager(Context c) {
        _locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        _alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        _listenerList = new ArrayList<>();
        _statusListenerList = new ArrayList<>();
        _context = c;

        //TODO:REMOVE
        _preferences = _context.getSharedPreferences(_context.getString(R.string.PREFERENCES_GLOBAL),Context.MODE_PRIVATE);

        updateAvailableLocationServices();
        BOLocationListener persistentLocationListener = new BOLocationListener() {
            @Override
            public void onLocationObtained(BOLocation currentLocation) {
                BOLocation.save(currentLocation);
            }
        };

    }

    /**
     * get the instance from BOLocationManager
     * @param c Context of the calling Activity
     * @return
     */
    public static BOLocationManager getInstance(Context c) {
        if(_instance == null) {
            _instance = new BOLocationManager(c);
        }
        return _instance;
    }

    //Location Constructors
    public static BOLocation createLocation(long timestamp,double latitude,double longitude) {
        return new BOLocation(timestamp,latitude,longitude);
    }

    public static BOLocation createLocation(int remoteId,int teamId,int eventId,String teamName,long timestamp,double latitude,double longitude) {
        if(getLocationById(remoteId)== null) {
            BOLocation location = createLocation(timestamp,latitude,longitude);
            location.setRemoteId(remoteId);
            location.setEventId(eventId);
            location.setTeamId(teamId);
            location.setTeamName(teamName);
            location.save();

            return  location;
        }
       else return getLocationById(remoteId);
    }

    /**
     * Start periodically updating the Location
     * @param c Context of the calling Activity
     */
    public void startUpdateLocationPeriodically(Context c) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(c.getString(R.string.intentaction_locationservice));
        broadcastIntent.putExtra(BOLocationBroadcastReceiver.KEY_EXTRA,BOLocationBroadcastReceiver.KEY_START);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c,REQUESTCODE_ALARMINTENT,broadcastIntent,0);
        c.sendBroadcast(broadcastIntent);
        _alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,0,_preferences.getInt("interval",TEN_MINUTES)*1000,pendingIntent);
    }

    /**
     * Tell all listeners that locationServices have changed
     */
    private static void callServiceListeners() {
        for(BOLocationServiceListener listener : _statusListenerList) {
            listener.onServiceStatusChanged();
            Log.d(TAG,"listener called");
        }
    }

    public void addServiceListener(BOLocationServiceListener listener) {
        if(!_statusListenerList.contains(listener)) {
            _statusListenerList.add(listener);
        }
    }

    public void removeServiceListener(BOLocationServiceListener listener) {
        if(_statusListenerList.contains(listener)) {
            _statusListenerList.remove(listener);
        }
    }



    @Nullable
    public static BOLocation getLocationById(int remoteId) {
        ArrayList<BOLocation> listWithCorrectId = new ArrayList<>();
        listWithCorrectId.addAll( BOLocation.findWithQuery(BOLocation.class,"SELECT * FROM BO_Location WHERE _REMOTE_ID = "+remoteId+" LIMIT 1"));
        if(listWithCorrectId.size()>0) {
            return listWithCorrectId.get(0);
        }
        return null;
    }

    /**
     * stop periodically updating the Location
     * @param c context of the calling Activity
     */
    public void stopUpdateLocationPeriodically(Context c) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(c.getString(R.string.intentaction_locationservice));
        broadcastIntent.putExtra(BOLocationBroadcastReceiver.KEY_EXTRA,BOLocationBroadcastReceiver.KEY_STOP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c,REQUESTCODE_ALARMINTENT,broadcastIntent,0);
        c.sendBroadcast(broadcastIntent);
        _alarmManager.cancel(pendingIntent);
    }

    public boolean locationServicesAvailable() {
        updateAvailableLocationServices();
        return _gpsAvailable||_networkAvailable;

    }


    /**
     * get the list of registered listeners
     * @return
     */
    private ArrayList<BOLocationListener> getListenerList() {
        return _listenerList;
    }


    private LocationManager getLocationManager() {
        return _locationManager;
    }

    /**
     * request a current location with
     * a listener to handle the result
     * and return the last known location
     * or null if there is none
     * @param c context of the calling activity
     * @param listener LocationListener to asynchronous handle the result of the call
     * @return current last known location or null if there is none
     */
    @Nullable
    public BOLocation getLocation(Context c, BOLocationRequestListener listener) {
        requestObtainingLocation(c,listener);
        return BOLocation.last(BOLocation.class);
    }

    /**
     * Get the last known Location
     * @return the last known Location as BOLocation object or null if there is none
     */
    @Nullable
    public BOLocation getLastKnownLocation() {
        return BOLocation.last(BOLocation.class);
    }

    /**
     * Request the System to obtain the current Location and fire the given Listener
     * @param c Context of the calling Activity
     * @param _locationListener Listener that fires when a new Location is available
     */
    private void requestObtainingLocation(Context c, BOLocationRequestListener _locationListener) {
        if(!_isLocating) {
            if(_locationManager == null) {
                _locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
            }
            _isLocating = true;

            //update status booleans
            updateAvailableLocationServices();
            //get fastest obtained location
            try {
                if(_networkAvailable) {
                    _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, _locationListener);
                }
                if(_gpsAvailable) {
                    _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _locationListener);
                }
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove listener from LocationManager
     * @param listener
     */
    public void stopListeningForChanges(BOLocationRequestListener listener) {
        try {
            getLocationManager().removeUpdates(listener);
        } catch(SecurityException e) {
            e.printStackTrace();
        }
        _isLocating = false;
    }


    /**
     * Check which location services are active and store that information in the corresponding variables
     */
    private void updateAvailableLocationServices() {
        Log.d(TAG,"provider updated");
        _networkAvailable = _locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        _gpsAvailable = _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Class to handle LocationListener calls
     */
    private static abstract class BOLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            BOLocation boLocation = new BOLocation(System.currentTimeMillis(),location.getLatitude(),location.getLongitude());
            boLocation.save();
            broadcastLocationListUpdated(_context);
            onLocationObtained(boLocation);
        }

        @Override
        public final void onStatusChanged(String provider, int status, Bundle extras) {
            callServiceListeners();
            //TODO
        }

        @Override
        public final void onProviderEnabled(String provider) {
            Log.d(TAG,"provider: "+provider);
            //TODO
        }

        @Override
        public final void onProviderDisabled(String provider) {
            //TODO
        }

        public abstract void onLocationObtained(BOLocation currentLocation);
    }

    /**
     * Send broadcast to show that there is a new location stored
     * @param c
     */
    private static void broadcastLocationListUpdated(Context c) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(c.getString(R.string.intentaction_updatelist));
        c.sendBroadcast(broadcastIntent);
    }

    public static void getAllLocationsFromServer(Context c, @Nullable BOLocationListObtainedListener listener) {
        new GetAllLocationsFromServerTask(c,listener).execute();
    }

    /**
     * Class to handle LocationRequests one-shot
     */
    public static abstract class BOLocationRequestListener extends BOLocationListener{
        @Override
        public void onLocationChanged(Location location) {
            super.onLocationChanged(location);
            try {
                _locationManager.removeUpdates(this);
                _isLocating = false;
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public interface BOLocationServiceListener {
        public void onServiceStatusChanged();
    }

    /**
     * Receiver that starts and stops the LocationService that obtains locations in the background
     */
    public static final class BOLocationBroadcastReceiver extends BroadcastReceiver {
        private static final String KEY_EXTRA = "LocationServiceExtra";
        private static final String KEY_START = "START";
        private static final String KEY_STOP = "STOP";
        private Intent boLocationServiceIntent;

        public BOLocationBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boLocationServiceIntent = new Intent(context, LocationService.class);
            if(intent.getExtras() != null) {
                String intentExtra = intent.getExtras().getString(KEY_EXTRA)== null ? "" : intent.getExtras().getString(KEY_EXTRA);

                if(!intentExtra.isEmpty()) {
                    if(intentExtra.equals(KEY_START)) {
                        context.startService(boLocationServiceIntent);
                    } else if(intentExtra.equals(KEY_STOP)) {
                        context.stopService(boLocationServiceIntent);
                    }
                }
            }
        }
    }

    public interface BOLocationListObtainedListener {
        void onListObtained();
    }

    public ArrayList<BOLocation> getAllLocationsFromTeam(int teamId) {
        ArrayList<BOLocation> returnList = new ArrayList<>();
        returnList.addAll(BOLocation.findWithQuery(BOLocation.class,"SELECT * FROM BO_Location WHERE _team_id = "+teamId+" ORDER BY _timestamp DESC"));
        Log.d(TAG,"user "+teamId+" has "+returnList.size()+" locations");
        return returnList;
    }


    /**
     * Clears the location database
     */
    public static void deleteLocationHistory() {
        BOLocation.deleteAll(BOLocation.class);
    }

    /**
     * Updates the visible representation of the Location database
     */
    public static ArrayList<BOLocation> getLocationHistory() {
        ArrayList<BOLocation> resultList = new ArrayList<>();
        resultList.addAll(BOLocation.listAll(BOLocation.class));
        return resultList;
    }

    public static final class BOLocationServiceBroadcastReceiver extends BroadcastReceiver {

        public BOLocationServiceBroadcastReceiver() {
            //empoty constructor
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"providers changed");
            callServiceListeners();
        }
    }

    //AsyncTasks

    public static class GetAllLocationsFromServerTask extends AsyncTask<Void,Void,ArrayList<BOLocation>> {
        private Context context;
        private BOLocationListObtainedListener listener;

        public GetAllLocationsFromServerTask(Context c, @Nullable  BOLocationListObtainedListener l) {
            context = c;
            listener = l;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<BOLocation> doInBackground(Void... params) {
            int id = UserManager.getInstance(context).getCurrentUser().getEventId()==-1 ? 1 : UserManager.getInstance(context).getCurrentUser().getEventId();
            ArrayList<BOLocation> resultList = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Constants.Api.BASE_URL+"/event/"+UserManager.getInstance(context).getCurrentUser().getEventId()+"/location/")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d(TAG,"responseBody: "+responseBody);

                JSONArray responseArray = new JSONArray(responseBody);
                for(int i = 0; i < responseArray.length(); i++) {
                    JSONObject curLocationObj = responseArray.getJSONObject(i);
                    BOLocation newLocation = BOLocation.fromJSON(curLocationObj);
                    resultList.add(newLocation);
                    Log.d(TAG,"location added");
                }
                return resultList;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<BOLocation> boLocations) {
            super.onPostExecute(boLocations);
            if(boLocations != null) {
                Log.d(TAG,"it worked! "+boLocations.size());
                if(listener!=null) {
                    listener.onListObtained();
                }
            } else {
                Log.d(TAG,"it did not work!");
            }
        }
    }
}
