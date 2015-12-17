package org.break_out.breakout.sync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.sync.model.PostingsDatabaseHelper;
import org.break_out.breakout.sync.services.PostingService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tino on 17.12.2015.
 */
public class BOSyncControllerPosting {

    private static BOSyncControllerPosting _instance = null;

    private Context _context = null;
    private Dao<Posting, Void> _dao = null;

    private List<UploadListener> _listeners = null;


    public interface UploadListener {
        public void uploadStateChanged();
    }

    private BOSyncControllerPosting(Context context) {
        _context = context;

        PostingsDatabaseHelper db = new PostingsDatabaseHelper(_context);
        try {
            _dao = db.getDao();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        _listeners = new ArrayList<UploadListener>();
    }

    public static BOSyncControllerPosting getInstance(Context context) {
        if(_instance == null) {
            _instance = new BOSyncControllerPosting(context);
        }

        return _instance;
    }

    public void registerUploadListener(UploadListener listener) {
        if(listener != null && !_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void unregisterListener(UploadListener listener) {
        if(listener != null && _listeners.contains(listener)) {
            _listeners.remove(listener);
        }
    }

    public void notifyAllUploadListeners() {
        for(UploadListener listener : _listeners) {
            listener.uploadStateChanged();
        }
    }

    private void addPostingToLocalDB(Posting posting) {
        try {
            _dao.create(posting);
            notifyAllUploadListeners();

            Log.i("breakout", "[BOSyncControllerPosting] Posting now in local DB");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void uploadPosting(Posting posting, UploadListener listener) {
        if(_dao == null) {
            Log.e("breakout", "DAO object is null! Cannot run uploadPosting().");
            return;
        }

        registerUploadListener(listener);

        // Prepare and send post
        posting.setSent(false);
        addPostingToLocalDB(posting);

        Intent intent = new Intent(_context, PostingService.class);
        intent.putExtra(PostingService.KEY_POSTING, posting);
        _context.startService(intent);

        Log.i("breakout", "[BOSyncControllerPosting] Called Service");
    }

    public List<Posting> getAllPostings() {
        if(_dao != null) {
            try {
                return _dao.queryForAll();
            } catch(SQLException e) {
                e.printStackTrace();

                Log.i("breakout", "[BOSyncControllerPosting] getAllPostings() cannot executed: SQLException");
            }
        }

        return new ArrayList<Posting>();
    }

}
