package org.break_out.breakout.sync.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.BOSyncReceiver;
import org.break_out.breakout.sync.model.BOSyncEntity;

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
    private List<BOSyncEntity> _processedEntities = new ArrayList<BOSyncEntity>();

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
    private BOSyncEntity getNextPendingUpload() {
        String whereClause = BOSyncEntity.COLUMN_IS_UPLOADING + " = ? OR " + BOSyncEntity.COLUMN_IS_UPDATING + " = ? OR " + BOSyncEntity.COLUMN_IS_DELETING + " = ?";
        String[] attrs = {"1", "1", "1"};

        for(Class<? extends BOSyncEntity> entityClass : BOSyncController.getInstance(this).getEntityClasses()) {
            List<? extends BOSyncEntity> candidates = BOSyncEntity.find(entityClass, whereClause, attrs);

            for(BOSyncEntity entity : candidates) {
                if(!_processedEntities.contains(entity)) {
                    _processedEntities.add(entity);
                    return entity;
                }
            }
        }

        return null;
    }

    private void notifyDataChanged(Class<? extends BOSyncEntity> type) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BOSyncReceiver.ACTION);
        broadcastIntent.putExtra(BOSyncReceiver.ENTITY_TYPE, type);
        broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(broadcastIntent);

        Log.d(TAG, "Sent broadcast for entity type " + type.getSimpleName());
    }

    private class UploaderThread extends Thread {

        @Override
        public void run() {

            while(_isRunning) {
                BOSyncEntity entity = getNextPendingUpload();

                if(entity == null) {
                    _isRunning = false;
                } else {
                    Log.d(TAG, "Starting upload of " + entity.toString());

                    // Upload, update or delete entity
                    boolean success = false;
                    BOSyncEntity.SyncState state = entity.getState();

                    // If the entity is supposed to be changed but has not been uploaded yet: Upload entity first
                    if(state != BOSyncEntity.SyncState.UPLOADING && !entity.hasRemoteId()) {
                        success = entity.updateOnServerSync(UploadService.this);

                        if(!success) {
                            continue;
                        }
                    }

                    switch(state) {
                        case UPLOADING:
                            success = entity.uploadToServerSync(UploadService.this);
                            break;
                        case UPDATING:
                            success = entity.updateOnServerSync(UploadService.this);
                            break;
                        case DELETING:
                            success = entity.deleteOnServerSync(UploadService.this);
                            break;
                    }

                    if(success && !entity.hasRemoteId()) {
                        Log.e(TAG, "Entity did not get a remote ID. Is the remote ID set correctly in " + entity.getClass().getSimpleName() + "'s uploading method?");
                        continue;
                    }

                    if(success) {
                        Log.d(TAG, state.toString() + " operation on server successful");

                        // Update local DB with new status
                        if(state == BOSyncEntity.SyncState.DELETING) {
                            entity.setState(BOSyncEntity.SyncState.DELETED);
                        } else {
                            entity.setState(BOSyncEntity.SyncState.NORMAL);
                        }

                        entity.save();

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
