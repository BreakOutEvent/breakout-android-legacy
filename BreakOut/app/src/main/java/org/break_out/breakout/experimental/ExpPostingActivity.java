package org.break_out.breakout.experimental;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import org.break_out.breakout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tino on 20.02.2016.
 */
public class ExpPostingActivity extends AppCompatActivity {

    private static final String TAG = "ExpPostingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_posting);

        final TextView tv = (TextView) findViewById(R.id.textview);

        final ExpPostingLoader loader = new ExpPostingLoader(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //loader.updateMissingIdsFromServer();
                //loader.getPostings(loader.loadInt(ExpPostingLoader.KEY_LAST_KNOWN_ID), loader.loadInt(ExpPostingLoader.KEY_LAST_KNOWN_ID)-9);
                loader.getPostings(10, 20);

                int lastKnown = loader.loadInt(ExpPostingLoader.KEY_LAST_KNOWN_ID);
                List<Integer> missing = loader.loadMissingIdsArr();

                List<ExpPosting> existingPostings = ExpPosting.listAll(ExpPosting.class);
                List<Integer> existingIds = new ArrayList<Integer>();
                for(ExpPosting p : existingPostings) {
                    existingIds.add(p.getId().intValue());
                }

                String text = "";
                for(int i = 0; i <= lastKnown; i++) {
                    if(missing.contains(i) && !existingIds.contains(i)) {
                        text += "<font color=red>[" + i + "]</font><br />";
                    } else if(existingIds.contains(i)) {
                        text += "<font color=green>" + getPostingWithId(i, existingPostings).toString() + "</font><br />";
                    } else {
                        text += "[" + i + "] Not missing but <b>should be missing</b><br />";
                    }
                }

                final String html = text;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(Html.fromHtml(html));
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

}
