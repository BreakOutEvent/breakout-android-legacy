package org.break_out.breakout.sync.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.break_out.breakout.sync.BOEntityDownloader;
import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.BOSyncReceiver;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.sync.model.SyncEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * This Service will iterate over all entities returned by {@link BOSyncController#getEntityClasses()}
 * and download items that are still missing in the local database based on the internet connnection and
 * priorities.
 * <p/>
 * <br /><br />
 * <p/>
 * Created by Tino on 18.12.2015.
 */
public class DownloadService extends Service {

    private static final String TAG = "DownloadService";

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
                new DownloaderThread().run();
            }
        }).start();
    }

    private <T extends SyncEntity> List<Long> getIDsToDownload(Class<T> type) {
        // Try to get all prioritized entries
        List<Long> candidateIds = new ArrayList<Long>();

        List<Posting> candidatesPrioritized =
                Select.from(Posting.class)
                        .where(Condition.prop(SyncEntity.IS_DOWNLOADING).eq(1),
                                Condition.prop(SyncEntity.DOWNLOAD_PRIORITY).gt(0))
                        .list();

        // Get candidates with priority
        for(Posting candidate : candidatesPrioritized) {
            candidateIds.add(candidate.getId());
        }

        if(!candidateIds.isEmpty()) {
            Log.d(TAG, "Found " + type.getSimpleName() + " IDs to download: " + candidateIds);
            return candidateIds;
        }

        // TODO: Download entries that are not prioritized?

        return candidateIds;
    }

    private void notifyDataChanged(Class<? extends SyncEntity> type) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BOSyncReceiver.ACTION);
        broadcastIntent.putExtra(BOSyncReceiver.ENTITY_TYPE, type);
        sendBroadcast(broadcastIntent);

        Log.d(TAG, "Sent broadcast for entity type " + type.getSimpleName());
    }

    private class DownloaderThread extends Thread {

        @Override
        public void run() {
            BOSyncController controller = BOSyncController.getInstance(DownloadService.this);

            for(Class<? extends SyncEntity> type : controller.getEntityClasses()) {
                BOEntityDownloader<? extends SyncEntity> downloader = controller.getDownloader(type);
                List<? extends SyncEntity> items = downloader.download(getIDsToDownload(type));

                boolean downloadedSomething = false;

                for(SyncEntity item : items) {
                    downloadedSomething = true;

                    item.setState(SyncEntity.SyncState.NORMAL);
                    item.save();

                    Log.d(TAG, "Downloaded and saved " + item.toString());
                }

                if(downloadedSomething) {
                    notifyDataChanged(type);
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
