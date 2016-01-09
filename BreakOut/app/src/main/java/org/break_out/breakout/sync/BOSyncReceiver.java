package org.break_out.breakout.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Tino on 17.12.2015.
 */
public class BOSyncReceiver extends BroadcastReceiver {

    public static final String ACTION = "org.break_out.posting.SYNC_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        BOSyncController.getInstance(context).notifyDataChangedListeners();
    }

}
