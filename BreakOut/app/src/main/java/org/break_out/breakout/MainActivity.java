package org.break_out.breakout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.sync.model.BOSyncEntity;

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

        updateView();

        _syncController.checkForNewEntities();

        final EditText etFromId = (EditText) findViewById(R.id.et_from_id);
        final EditText etToId = (EditText) findViewById(R.id.et_to_id);

        Button addButton = (Button) findViewById(R.id.bt_request);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int from = Integer.parseInt(etFromId.getText().toString());
                int to = Integer.parseInt(etToId.getText().toString());

                _syncController.get(Posting.class, from, to);
            }
        });
    }

    private void updateView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _entities = _syncController.getAll(Posting.class);

                String text = "";

                for(Posting p : _entities) {
                    text += p.toString() + "\n";
                }

                final String finalText = text;

                _tv.post(new Runnable() {
                    @Override
                    public void run() {
                        _tv.setText(finalText);
                    }
                });
            }
        }).start();
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
