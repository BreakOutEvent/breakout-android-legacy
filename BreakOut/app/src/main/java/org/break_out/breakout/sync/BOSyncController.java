package org.break_out.breakout.sync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.sync.model.SyncEntity;
import org.break_out.breakout.sync.service.UploaderService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tino on 07.01.2016.
 */
public class BOSyncController {

    private static final String TAG = "BOSyncController";

    private static BOSyncController _instance = null;

    private List<Class<? extends SyncEntity>> _entities = new ArrayList<>();
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
        // The order of the entities represents the priority (first -> most important)

        // Highest priority
        _entities.add(Posting.class);
        // Lowest priority
    }

    public List<Class<? extends SyncEntity>> getEntityClasses() {
        return _entities;
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
        Intent intent = new Intent(_context, UploaderService.class);
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

    public <T extends SyncEntity> List<T> getAll(Class<T> type) {
        return SyncEntity.listAll(type);
    }

    /**
     * Call this method to get all entities with IDs within a certain range (between
     * first and last). The loader is an implementation of {@link BOEntityLoader} and implements
     * the actual download and DB access for the entity. For hints on how to implement it see {@link BOEntityLoader}.
     *
     * @param first The first ID in the range you want to get
     * @param last The last ID in the range you want to get
     * @param loader A loader handling the loading of the specific entity
     * @param <T> Your entities
     * @return
     */
    public <T extends SyncEntity> List<T> get(int first, int last, BOEntityLoader<T> loader) {
        // TODO
        return loader.load(first, last);
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
