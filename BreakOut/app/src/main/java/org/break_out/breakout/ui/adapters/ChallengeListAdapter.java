package org.break_out.breakout.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.break_out.breakout.model.Challenge;

import java.util.ArrayList;

/**
 * Created by Maximilian Duehr on 25.05.2016.
 */
public class ChallengeListAdapter extends RecyclerView.Adapter<ChallengeListAdapter.ChallengeViewHolder> {
    private Context _context;
    private ArrayList<Challenge> _challengeList;

    public ChallengeListAdapter(Context c, ArrayList<Challenge> challengeList) {
        _context = c;
        _challengeList = challengeList;
    }
    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onBindViewHolder(ChallengeViewHolder holder, int position) {

    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        public ChallengeViewHolder(View itemView) {
            super(itemView);
        }
    }
}
