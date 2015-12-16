package org.break_out.breakout.sync.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.model.PostingsDatabaseHelper;
import org.break_out.breakout.sync.model.Posting;

import java.sql.SQLException;

/**
 * Created by Tino on 15.12.2015.
 */
public class PostingService extends IntentService {

    public static final String KEY_POSTING = "message";

    private Dao<Posting, Void> _dao = null;


    public PostingService() {
        super("PostingService");

        PostingsDatabaseHelper db = new PostingsDatabaseHelper(this);
        try {
            _dao = db.getDao();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int requestId = intent.getIntExtra(BOSyncController.KEY_REQUEST_ID, -1);

        if(requestId == -1) {
            Log.e("breakout", "You must pass a request ID to the Service (BOSyncController.KEY_REQUEST_ID)!");
            return;
        }

        Posting posting = (Posting) intent.getSerializableExtra(KEY_POSTING);
        posting.setSent(false);

        Log.i("breakout", "[Service] Start sending \"" + posting.getText() + "\"");

        SystemClock.sleep(10000);
        posting.setSent(true);

        Log.i("breakout", "[Service] Sent \"" + posting.getText() + "\"");

        try {
            _dao.update(posting);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        sendResult(requestId);
    }

    private void sendResult(int requestId) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BOSyncController.ACTION_RESP);
        //broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(BOSyncController.KEY_REQUEST_ID, requestId);
        sendBroadcast(broadcastIntent);

        Log.i("breakout", "[Service] Sent broadcast");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        return START_REDELIVER_INTENT;
    }
}
