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
import org.break_out.breakout.sync.model.BOSyncEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * This Service will iterate over all entities returned by {@link BOSyncController#getEntityClasses()}
 * and download items that are still missing in the local database based on the internet connection and
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

    private <T extends BOSyncEntity> List<Long> getIDsToDownload(Class<T> type) {
        // Try to get all prioritized entries
        List<Long> candidateIds = new ArrayList<Long>();

        List<Posting> candidatesPrioritized =
                Select.from(Posting.class)
                        .where(Condition.prop(BOSyncEntity.IS_DOWNLOADING_COLUMN).eq(1),
                                Condition.prop(BOSyncEntity.DOWNLOAD_PRIORITY_COLUMN).gt(0))
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

    private void notifyDataChanged(Class<? extends BOSyncEntity> type) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BOSyncReceiver.ACTION);
        broadcastIntent.putExtra(BOSyncReceiver.ENTITY_TYPE, type);
        broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(broadcastIntent);

        Log.d(TAG, "Sent broadcast for entity type " + type.getSimpleName());
    }

    private class DownloaderThread extends Thread {

        @Override
        public void run() {
            BOSyncController controller = BOSyncController.getInstance(DownloadService.this);

            for(Class<? extends BOSyncEntity> type : controller.getEntityClasses()) {
                BOEntityDownloader<? extends BOSyncEntity> downloader = controller.getDownloader(type);
                List<? extends BOSyncEntity> items = downloader.downloadSync(getIDsToDownload(type));

                boolean downloadedSomething = false;

                for(BOSyncEntity item : items) {
                    downloadedSomething = true;

                    item.setState(BOSyncEntity.SyncState.NORMAL);
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
