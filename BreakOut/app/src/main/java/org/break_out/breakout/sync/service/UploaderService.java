package org.break_out.breakout.sync.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.BOSyncReceiver;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.sync.model.SyncEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tino on 18.12.2015.
 */
public class UploaderService extends Service {

    private boolean _isRunning = false;
    private List<SyncEntity> _entitiesToUpload = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("breakout", "[UploaderService] --- START COMMAND ---");

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

    private SyncEntity getNextPendingUpload() {
        if(_entitiesToUpload == null) {
            _entitiesToUpload = new ArrayList<SyncEntity>();

            List<Class<? extends SyncEntity>> entityClasses = BOSyncController.getInstance(this).getEntityClasses();
            for(Class<? extends SyncEntity> entityClass : entityClasses) {
                _entitiesToUpload.addAll(SyncEntity.find(entityClass, SyncEntity.IS_UPLOADING_NAME + " = ?", "1"));
            }
        }

        if(_entitiesToUpload.size() > 0) {
            SyncEntity entity = _entitiesToUpload.get(0);
            _entitiesToUpload.remove(entity);

            return entity;
        }

        return null;
    }

    private void sendResult() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BOSyncReceiver.ACTION);
        sendBroadcast(broadcastIntent);

        Log.i("breakout", "[UploaderService] Sent broadcast");
    }

    private class UploaderThread extends Thread {

        @Override
        public void run() {

            while(_isRunning) {
                SyncEntity entity = getNextPendingUpload();

                if(entity == null) {
                    _isRunning = false;
                } else {
                    Log.i("breakout", "[UploaderService] Starting upload of \"" + entity.toString() + "\"");

                    // Upload entity
                    boolean success = entity.uploadToServer();

                    if(success) {
                        Log.i("breakout", "[UploaderService] > Upload successful");

                        // Update local DB
                        entity.setState(SyncEntity.SyncState.NORMAL);
                        entity.save();

                        // Send broadcast
                        sendResult();
                    } else {
                        Log.i("breakout", "[UploaderService] > Upload failed");
                    }
                }
            }

            _isRunning = false;
            _entitiesToUpload = null;
            stopSelf();
            Log.i("breakout", "[UploaderService] --- STOPPED SERVICE ---");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
