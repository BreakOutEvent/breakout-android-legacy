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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Maximilian Duehr on 21.04.2016.
 */
public class PostingListAdapter extends RecyclerView.Adapter<PostingListAdapter.PostingViewHolder> {
    
    private ArrayList<Posting> _postingList;

    public PostingListAdapter(ArrayList<Posting> postingList) {
        _postingList = postingList;

        // FIXME: Test data
        Posting p1 = new Posting();
        p1.setText("post nummer 1");
        _postingList.add(p1);

        Posting p2 = new Posting();
        p2.setText("a short test lol");
        _postingList.add(p2);
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

        if(posting.getCreatedTimestamp() != 0L) {
            holder.tv_time.setReferenceTime(posting.getCreatedTimestamp() * 60);
        }

        if(!posting.getText().isEmpty()) {
            holder.tvComment.setText(posting.getText());
        }

        if(!posting.hasImage()) {
            holder.ivPosting.setVisibility(View.GONE);
        } else {
            holder.ivPosting.setVisibility(View.VISIBLE);
            //TODO: Set image
        }

    }
}
