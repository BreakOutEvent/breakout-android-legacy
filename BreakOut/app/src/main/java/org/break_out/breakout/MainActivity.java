package org.break_out.breakout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.sync.model.SyncEntity;
import org.break_out.breakout.sync.service.DownloadService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BOSyncController.DataChangedListener {

    private static final String TAG = "MainActivity";

    private BOSyncController _syncController;
    private TextView _tv;

    private String[] _messages = {"Hello world.", "This is a test.", "What's up?", "Test.", "Short text.", "How are you?", "Let's meet.", "Where are you?", "Great app!"};

    private List<Posting> _entities = new ArrayList<Posting>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _syncController = BOSyncController.getInstance(this);

        _tv = (TextView) findViewById(R.id.textview);

        // For testing the download:

        // First add test data
        addTestDataMissingPostings();

        // Then start download service
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);

        updateView();

        /*
        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Posting p = new Posting();
                p.setText(_messages[(int) (Math.random() * _messages.length)]);
                _syncController.upload(p);
            }
        });

        Button deleteButton = (Button) findViewById(R.id.clear_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_entities.size() > 0) {
                    _syncController.delete(_entities.get(0));
                }
            }
        });
        */
    }

    private void addTestDataMissingPostings() {
        Posting p1 = new Posting();
        p1.setId(0L);
        p1.setState(SyncEntity.SyncState.DOWNLOADING);
        p1.setDownloadPriority(1);
        p1.save();

        Posting p2 = new Posting();
        p2.setId(1L);
        p2.setState(SyncEntity.SyncState.DOWNLOADING);
        p2.setDownloadPriority(1);
        p2.save();

        Posting p3 = new Posting();
        p3.setId(2L);
        p3.setState(SyncEntity.SyncState.DOWNLOADING);
        p3.setDownloadPriority(1);
        p3.save();
    }

    private void updateView() {
        _entities = _syncController.getAll(Posting.class);

        String text = "";

        for(Posting p : _entities) {
            text += p.toString() + "\n";
        }

        _tv.setText(text);
    }

    @Override
    public void dataChanged() {
        updateView();
        Log.d(TAG, "Data change listener called. Refreshing view.");
    }

    @Override
    protected void onPause() {
        super.onPause();

        _syncController.unregisterListener(this);
        Log.d(TAG, "Unregistered listener");
    }

    @Override
    protected void onResume() {
        super.onResume();

        _syncController.registerUploadListener(Posting.class, this);
        Log.d(TAG, "Registered listener");
        updateView();
    }
}
