package org.break_out.breakout.sync.service;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.break_out.breakout.sync.BOSyncReceiver;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.sync.model.PostingsDatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tino on 18.12.2015.
 */
public class UploaderService extends Service {

    private Dao<Posting, Void> _dao = null;
    private boolean _isRunning = false;


    public UploaderService() {
        super();

        PostingsDatabaseHelper db = new PostingsDatabaseHelper(this);
        try {
            _dao = db.getDao();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("breakout", "[UploaderService] START COMMAND");

        startUploading();
        return START_STICKY;
    }

    private void startUploading() {

        if(_isRunning) {
            return;
        }

        _isRunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                new UploaderThread().run();
            }
        }).start();
    }

    private Posting getNextPendingUpload() {
        try {
            List<Posting> postings = _dao.query(_dao.queryBuilder().limit(1L).where().eq(Posting.COLUMN_SENT, false).prepare());
            if(postings.size() == 1) {
                return postings.get(0);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void sendResult() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BOSyncReceiver.ACTION);
        sendBroadcast(broadcastIntent);

        Log.i("breakout", "[UploaderService] Sent broadcast");
    }

    private class UploaderThread extends Thread {

        @Override
        public void run() {

            while(_isRunning) {
                Posting posting = getNextPendingUpload();

                if(posting == null) {
                    _isRunning = false;
                } else {
                    Log.i("breakout", "[UploaderService$UploaderThread] Starting upload of \"" + posting.getText() + "\"");

                    // Upload post
                    try {
                        Thread.sleep(4000);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update local DB
                    posting.setSent(true);
                    try {
                        _dao.update(posting);
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }

                    // Send broadcast
                    sendResult();

                    Log.i("breakout", "[UploaderService$UploaderThread] > Finished upload of \"" + posting.getText() + "\"");
                }
            }

            _isRunning = false;
            stopSelf();
            Log.i("breakout", "[UploaderService$UploaderThread] STOPPED SERVICE");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
