package org.break_out.breakout.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.break_out.breakout.R;
import org.break_out.breakout.model.Challenge;
import org.break_out.breakout.ui.activities.ChooseChallengeActivity;

import java.util.ArrayList;

/**
 * Created by Maximilian Duehr on 25.05.2016.
 */
public class ChallengeListAdapter extends RecyclerView.Adapter<ChallengeListAdapter.ChallengeViewHolder> {
    private static final String TAG = "ChallengeListAdapter";
    private Context _context;
    private ArrayList<Challenge> _challengeList;
    private static ChooseChallengeActivity.OnItemClickListener _listener;

    public ChallengeListAdapter(Context c, ArrayList<Challenge> challengeList, ChooseChallengeActivity.OnItemClickListener listener) {
        _context = c;
        _challengeList = challengeList;
        _listener = listener;
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
        final Challenge curChallenge = _challengeList.get(position);
        holder.tv_title.setText(generateTitle(curChallenge));
        holder.tv_description.setText(curChallenge.getDescription());
        holder.ll_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onItemClick(curChallenge.getRemoteID());
            }
        });

    }

    private static String generateTitle(Challenge challenge) {
        StringBuilder builder = new StringBuilder();
        builder.append(challenge.getAmount())
                .append("€");
        return builder.toString();
    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_card;
        TextView tv_title;
        TextView tv_description;
        public ChallengeViewHolder(View itemView) {
            super(itemView);
            ll_card = (LinearLayout) itemView.findViewById(R.id.challenge_ll);
            tv_title = (TextView) itemView.findViewById(R.id.challenge_title);
            tv_description = (TextView) itemView.findViewById(R.id.challenge_description);
        }
    }

}
