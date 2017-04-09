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
    private ArrayList<Challenge> _challengeList;
    private Context _context;
    private static ChooseChallengeActivity.OnItemClickListener _listener;

    public ChallengeListAdapter(Context context,ArrayList<Challenge> challengeList, ChooseChallengeActivity.OnItemClickListener listener) {
        _context = context;
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


        if(!curChallenge.getState().equals(Challenge.STATE.ACCEPTED)) {
            holder.ll_card.setClickable(false);
            holder.ll_card.setBackgroundColor(_context.getResources().getColor(R.color.black_transparent_25));
        } else {
            holder.ll_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _listener.onItemClick(curChallenge.getRemoteID());
                }
            });
        }

    }

    private static String generateTitle(Challenge challenge) {
        StringBuilder builder = new StringBuilder();
        builder.append(challenge.getAmount())
                .append("€");
        String title = builder.toString();
        Log.d(TAG,"generate title: "+title);
        return title;
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
