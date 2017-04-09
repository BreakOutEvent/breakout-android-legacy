package org.break_out.breakout.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.break_out.breakout.R;
import org.break_out.breakout.api.BreakoutApiService;
import org.break_out.breakout.api.RemotePosting;
import org.break_out.breakout.api.Size;
import org.break_out.breakout.api.User;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.functions.Action1;

public class PostDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostDetailActivity";
    public static final String TAG_ID = "TagId";
    private int _remoteId;
    private ImageView _iv_posting;
    private CircleImageView _civ_profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Intent callingIntent = getIntent();
        _remoteId = callingIntent.getIntExtra(TAG_ID, -1);
        _civ_profilePicture = (CircleImageView) findViewById(R.id.posting_civ_teamPic);

        if(_remoteId != -1) {
            loadData(_remoteId);
            Log.d(TAG,"id: "+_remoteId);
        }
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
    }
}
