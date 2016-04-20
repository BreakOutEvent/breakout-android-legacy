package org.break_out.breakout.sync.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.break_out.breakout.sync.BOEntityDownloader;
import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.BOSyncReceiver;
import org.break_out.breakout.sync.model.BOSyncEntity;

import java.util.List;

/**
 * Created by Tino on 18.12.2015.
 */
public class UpdateService extends Service {

    private static final String TAG = "UpdateService";

    private boolean _isRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "--- START COMMAND ---");

        startDownloading();
        return START_STICKY;
    }

    private void startDownloading() {

        if(_isRunning) {
            return;
        }

        _isRunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                new UpdaterThread().run();
            }
        }).start();
    }

    private void notifyDataChanged(Class<? extends BOSyncEntity> type) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BOSyncReceiver.ACTION);
        broadcastIntent.putExtra(BOSyncReceiver.ENTITY_TYPE, type);
        broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(broadcastIntent);

        Log.d(TAG, "Sent broadcast for entity type " + type.getSimpleName());
    }

    private class UpdaterThread extends Thread {

        private BOSyncController controller = null;

        @Override
        public void run() {
            controller = BOSyncController.getInstance(UpdateService.this);

            for(Class<? extends BOSyncEntity> type : controller.getEntityClasses()) {
                downloadAndSaveNewIDs(type);
            }

            stopSelf();

            Log.d(TAG, "--- STOPPED SERVICE ---");
        }

        private <T extends BOSyncEntity> void downloadAndSaveNewIDs(Class<T> type) {
            BOEntityDownloader<? extends BOSyncEntity> downloader = controller.getDownloader(type);

            long lastKnownId = -1;
            List<T> lastEntityList = T.findWithQuery(type, "SELECT * FROM " + type.getSimpleName() + " ORDER BY " + BOSyncEntity.COLUMN_REMOTE_ID + " DESC LIMIT 1");
            if(lastEntityList != null && !lastEntityList.isEmpty()) {
                T lastEntity = lastEntityList.get(0);
                if(lastEntity != null && lastEntity.hasRemoteId()) {
                    lastKnownId = lastEntity.getRemoteId();
                }
            }

            List<Long> newIds = downloader.downloadNewIDsSync(UpdateService.this, lastKnownId);

            boolean somethingChanged = false;

            // Generate and save one empty entity for every ID
            for(Long id : newIds) {
                T emptyEntity = null;
                try {
                    emptyEntity = type.getDeclaredConstructor().newInstance();
                } catch(Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Could not call the empty constructor for the entity " + type.getSimpleName());
                }

                if(emptyEntity != null) {
                    emptyEntity.setRemoteId(id);
                    emptyEntity.setState(BOSyncEntity.SyncState.DOWNLOADING);
                    emptyEntity.save();

                    somethingChanged = true;

                    Log.d(TAG, "Saved empty entity: " + emptyEntity.toString());
                }
            }

            if(somethingChanged) {
                notifyDataChanged(type);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
