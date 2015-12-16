package org.break_out.breakout.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by Maximilian Dühr on 16.12.2015.
 */
public class NetworkHelper {
    private final static String TAG = "NetworkHelper";
    private static NetworkState _networkState_currentState;

    /**
     * class to make access to network operations easier and centralized
     */
    private static NetworkHelper _instance;
    private static ArrayList<NetworkUpdateCallback> _arrayList_networkListeners;

    private NetworkHelper() {
        _arrayList_networkListeners = new ArrayList<>();
    }

    /**
     * @return instance of the NetworkHelper class to work with
     */
    public static NetworkHelper getInstance() {
        if (_instance == null) {
            _instance = new NetworkHelper();

        }
        return _instance;
    }

    /**
     * updates the current networkState if it is different than it was before
     *
     * @param type current type
     * @return true if type had to be cahnged, false if not
     */
    private static boolean setNetworkState(NetworkState type) {
        if (_networkState_currentState != type) {
            _networkState_currentState = type;
            return true;
        }
        return false;
    }

    /**
     * add a object that implemented the NetworkUpdateCallback interface and wants to be updated on the current network state
     *
     * @param networkUpdateCallback
     */
    public void addNetworkCallback(NetworkUpdateCallback networkUpdateCallback) {
        getListenersArrayList().add(networkUpdateCallback);
    }

    /**
     * remove an object from callback list
     *
     * @param networkUpdateCallback object to be removed
     */
    public void removeNetworkCallback(NetworkUpdateCallback networkUpdateCallback) {
        getListenersArrayList().remove(networkUpdateCallback);
    }

    /**
     * evaluates the current network situation and sends a update to all listeners if needed
     *
     * @param context
     */
    private static void sendNetworkUpdate(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        NetworkState currentState = getStateFromNetworkInfo(networkInfo);
        if (setNetworkState(currentState)) {
            if (_networkState_currentState != NetworkState.OFFLINE) {
                updateReceiversInternetActive(_networkState_currentState);
            } else {
                updateReceiversInternetInactive();
            }
        }
    }

    //TODO: evaluate usefullness
   /* private void updateNetworkState(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            setNetworkState(NetworkState.WIFI);
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            setNetworkState(NetworkState.MOBILE);
        } else {
            setNetworkState(NetworkState.OFFLINE);
        }
    }*/

    /**
     * @return list of listeners
     */
    private static ArrayList<NetworkUpdateCallback> getListenersArrayList() {
        if (_arrayList_networkListeners == null) {
            _arrayList_networkListeners = new ArrayList<>();
        }
        return _arrayList_networkListeners;
    }

    /**
     * extracts NetworkType data from given NetworkInfo
     *
     * @param networkInfo NetworkInfo object
     * @return a NetworkState representing the current connection state. Either WIFI, MOBILE or OFFLINE
     */
    private static NetworkState getStateFromNetworkInfo(@Nullable NetworkInfo networkInfo) {
        if (networkInfo != null) {
            int state = networkInfo.getType();
            switch (state) {
                case ConnectivityManager.TYPE_MOBILE:
                    return NetworkState.MOBILE;
                case ConnectivityManager.TYPE_WIFI:
                    return NetworkState.WIFI;
            }
        }
        return NetworkState.OFFLINE;
    }

    /**
     * gives a NetworkState object presenting the network state at the moment the method is called
     *
     * @param c current Context
     * @return either WIFI, MOBILE or OFFLINE represented by a NetworkState
     */
    public NetworkState getCurrentState(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        setNetworkState(getStateFromNetworkInfo(connectivityManager.getActiveNetworkInfo()));
        return _networkState_currentState;
    }

    /**
     * @param type type of the internetconnection, either WIFI or MOBILE
     */
    private static void updateReceiversInternetActive(NetworkState type) {
        //extra check if the right method is called
        if (type != NetworkState.OFFLINE) {
            for (NetworkUpdateCallback networkUpdateCallback : getListenersArrayList()) {
                networkUpdateCallback.onInternetConnected(type);
            }
        } else {
            updateReceiversInternetInactive();
        }
    }

    /**
     * update all listeners that internet is not available anymore
     */
    private static void updateReceiversInternetInactive() {
        for (NetworkUpdateCallback callback : getListenersArrayList()) {
            callback.onInternetDisabled();
        }
    }

    /**
     * callback interface to be implemented for a class to be updated by this class about the network state
     */
    public interface NetworkUpdateCallback {
        void onInternetConnected(NetworkState connectedNetworkState);

        void onInternetDisabled();
    }

    /**
     * enum representing the connection states we are interested in, WIFI, MOBILE and OFFLINE
     */
    public enum NetworkState {
        WIFI, MOBILE, OFFLINE
    }

    /**
     * class used to monitor network changes
     */
    public static class BreakOutConnectionBroadcastReceiver extends BroadcastReceiver {
        private final static String TAG = "ConBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            sendNetworkUpdate(context);
        }
    }
}