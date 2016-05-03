package org.break_out.breakout.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import org.break_out.breakout.R;
import org.break_out.breakout.sync.model.Posting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Maximilian Duehr on 21.04.2016.
 */
public class PostingListAdapter extends RecyclerView.Adapter<PostingListAdapter.PostingViewHolder> {
    private static final String TAG = "PostingListAdapter";

    private ArrayList<Posting> _postingList;

    public PostingListAdapter(ArrayList<Posting> postingList) {
        _postingList = postingList;
    }

    @Override
    public PostingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new PostingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostingViewHolder holder, int position) {
        populateView(holder, position);
    }

    @Override
    public int getItemCount() {
        return _postingList.size();
    }

    public static class PostingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPosting;
        CircleImageView civTeamPic;
        TextView tvTeamName;
        TextView tvTeamLocation;
        RelativeTimeTextView tv_time;
        TextView tvComment;

        public PostingViewHolder(View itemView) {
            super(itemView);
            ivPosting = (ImageView) itemView.findViewById(R.id.posting_iv_image);
            civTeamPic = (CircleImageView) itemView.findViewById(R.id.posting_civ_teamPic);
            tvTeamName = (TextView) itemView.findViewById(R.id.posting_tv_teamName);
            tvTeamLocation = (TextView) itemView.findViewById(R.id.posting_tv_teamLocation);
            tv_time = (RelativeTimeTextView) itemView.findViewById(R.id.posting_tv_time);
            tvComment = (TextView) itemView.findViewById(R.id.posting_tv_comment);

        }
    }

    private void populateView(PostingViewHolder holder, int pos) {
        Posting posting = _postingList.get(pos);

        if (posting.getCreatedTimestamp() != 0L) {
            holder.tv_time.setText(timeBuilder(posting.getCreatedTimestamp()));
        }

        if (!posting.getText().isEmpty()) {
            holder.tvComment.setText(posting.getText());
        }

        if (!posting.hasImage()) {
            holder.ivPosting.setVisibility(View.GONE);
        } else {
            holder.ivPosting.setVisibility(View.VISIBLE);
            //TODO: Set image
        }
    }

    private String timeBuilder(long timestamp) {
        long curTime = System.currentTimeMillis();
        long dif = curTime - (timestamp * 1000);
        StringBuilder responseBuilder = new StringBuilder();
        int minutes = (int) (dif / 1000) / 60;
        int hours = 0;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp * 1000);

        for (long i = minutes; (i - 60) > 0; i -= 60) {
            hours++;
        }

        if (hours == 0) {
            if (Locale.getDefault().getISO3Language().contains("de")) {
                responseBuilder.append("vor ")
                        .append(minutes)
                        .append(" Minuten");
            } else {
                responseBuilder.append(minutes)
                        .append(" minutes")
                        .append(" ago");
            }
        } else if (hours < 24) {
            if (Locale.getDefault().getISO3Language().contains("de")) {
                responseBuilder.append("vor ")
                        .append(hours)
                        .append(" Stunden");
            } else {
                responseBuilder.append(hours)
                        .append(" hours")
                        .append(" ago");
            }
        } else {
            responseBuilder.append(c.get(Calendar.DAY_OF_MONTH) + 1)
                    .append(".")
                    .append(c.get(Calendar.MONTH) + 1)
                    .append(".")
                    .append(c.get(Calendar.YEAR));
        }


        return responseBuilder.toString();
    }
}
