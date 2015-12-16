package org.break_out.breakout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Maximilian Dühr on 16.12.2015.
 */
public class NetworkHelper {


    private static NetworkHelper _instance;

    private NetworkHelper() {
    }

    public static NetworkHelper getInstance() {
        if (_instance == null) {
            _instance = new NetworkHelper();
        }
        return _instance;
    }


    public class BreakOutConnectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
