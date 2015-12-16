package org.break_out.breakout.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import org.break_out.breakout.sync.model.PostingsDatabaseHelper;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.sync.services.PostingService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tino on 16.12.2015.
 */
public class BOSyncController {

    public static final String KEY_REQUEST_ID = "request_id";
    public static final String ACTION_RESP = "org.break_out.posting.ACTION_RESPONSE";

    private Context _context = null;
    private Dao<Posting, Void> _dao = null;

    private static int _nextRequestId = 0;

    private static Map<Integer, PostingCallback> _callbacks = null;


    public interface PostingCallback {
        public void postingRequestDone();
    }


    public BOSyncController(Context context) {
        _context = context;

        PostingsDatabaseHelper db = new PostingsDatabaseHelper(_context);
        try {
            _dao = db.getDao();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        _callbacks = new HashMap<Integer, PostingCallback>();
    }

    private int generateRequestId() {
        return _nextRequestId++;
    }

    public void uploadPosting(Posting posting, PostingCallback callback) {
        if(_dao == null) {
            return;
        }

        // Prepare callback and request ID
        int requestId = generateRequestId();
        Log.i("breakout", "[BOSyncController] Request ID: " + requestId);

        if(callback != null) {
            _callbacks.put(requestId, callback);
        }

        // Prepare and send post
        posting.setSent(false);

        try {
            _dao.create(posting);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(_context, PostingService.class);
        intent.putExtra(KEY_REQUEST_ID, requestId);
        intent.putExtra(PostingService.KEY_POSTING, posting);
        _context.startService(intent);
    }

    public List<Posting> getAllPostings() {
        try {
            return _dao.queryForAll();
        } catch(SQLException e) {
            e.printStackTrace();

            return new ArrayList<Posting>();
        }
    }

    /**
     * Receiving results from the {@link PostingService}.<br />
     * This class <b>has to be static</b>! Otherwise it cannot be initiated.
     */
    public static class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("breakout", "[BroadcastReceiver] onReceive()");

            int requestId = intent.getIntExtra(BOSyncController.KEY_REQUEST_ID, -1);

            if(requestId != -1 && _callbacks != null && _callbacks.keySet().contains(requestId)) {
                // Get callback for request ID
                PostingCallback callback = _callbacks.get(requestId);

                // Invoke callback
                if(callback != null) {
                    callback.postingRequestDone();
                }
            }
        }
    }
}
