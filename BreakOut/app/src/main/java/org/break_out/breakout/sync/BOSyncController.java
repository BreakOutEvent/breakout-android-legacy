package org.break_out.breakout.sync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.sync.model.BOSyncEntity;
import org.break_out.breakout.sync.service.DownloadService;
import org.break_out.breakout.sync.service.UpdateService;
import org.break_out.breakout.sync.service.UploadService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Tino on 07.01.2016.
 */
public class BOSyncController {

    private static final String TAG = "BOSyncController";

    private static BOSyncController _instance = null;

    private Map<Class<? extends BOSyncEntity>, BOEntityDownloader<? extends BOSyncEntity>> _entities = new HashMap<>();
    private Context _context = null;

    private Map<Class<? extends BOSyncEntity>, List<DataChangedListener>> _listeners = new HashMap<>();

    public interface DataChangedListener {
        public void dataChanged();
    }

    private BOSyncController(Context context) {
        _context = context;

        initEntities();
    }

    public static BOSyncController getInstance(Context context) {
        if(_instance == null) {
            _instance = new BOSyncController(context);
        }

        return _instance;
    }

    private void initEntities() {
        // Register all your entities here
        registerEntity(Posting.class, Posting.getDownloader());
    }

    private <T extends BOSyncEntity> void registerEntity(Class<T> type, BOEntityDownloader<T> entityLoader) {
        _entities.put(type, entityLoader);
    }

    public Set<Class<? extends BOSyncEntity>> getEntityClasses() {
        return _entities.keySet();
    }

    public <T extends BOSyncEntity> BOEntityDownloader<T> getDownloader(Class<T> type) {
        if(!_entities.keySet().contains(type)) {
            Log.e(TAG, "Cannot get the downloader for the entity type " + type.getSimpleName() + "! Have you registered this entity type?");
            return null;
        }

        return (BOEntityDownloader<T>) _entities.get(type);
    }

    public <T extends BOSyncEntity> void registerUploadListener(Class<T> entityType, DataChangedListener listener) {
        if(!_listeners.keySet().contains(entityType)) {
            _listeners.put(entityType, new ArrayList<DataChangedListener>());
        }

        List<DataChangedListener> typeListeners = _listeners.get(entityType);

        if(listener != null && !typeListeners.contains(listener)) {
            typeListeners.add(listener);
        }
    }

    public void unregisterListener(DataChangedListener listener) {
        if(listener == null) {
            return;
        }

        for(Class<? extends BOSyncEntity> key : _listeners.keySet()) {
            List<DataChangedListener> typeListeners = _listeners.get(key);

            if(typeListeners.contains(listener)) {
                typeListeners.remove(listener);
            }
        }
    }

    public void notifyDataChangedListeners(Class<? extends BOSyncEntity> type) {
        if(!_listeners.containsKey(type) || _listeners.get(type) == null) {
            return;
        }

        for(DataChangedListener listener : _listeners.get(type)) {
            listener.dataChanged();
        }
    }

    public void startUploadService() {
        Intent intent = new Intent(_context, UploadService.class);
        _context.startService(intent);
    }

    public void startDownloadService() {
        Intent intent = new Intent(_context, DownloadService.class);
        _context.startService(intent);
    }

    public void startUpdateService() {
        Intent intent = new Intent(_context, UpdateService.class);
        _context.startService(intent);
    }

    public void checkForNewEntities() {
        startUpdateService();
    }

    public void upload(BOSyncEntity entity) {
        // Set sync state
        entity.setState(BOSyncEntity.SyncState.UPLOADING);
        entity.save();
        notifyDataChangedListeners(entity.getClass());

        Log.d(TAG, "Saved entity " + entity.toString());
        startUploadService();
        Log.d(TAG, "Called Service");
    }

    public void update(BOSyncEntity entity) {
        // Set sync state
        entity.setState(BOSyncEntity.SyncState.UPDATING);
        entity.save();
        notifyDataChangedListeners(entity.getClass());

        Log.d(TAG, "Saved entity " + entity.toString());
        startUploadService();
        Log.d(TAG, "Called Service");
    }

    /**
     * Returns all items of the given entity type from the
     * <b>local database</b>.
     *
     * @param type The type of entity you want to receive
     * @param <T> The tpye of entity you want to receive
     * @return A list of all items of that entity type from the local database
     */
    public <T extends BOSyncEntity> List<T> getAll(Class<T> type) {
        return T.listAll(type);
    }

    public <T extends BOSyncEntity> List<T> get(Class<T> type, long fromId, long toId) {
        // Ensure that fromId < toId
        if(fromId > toId) {
            long tempId = fromId;
            fromId = toId;
            toId = tempId;
        }

        String[] idsToGet = new String[new Long(toId-fromId+1).intValue()];
        for(int i = 0; i < idsToGet.length; i++) {
            idsToGet[i] = "" + fromId + i;
        }

        List<T> localItems = T.findById(type, idsToGet);

        boolean needToStartDownload = false;

        // Set priorities for all items that are not downloaded yet
        for(T item : localItems) {
            if(item.isDownloading()) {
                item.setDownloadPriority(1);
                item.save();

                needToStartDownload = true;
            }
        }

        if(needToStartDownload) {
            startDownloadService();
        }

        return localItems;
    }

    public void delete(BOSyncEntity entity) {
        // Set sync state
        entity.setState(BOSyncEntity.SyncState.DELETING);
        entity.save();
        notifyDataChangedListeners(entity.getClass());

        Log.d(TAG, "Saved entity " + entity.toString());
        startUploadService();
        Log.d(TAG, "Called Service");
    }

}
