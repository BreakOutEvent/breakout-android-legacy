package org.break_out.breakout.sync.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.BOSyncReceiver;
import org.break_out.breakout.sync.model.SyncEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * This Service will iterate over all entities returned by {@link BOSyncController#getEntityClasses()}
 * and upload the pending uploads, updates and deletions.
 *
 * <br /><br />
 *
 * Created by Tino on 18.12.2015.
 */
public class UploadService extends Service {

    private static final String TAG = "UploadService";

    private boolean _isRunning = false;
    private List<SyncEntity> _processedEntities = new ArrayList<SyncEntity>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "--- START COMMAND ---");

        startUploading();
        return START_STICKY;
    }

    private void startUploading() {

        if(_isRunning) {
            return;
        }

        _isRunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                new UploaderThread().run();
            }
        }).start();
    }

    /**
     * Returns the next entity to be uploaded, updated or deleted on the server.
     * Will return null if there is no further entity waiting for an upload in this
     * particular run of the service (for every run there will only be one try for
     * every upload).
     *
     * @return The next entity to be uploaded, updated or deleted on server or null
     */
    private SyncEntity getNextPendingUpload() {
        String whereClause = SyncEntity.IS_UPLOADING_NAME + " = ? OR " + SyncEntity.IS_UPDATING_NAME + " = ? OR " + SyncEntity.IS_DELETING + " = ?";
        String[] attrs = {"1", "1", "1"};

        for(Class<? extends SyncEntity> entityClass : BOSyncController.getInstance(this).getEntityClasses()) {
            List<? extends SyncEntity> candidates = SyncEntity.find(entityClass, whereClause , attrs);

            for(SyncEntity entity : candidates) {
                if(!_processedEntities.contains(entity)) {
                    _processedEntities.add(entity);
                    return entity;
                }
            }
        }

        return null;
    }

    private void notifyDataChanged(Class<? extends SyncEntity> type) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BOSyncReceiver.ACTION);
        broadcastIntent.putExtra(BOSyncReceiver.ENTITY_TYPE, type);
        sendBroadcast(broadcastIntent);

        Log.d(TAG, "Sent broadcast for entity type " + type.getSimpleName());
    }

    private class UploaderThread extends Thread {

        @Override
        public void run() {

            while(_isRunning) {
                SyncEntity entity = getNextPendingUpload();

                if(entity == null) {
                    _isRunning = false;
                } else {
                    Log.d(TAG, "Starting upload of \"" + entity.toString() + "\"");

                    // Upload, update or delete entity
                    boolean success = false;
                    switch(entity.getState()) {
                        case UPLOADING:
                            success = entity.uploadToServerSync();
                            break;
                        case UPDATING:
                            success = entity.updateOnServerSync();
                            break;
                        case DELETING:
                            success = entity.deleteOnServerSync();
                            break;
                    }

                    if(success) {
                        Log.d(TAG, entity.getState().toString() + " operation on server successful");

                        // Update local DB with NORMAL state
                        entity.setState(SyncEntity.SyncState.NORMAL);

                        // Send broadcast indicating the change of the data
                        notifyDataChanged(entity.getClass());
                    } else {
                        Log.d(TAG, entity.getState().toString() + " operation on server failed");
                    }
                }
            }

            stopSelf();

            Log.d(TAG, "--- STOPPED SERVICE ---");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
