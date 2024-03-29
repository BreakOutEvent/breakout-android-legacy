package org.break_out.breakout.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.break_out.breakout.R;
import org.break_out.breakout.api.BreakoutApiService;
import org.break_out.breakout.api.Medium;
import org.break_out.breakout.api.RemotePosting;
import org.break_out.breakout.api.Size;
import org.break_out.breakout.api.User;
import org.break_out.breakout.ui.adapters.PostingListAdapter;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.functions.Action1;

public class PostDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostDetailActivity";
    public static final String TAG_ID = "TagId";
    private int _remoteId;
    private RelativeLayout _rl_challenge;
    private TextView _tv_challenge;
    private ImageView _iv_posting;
    private ImageView _iv_like;
    private CircleImageView _civ_profilePicture;
    private TextView _tv_teamName;
    private TextView _tv_location;
    private TextView _tv_likes;
    private TextView _tv_comments;
    private TextView _tv_comment;
    private TextView _tv_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Intent callingIntent = getIntent();
        _remoteId = callingIntent.getIntExtra(TAG_ID, -1);
        _rl_challenge = (RelativeLayout) findViewById(R.id.posting_rl_challengeWrap);
        _civ_profilePicture = (CircleImageView) findViewById(R.id.posting_civ_teamPic);
        _iv_posting = (ImageView) findViewById(R.id.posting_iv_image);
        _iv_like = (ImageView) findViewById(R.id.posting_iv_likes);
        _tv_teamName = (TextView) findViewById(R.id.posting_tv_teamName);
        _tv_location = (TextView) findViewById(R.id.posting_tv_teamLocation);
        _tv_likes = (TextView) findViewById(R.id.posting_tv_likes);
        _tv_comments = (TextView) findViewById(R.id.posting_tv_comments);
        _tv_comment = (TextView) findViewById(R.id.posting_tv_comment);
        _tv_time = (TextView) findViewById(R.id.posting_tv_time);
        _tv_challenge = (TextView) findViewById(R.id.posting_tv_challenge);


        _iv_posting.setVisibility(View.GONE);
        Glide.clear(_iv_posting);

        if(_remoteId != -1) {
            loadData(_remoteId);
            Log.d(TAG,"id: "+_remoteId);
        }

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle(getString(R.string.app_name));
        tb.setTitleTextColor(getResources().getColor(R.color.white));
    }

    private void loadData(int id) {
        new BreakoutApiService(this).getPostingById(id).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        }).subscribe(new Action1<RemotePosting>() {
            @Override
            public void call(RemotePosting remotePosting) {
                populateView(remotePosting);

            }
        });
    }

    private void populateView(RemotePosting posting) {
        //TODO
        User u = posting.getUser();
        if(u.getProfilePic()!= null){
            for(Size s : u.getProfilePic().getSizes()) {
                if(s.getType()!=null){
                    if(s.getType().equals("IMAGE")) {
                        Glide.with(this)
                                .load(s.getUrl())
                                .placeholder(R.drawable.placeholder_profile_pic)
                                .dontAnimate()
                                .dontTransform()
                                .into(_civ_profilePicture);
                    }
                }
            }
        }
        if(!posting.getMedia().isEmpty()){
            for(Medium m : posting.getMedia()){
                if(m.getSizes() != null){
                    for(Size s : m.getSizes()){
                        if(s.getType()!=null){
                            if(s.getType().equals("IMAGE")){
                                _iv_posting.setVisibility(View.VISIBLE);
                                Uri uri = Uri.parse(s.getUrl());
                                Glide.with(this)
                                        .load(uri)
                                        .dontAnimate()
                                        .dontTransform()
                                        .into(_iv_posting);
                            }
                        }
                    }
                }
            }
        }
        _tv_comments.setText((posting.getComments().size()+""));
        _tv_comment.setText(posting.getText());
        _tv_likes.setText((posting.getLikes()+""));
        if(posting.getUser().getParticipant() != null){
            _tv_teamName.setText(posting.getUser().getParticipant().getTeamName());
        }
        if(posting.getPostingLocation() != null && posting.getPostingLocation().getLocationData() != null){
            _tv_location.setText(posting.getPostingLocation().getLocationData().getLocality() + " - " +posting.getPostingLocation().getLocationData().getCountry());
        } else {
            _tv_location.setText("");
        }

        if(posting.getProves() == null){
            _rl_challenge.setVisibility(View.GONE);
        } else {
            _tv_challenge.setText(posting.getProves().getDescription());
        }
        if(posting.getDate() != null) {
            _tv_time.setText(PostingListAdapter.timeBuilder(posting.getDate()));
        }
    }
}
