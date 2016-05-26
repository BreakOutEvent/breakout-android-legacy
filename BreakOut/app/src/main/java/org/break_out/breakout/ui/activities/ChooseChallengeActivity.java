package org.break_out.breakout.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.ChallengeManager;
import org.break_out.breakout.model.Challenge;
import org.break_out.breakout.ui.adapters.ChallengeListAdapter;

import java.util.ArrayList;

public class ChooseChallengeActivity extends AppCompatActivity {
    private static final String TAG = "ChooseChallengeActivity";
    public static final String RESULT_ID = "result_id";
    private RecyclerView _recyclerView;
    private ChallengeListAdapter _adapter;
    private ArrayList<Challenge> _challenges;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_challenge);
        _recyclerView = (RecyclerView) findViewById(R.id.chooseChallenge_rv) ;
        _challenges = new ArrayList<>();
        _challenges.addAll(ChallengeManager.getInstance().getAllChallenges());
        _adapter = new ChallengeListAdapter(this,_challenges,new OnItemClickListener());
        _recyclerView.setAdapter(_adapter);
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateAndShowChallenges();
    }

    private void updateAndShowChallenges() {
        final ChallengeManager manager = ChallengeManager.getInstance();
        manager.fetchChallenges(this,new ChallengeManager.ChallengesFetchedListener() {
            @Override
            public void onChallengesFetched() {
                updateList();
            }
        });
    }

    private void updateList() {
        _challenges.clear();
        _challenges.addAll(ChallengeManager.getInstance().getAllChallenges());
        _adapter.notifyDataSetChanged();
    }

    public class OnItemClickListener {
        public void onItemClick(int id) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(RESULT_ID,id);
            setResult(RESULT_OK,resultIntent);
            finish();
        }
    }
}
