package org.break_out.breakout.experimental;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.break_out.breakout.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tino on 20.02.2016.
 */
public class ExpPostingActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    private static final String TAG = "ExpPostingActivity";

    private ExpPostingLoader _loader = null;

    private ListView _listView = null;
    private ArrayAdapter<ExpPosting> _adapter = null;
    private List<ExpPosting> _items = new ArrayList<ExpPosting>();

    private boolean _isFirst = true;
    private int _minId = -1;
    private boolean _isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_posting);

        _listView = (ListView) findViewById(R.id.listview);

        _loader = new ExpPostingLoader(this);

        _adapter = new ArrayAdapter<ExpPosting>(this, android.R.layout.simple_list_item_1, _items);
        _listView.setAdapter(_adapter);
        _listView.setOnScrollListener(this);

        updateAdapter();
    }

    private void updateAdapter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ExpPosting> newItems;

                if(_isFirst) {
                    int lastKnown = _loader.loadInt(ExpPostingLoader.KEY_LAST_KNOWN_ID);
                    newItems = _loader.getPostings(lastKnown, lastKnown-9);

                    _minId = lastKnown - 10;
                    _isFirst = false;
                } else {
                    newItems = _loader.getPostings(_minId, _minId-9);

                    _minId -= 10;
                }

                for(int i = newItems.size()-1; i >= 0; i--) {
                    _items.add(newItems.get(i));
                }

                Log.d(TAG, _items.toString());

                _isLoading = false;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private ExpPosting getPostingWithId(int id, List<ExpPosting> postings) {
        for(ExpPosting p : postings) {
            if(p.getId() == id) {
                return p;
            }
        }

        return null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            if(!_isLoading) {
                _isLoading = true;
                updateAdapter();
            }
        }
    }
}
