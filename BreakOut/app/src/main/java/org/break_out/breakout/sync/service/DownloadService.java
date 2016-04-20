package org.break_out.breakout.sync.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.break_out.breakout.sync.BOEntityDownloader;
import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.BOSyncReceiver;
import org.break_out.breakout.sync.model.BOSyncEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * This Service will iterate over all entities returned by {@link BOSyncController#getEntityClasses()}
 * and download items that are still missing in the local database based on the internet connection and
 * priorities.
 * <br /><br />
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

    /**
     * Returns a list of ID pairs of items that should be downloaded.<br />
     * The first ID will be the local ID of the entry, the second ID the remote ID.
     *
     * @param type The entity type
     * @param <T> The class of the entity
     * @return A list of pairs of IDs (local ID, remote ID)
     */
    private <T extends BOSyncEntity> List<Pair<Long, Long>> getIDsToDownload(Class<T> type) {
        // Try to get all prioritized entries

        // First ID: local ID
        // Second ID: remote
        List<Pair<Long, Long>> candidateIds = new ArrayList<Pair<Long, Long>>();

        List<T> candidatesPrioritized =
                Select.from(type)
                        .where(Condition.prop(BOSyncEntity.COLUMN_IS_DOWNLOADING).eq(1),
                                Condition.prop(BOSyncEntity.COLUMN_DOWNLOAD_PRIORITY).eq(BOSyncEntity.PRIORITY_HIGH))
                        .list();

        // Get candidates with priority
        for(T candidate : candidatesPrioritized) {
            candidateIds.add(new Pair<Long, Long>(candidate.getId(), candidate.getRemoteId()));
        }

        if(!candidateIds.isEmpty()) {
            return candidateIds;
        }

        // TODO: Add IDs with another priority or items that are invalid and have to be updated

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

                List<Pair<Long, Long>> idsToDownload = getIDsToDownload(type);

                List<Long> localIds = new ArrayList<Long>();
                List<Long> remoteIds = new ArrayList<Long>();

                for(Pair<Long, Long> idPair : idsToDownload) {
                    localIds.add(idPair.first);
                    remoteIds.add(idPair.second);
                }

                List<? extends BOSyncEntity> items = downloader.downloadSync(DownloadService.this, remoteIds);

                boolean downloadedSomething = false;

                for(BOSyncEntity item : items) {
                    if(!item.hasRemoteId()) {
                        Log.e(TAG, "Downloaded item has no remote ID! Does " + downloader.getClass().getSimpleName() + " set the remote ID correctly in downloadSync(...)?");
                        continue;
                    }

                    downloadedSomething = true;

                    item.setId(localIds.get(remoteIds.indexOf(item.getRemoteId())));
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
