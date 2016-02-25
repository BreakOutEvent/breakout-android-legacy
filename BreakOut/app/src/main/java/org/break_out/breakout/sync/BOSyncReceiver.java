package org.break_out.breakout.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.break_out.breakout.sync.model.BOSyncEntity;

/**
 * Created by Tino on 17.12.2015.
 */
public class BOSyncReceiver extends BroadcastReceiver {

    private static final String TAG = "BOSyncReceiver";

    public static final String ACTION = "org.break_out.posting.SYNC_RECEIVER";
    public static final String ENTITY_TYPE = "entity_type";

    @Override
    public void onReceive(Context context, Intent intent) {
        Class<? extends BOSyncEntity> type = (Class<? extends BOSyncEntity>) intent.getSerializableExtra(ENTITY_TYPE);

        if(type == null) {
            Log.e(TAG, "Could not get affected entity type from intent (was null)! Did you set it as a serializable extra using ENTITY_TYPE?");
            return;
        }

        BOSyncController.getInstance(context).notifyDataChangedListeners(type);
    }

}
