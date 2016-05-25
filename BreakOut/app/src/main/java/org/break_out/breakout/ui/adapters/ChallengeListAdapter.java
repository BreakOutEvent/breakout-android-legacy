package org.break_out.breakout.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.break_out.breakout.R;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_challenge, parent, false);
        return new ChallengeViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return _challengeList.size();
    }

    @Override
    public void onBindViewHolder(ChallengeViewHolder holder, int position) {
        Challenge curChallenge = _challengeList.get(position);
        holder.tv_title.setText(generateTitle(curChallenge));
        holder.tv_description.setText(curChallenge.getDescription());

    }

    private static String generateTitle(Challenge challenge) {
        StringBuilder builder = new StringBuilder();
        builder.append(challenge.getAmount())
                .append("€");
        return builder.toString();
    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_description;
        public ChallengeViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.challenge_title);
            tv_description = (TextView) itemView.findViewById(R.id.challenge_description);
        }
    }
}
