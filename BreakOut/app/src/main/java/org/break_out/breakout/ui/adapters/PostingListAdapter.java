package org.break_out.breakout.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.break_out.breakout.R;
import org.break_out.breakout.api.BreakoutApiService;
import org.break_out.breakout.api.Medium;
import org.break_out.breakout.api.PostingLocation;
import org.break_out.breakout.api.RemotePosting;
import org.break_out.breakout.api.Size;
import org.break_out.breakout.ui.activities.PostDetailActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import rx.functions.Action1;

/**
 * Created by Maximilian Duehr on 21.04.2016.
 */
public class PostingListAdapter extends RecyclerView.Adapter<PostingListAdapter.PostingViewHolder> {
    private static final String TAG = "PostingListAdapter";

    private static ArrayList<RemotePosting> _postingList;
    private static OnPositionFromEndReachedListener _listener;
    BreakoutApiService apiService;
    private Context _context;

    public PostingListAdapter(Context context, ArrayList<RemotePosting> postingList) {
        _postingList = postingList;
        _context = context;
        apiService = new BreakoutApiService(_context);
    }

    @Override
    public PostingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new PostingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PostingViewHolder holder, int position) {
        Log.d(TAG, "onBind");
        final RemotePosting posting = _postingList.get(holder.getAdapterPosition());
        holder.llWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail(holder.getAdapterPosition());
            }
        });
        holder.tvLikes.setText(posting.getLikes() + " Likes");
        holder.tvComments.setText(posting.getComments().size() + " Kommentare");
        holder.ivPosting.setVisibility(View.GONE);
        holder.rlChallenge.setVisibility(View.GONE);
        String teamName = posting.getUser().getParticipant().getTeamName();
        if(teamName != null) {
            holder.tvTeamName.setText(teamName);
        }

        // Add location to view
        PostingLocation postingLocation = posting.getPostingLocation();
        if(postingLocation != null) {
            if(postingLocation.getLocationData() != null) {
                String locationText = postingLocation.getLocationData().getLocality() + " - " +
                        postingLocation.getLocationData().getCountry();
                holder.tvTeamLocation.setText(locationText);
            } else {
                String lat = String.valueOf(postingLocation.getLatitude());
                String lon = String.valueOf(postingLocation.getLongitude());

                lat = lat.substring(0, 5);
                lon = lon.substring(0, 5);
                holder.tvTeamLocation.setText(lat + ", " + lon);
            }
        } else {
            holder.tvTeamLocation.setVisibility(View.GONE);
        }

        //Image logic
        Glide.clear(holder.ivPosting);
        for(Medium m : posting.getMedia()) {
            holder.ivPosting.setVisibility(View.VISIBLE);
            for(Size s : m.getSizes()) { // TODO: Fix possible NPE in kotlin class
                if(s.getType().equals("IMAGE")) {
                    Log.d(TAG, "glide should load");
                    Uri uri = Uri.parse(s.getUrl());
                    Glide.with(_context)
                            .load(uri)
                            .placeholder(R.drawable.bg_welcome_600dp)
                            .dontAnimate()
                            .dontTransform()
                            .into(holder.ivPosting);
                }
            }
            callListenerIfNeeded(holder.getAdapterPosition());
        }

        Glide.clear(holder.civTeamPic);
        if(posting.getUser().getProfilePic() != null) {
            for(Size s : posting.getUser().getProfilePic().getSizes()) {
                if(s.getType().equals("IMAGE")) {
                    Log.d(TAG, "glide should load profile");
                    Uri uri = Uri.parse(s.getUrl());
                    Glide.with(_context)
                            .load(uri)
                            .placeholder(R.drawable.placeholder_profile_pic)
                            .dontAnimate()
                            .dontTransform()
                            .into(holder.civTeamPic);

                }
            }
        }

        //like logic
        holder.rlLikeWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!posting.getHasLiked()) {
                    apiService.likePosting(posting.getId())
                            .subscribe(new Action1<ResponseBody>() {
                                @Override
                                public void call(ResponseBody responseBody) {
                                    posting.setLikes(posting.getLikes() + 1);
                                    posting.setHasLiked(true);
                                    holder.tvLikes.setText(posting.getLikes() + " Likes");
                                    holder.tvLikes.setTextColor(_context.getResources().getColor(R.color.red_like));
                                    holder.ivLikes.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_favorite_red_18dp));
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e(TAG, "Could not like post");
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                            });
                } else {
                    Log.d(TAG, "unlike");
                    apiService.unlikePosting(posting.getId())
                            .subscribe(new Action1<ResponseBody>() {
                                @Override
                                public void call(ResponseBody responseBody) {
                                    posting.setLikes(posting.getLikes() - 1);
                                    posting.setHasLiked(false);
                                    holder.tvLikes.setText(posting.getLikes() + " Likes");
                                    holder.tvLikes.setTextColor(_context.getResources().getColor(android.R.color.darker_gray));
                                    holder.ivLikes.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_favorite_black_18dp));
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e(TAG, "Could not unlike post");
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                            });
                }

            }
        });

        // Add likes + listener for likes to view!
        if(posting.getHasLiked()) { // TODO: This does not work
            holder.tvLikes.setTextColor(_context.getResources().getColor(R.color.red_like));
            holder.ivLikes.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_favorite_red_18dp));
        } else {
            //TODO: Fix this color, it is to bright compared to the default font color!
            holder.tvLikes.setTextColor(_context.getResources().getColor(android.R.color.darker_gray));
            holder.ivLikes.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_favorite_black_18dp));
        }

        // Add date to view
        if (posting.getDate() != null) {
            holder.tvTime.setText(timeBuilder(posting.getDate())); // TODO: Use Android Resources!
        }

        // Add text to view
        if (posting.getText() != null && !posting.getText().isEmpty()) {
            holder.tvComment.setText(posting.getText());
        }
        if(posting.getProof() != null) {
            holder.rlChallenge.setVisibility(View.VISIBLE);
            holder.tvChallenge.setText(posting.getProof().getDescription());
        }
    }


    @Override
    public int getItemCount() {
        return _postingList.size();
    }

    private String timeBuilder(long timestamp) {
        long curTime = System.currentTimeMillis();
        long dif = curTime - (timestamp * 1000);
        StringBuilder responseBuilder = new StringBuilder();
        int minutes = (int) (dif / 1000) / 60;
        int hours = 0;
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
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
            responseBuilder.append(c.get(Calendar.DAY_OF_MONTH))
                    .append(".")
                    .append(c.get(Calendar.MONTH) + 1)
                    .append(".")
                    .append(c.get(Calendar.YEAR));
            if (Locale.getDefault().getISO3Language().contains("de")) {
                responseBuilder.append(" um ");
            } else {
                responseBuilder.append(" at ");
            }
            responseBuilder.append(String.format("%02d", c.get(Calendar.HOUR_OF_DAY)))
                    .append(":")
                    .append(String.format("%02d", c.get(Calendar.MINUTE)));
        }
        return responseBuilder.toString();
    }

    public void setPositionListener(OnPositionFromEndReachedListener listener) {
        _listener = listener;
    }

    private void callListenerIfNeeded(int position) {
        if (_listener != null) {

            if ((getItemCount() - (position + 1)) <= _listener.getDesiredOffset()) {
                _listener.onOffsetReached();
            }
        }
    }

    private void showDetail(int pos){
        RemotePosting posting = _postingList.get(pos);
        int id = posting.getId();
        Intent showDetailIntent = new Intent(_context,PostDetailActivity.class);
        showDetailIntent.putExtra(PostDetailActivity.TAG_ID,id);
        _context.startActivity(showDetailIntent);
    }

    public static abstract class OnPositionFromEndReachedListener {
        private int offset;

        public OnPositionFromEndReachedListener(int offset) {
            this.offset = offset;
        }

        public final int getDesiredOffset() {
            return offset;
        }

        public abstract void onOffsetReached();
    }

    public class PostingViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llWrapper;
        RelativeLayout rlLikeWrapper;
        ImageView ivPosting;
        CircleImageView civTeamPic;
        TextView tvTeamName;
        TextView tvTeamLocation;
        TextView tvTime;
        TextView tvComment;
        RelativeLayout rlChallenge;
        TextView tvChallenge;
        ImageView ivChallenge;
        ImageView ivLikes;
        ImageView ivComments;
        TextView tvLikes;
        TextView tvComments;

        public PostingViewHolder(View itemView) {
            super(itemView);
            llWrapper = (LinearLayout) itemView.findViewById(R.id.posting_ll_wrapper);
            ivPosting = (ImageView) itemView.findViewById(R.id.posting_iv_image);
            civTeamPic = (CircleImageView) itemView.findViewById(R.id.posting_civ_teamPic);
            tvTeamName = (TextView) itemView.findViewById(R.id.posting_tv_teamName);
            tvTeamLocation = (TextView) itemView.findViewById(R.id.posting_tv_teamLocation);
            tvTime = (TextView) itemView.findViewById(R.id.posting_tv_time);
            tvComment = (TextView) itemView.findViewById(R.id.posting_tv_comment);
            rlChallenge = (RelativeLayout) itemView.findViewById(R.id.post_rl_challengeWrap);
            tvChallenge = (TextView) itemView.findViewById(R.id.post_tv_challenge);
            ivChallenge = (ImageView) itemView.findViewById(R.id.post_iv_trophy);
            ivLikes = (ImageView) itemView.findViewById(R.id.post_iv_likes);
            tvLikes = (TextView) itemView.findViewById(R.id.post_tv_likes);
            ivComments = (ImageView) itemView.findViewById(R.id.post_iv_comments);
            tvComments = (TextView) itemView.findViewById(R.id.post_tv_comments);
            rlLikeWrapper = (RelativeLayout) itemView.findViewById(R.id.post_rl_likeWrapper);
        }
    }


}
