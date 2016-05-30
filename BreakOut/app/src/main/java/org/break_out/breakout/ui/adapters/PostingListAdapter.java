package org.break_out.breakout.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.break_out.breakout.BOLocation;
import org.break_out.breakout.R;
import org.break_out.breakout.manager.MediaManager;
import org.break_out.breakout.model.BOMedia;
import org.break_out.breakout.sync.model.Posting;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Maximilian Duehr on 21.04.2016.
 */
public class PostingListAdapter extends RecyclerView.Adapter<PostingListAdapter.PostingViewHolder> {
    private static final String TAG = "PostingListAdapter";

    private ArrayList<Posting> _postingList;
    private Context _context;

    public PostingListAdapter(Context context, ArrayList<Posting> postingList) {
        _postingList = postingList;
        _context = context;
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
        TextView tvTime;
        TextView tvComment;
        RelativeLayout rlChallenge;
        TextView tvChallenge;
        ImageView ivChallenge;

        public PostingViewHolder(View itemView) {
            super(itemView);
            ivPosting = (ImageView) itemView.findViewById(R.id.posting_iv_image);
            civTeamPic = (CircleImageView) itemView.findViewById(R.id.posting_civ_teamPic);
            tvTeamName = (TextView) itemView.findViewById(R.id.posting_tv_teamName);
            tvTeamLocation = (TextView) itemView.findViewById(R.id.posting_tv_teamLocation);
            tvTime = (TextView) itemView.findViewById(R.id.posting_tv_time);
            tvComment = (TextView) itemView.findViewById(R.id.posting_tv_comment);
            rlChallenge = (RelativeLayout) itemView.findViewById(R.id.post_rl_challengeWrap);
            tvChallenge = (TextView) itemView.findViewById(R.id.post_tv_challenge);
            ivChallenge = (ImageView) itemView.findViewById(R.id.post_iv_trophy);
        }
    }

    private void populateView(final PostingViewHolder holder, int pos) {
        final Posting posting = _postingList.get(pos);

        String teamName = posting.getTeamName();
        holder.tvTeamName.setText(teamName);

        List<Address> addressList = null;
        Geocoder coder = new Geocoder(_context);
        BOLocation location = posting.getLocation();
        if(location != null) {
            try {
                addressList = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch(IOException e) {
                e.printStackTrace();
            }
            if(addressList != null) {
                if(addressList.size() > 0) {
                    setLocation(holder.tvTeamLocation, addressList.get(0));
                }

            }
        }

        if(posting.getCreatedTimestamp() != 0L) {
            holder.tvTime.setText(timeBuilder(posting.getCreatedTimestamp()));
        }

        if(!posting.getText().isEmpty()) {
            holder.tvComment.setText(posting.getText());
        }

        if(!posting.hasMedia()) {
            holder.ivPosting.setVisibility(View.GONE);
        } else {
            holder.ivPosting.setVisibility(View.VISIBLE);
            holder.ivPosting.setImageDrawable(_context.getResources().getDrawable(R.drawable.bg_welcome_600dp));
            if(posting.getMedia() != null) {
                if(posting.getMedia().isDownloaded()) {
                    if(MediaManager.getInstance().getFromCache(posting.getMedia().getUrl()) != null) {
                        holder.ivPosting.setImageBitmap(MediaManager.getInstance().getFromCache(posting.getMedia().getUrl()));
                    } else {
                        Bitmap imageBitmap = MediaManager.decodeSampledBitmapFromFile(posting.getMedia(), 250, 250);
                        MediaManager.getInstance().addToCache(posting.getMedia().getUrl(), imageBitmap);
                        holder.ivPosting.setImageBitmap(imageBitmap);

                    }
                } else {
                    MediaManager.loadMediaFromServer(posting.getMedia(), holder.ivPosting, BOMedia.SIZE.MEDIUM);
                }
            }
        }
        holder.civTeamPic.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_account_box_white_24dp));
        if(posting.getProfileImage()!=null) {
            if(!posting.getProfileImage().isDownloaded()) {
                MediaManager.loadMediaFromServer(posting.getProfileImage(), holder.civTeamPic, BOMedia.SIZE.SMALL);
            } else {
                if(MediaManager.getInstance().getFromCache(posting.getProfileImage().getUrl()) != null) {
                    holder.civTeamPic.setImageBitmap(MediaManager.getInstance().getFromCache(posting.getProfileImage().getUrl()));
                } else {
                    Bitmap profileImageBitmap = MediaManager.decodeSampledBitmapFromFile(posting.getProfileImage(), 75, 75);
                    MediaManager.getInstance().addToCache(posting.getProfileImage().getUrl(), profileImageBitmap);
                    holder.civTeamPic.setImageBitmap(profileImageBitmap);
                }
            }
        } else {
            holder.civTeamPic.setImageDrawable(_context.getResources().getDrawable(R.drawable.placeholder_profile_pic));
        }

        if(posting.getProvenChallengeId() != -1) {
            holder.rlChallenge.setVisibility(View.VISIBLE);
            holder.tvChallenge.setText(posting.getChallengeDescription());
        } else {
            holder.rlChallenge.setVisibility(View.GONE);
        }

        holder.tvTeamLocation.setText(posting.getLocationName());
        holder.tvTeamName.setText(teamName);
    }


    private void setLocation(TextView textView, Address currentAddress) {
        StringBuilder builder = new StringBuilder();
        builder.append(currentAddress.getLocality())
                .append(", ")
                .append(currentAddress.getCountryName());
        String location = builder.toString();
        textView.setText(location);
    }

    private String timeBuilder(long timestamp) {
        long curTime = System.currentTimeMillis();
        long dif = curTime - (timestamp * 1000);
        StringBuilder responseBuilder = new StringBuilder();
        int minutes = (int) (dif / 1000) / 60;
        int hours = 0;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp * 1000);

        for(long i = minutes; (i - 60) > 0; i -= 60) {
            hours++;
        }

        if(hours == 0) {
            if(Locale.getDefault().getISO3Language().contains("de")) {
                responseBuilder.append("vor ")
                        .append(minutes)
                        .append(" Minuten");
            } else {
                responseBuilder.append(minutes)
                        .append(" minutes")
                        .append(" ago");
            }
        } else if(hours < 24) {
            if(Locale.getDefault().getISO3Language().contains("de")) {
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
