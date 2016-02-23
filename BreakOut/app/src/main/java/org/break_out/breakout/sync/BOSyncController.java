package org.break_out.breakout.sync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.sync.model.SyncEntity;
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

    private Map<Class<? extends SyncEntity>, BOEntityDownloader<? extends SyncEntity>> _entities = new HashMap<>();
    private Context _context = null;

    private List<DataChangedListener> _listeners = new ArrayList<DataChangedListener>();

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

    private <T extends SyncEntity> void registerEntity(Class<T> type, BOEntityDownloader<T> entityLoader) {
        _entities.put(type, entityLoader);
    }

    public Set<Class<? extends SyncEntity>> getEntityClasses() {
        return _entities.keySet();
    }

    public <T extends SyncEntity> BOEntityDownloader<T> getDownloader(Class<T> type) {
        if(!_entities.keySet().contains(type)) {
            Log.e(TAG, "Cannot get the downloader for the entity type " + type.getSimpleName() + "! Have you registered this entity type?");
            return null;
        }

        return (BOEntityDownloader<T>) _entities.get(type);
    }

    public void registerUploadListener(DataChangedListener listener) {
        // TODO: Make it possible to register listeners only for a certain entity class

        if(listener != null && !_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void unregisterListener(DataChangedListener listener) {
        if(listener != null && _listeners.contains(listener)) {
            _listeners.remove(listener);
        }
    }

    public void notifyDataChangedListeners() {
        for(DataChangedListener listener : _listeners) {
            listener.dataChanged();
        }
    }

    public void tryUploadAll() {
        Intent intent = new Intent(_context, UploadService.class);
        _context.startService(intent);
    }

    public void upload(SyncEntity entity) {
        // Set sync state
        entity.setState(SyncEntity.SyncState.UPLOADING);
        entity.save();
        notifyDataChangedListeners();

        Log.d(TAG, "Saved entity " + entity.toString());
        tryUploadAll();
        Log.d(TAG, "Called Service");
    }

    public void update(SyncEntity entity) {
        // Set sync state
        entity.setState(SyncEntity.SyncState.UPDATING);
        entity.save();
        notifyDataChangedListeners();

        Log.d(TAG, "Saved entity " + entity.toString());
        tryUploadAll();
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
    public <T extends SyncEntity> List<T> getAll(Class<T> type) {
        return T.listAll(type);
    }

    public void delete(SyncEntity entity) {
        // Set sync state
        entity.setState(SyncEntity.SyncState.DELETING);
        entity.save();
        notifyDataChangedListeners();

        Log.d(TAG, "Saved entity " + entity.toString());
        tryUploadAll();
        Log.d(TAG, "Called Service");
    }

}
