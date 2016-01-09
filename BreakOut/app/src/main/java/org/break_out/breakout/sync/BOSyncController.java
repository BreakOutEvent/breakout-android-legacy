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

        Log.i("breakout", "[BOSyncController] Saved entity " + entity.toString());
        tryUploadAll();
        Log.i("breakout", "[BOSyncController] Called Service");
    }

    public void update(SyncEntity entity) {
        // Set sync state
        entity.setState(SyncEntity.SyncState.UPDATING);
        entity.save();
        notifyDataChangedListeners();

        Log.i("breakout", "[BOSyncController] Saved entity " + entity.toString());
        tryUploadAll();
        Log.i("breakout", "[BOSyncController] Called Service");
    }

    public <T extends SyncEntity> List<T> getAll(Class<T> type) {
        return SyncEntity.listAll(type);
    }

    public void delete(SyncEntity entity) {
        // Set sync state
        entity.setState(SyncEntity.SyncState.DELETING);
        entity.save();
        notifyDataChangedListeners();

        Log.i("breakout", "[BOSyncController] Saved entity " + entity.toString());
        tryUploadAll();
        Log.i("breakout", "[BOSyncController] Called Service");
    }

}
