package org.break_out.breakout.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    }

    @Override
    public PostingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        PostingViewHolder viewHolder = new PostingViewHolder(v);

        return viewHolder;
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
        ImageView iv_posting;
        CircleImageView civ_teamPic;
        TextView tv_teamName;
        TextView tv_teamLocation;
        TextView tv_time;
        TextView tv_comment;

        public PostingViewHolder(View itemView) {
            super(itemView);
            iv_posting = (ImageView) itemView.findViewById(R.id.posting_iv_image);
            civ_teamPic = (CircleImageView) itemView.findViewById(R.id.posting_civ_teamPic);
            tv_teamName = (TextView) itemView.findViewById(R.id.posting_tv_teamName);
            tv_teamLocation = (TextView) itemView.findViewById(R.id.posting_tv_teamLocation);
            tv_time = (TextView) itemView.findViewById(R.id.posting_tv_time);
            tv_comment = (TextView) itemView.findViewById(R.id.posting_tv_comment);

        }
    }

    private void populateView(PostingViewHolder holder, int pos) {
        Posting posting = _postingList.get(pos);
        if(!posting.getText().isEmpty()) {
            holder.tv_comment.setText(posting.getText());
        }
        if(!posting.hasImage()) {
            holder.iv_posting.setVisibility(View.GONE);
        } else {
            holder.iv_posting.setVisibility(View.VISIBLE);
            //TODO: Set image
        }

    }
}
