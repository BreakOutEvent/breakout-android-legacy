package org.break_out.breakout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.model.Posting;

public class MainActivity extends AppCompatActivity implements BOSyncController.UploadListener {

    private BOSyncController _syncController;
    private TextView _tv;

    private String[] _messages = {"Hello world.", "This is a test.", "What's up?", "Test.", "Short text.", "How are you?", "Let's meet.", "Where are you?", "Great app!"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _syncController = BOSyncController.getInstance(this);

        _tv = (TextView) findViewById(R.id.textview);

        updateView();

        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Posting p = new Posting();
                p.setText(_messages[(int) (Math.random()*_messages.length)]);
                _syncController.upload(p, new BOSyncController.UploadListener() {
                    @Override
                    public void uploadStateChanged() {
                        updateView();
                    }
                });
                updateView();
            }
        });

        Button clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // TEST AREA
        Log.v("breakout", "Done.");
        // End TEST AREA
    }

    private void updateView() {
        String text = "";

        for(Posting p : _syncController.getAll(Posting.class)) {
            text += p.toString() + "\n";
        }

        _tv.setText(text);
    }

    @Override
    public void uploadStateChanged() {
        updateView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _syncController.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _syncController.registerUploadListener(this);
    }
}
