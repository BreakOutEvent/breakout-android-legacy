package org.break_out.breakout.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flurry.android.tumblr.Post;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.PostingManager;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.ui.activities.MainActivity;
import org.break_out.breakout.ui.adapters.PostingListAdapter;
import org.break_out.breakout.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Maximilian Duehr on 21.04.2016.
 */
public class AllPostsFragment extends BOFragment {

    private static final String TAG = "AllPostsFragment";

    private PostingListAdapter _adapter;
    private ArrayList<Posting> _dataList;

    private PostingManager _postingManager;

    private RecyclerView _recyclerView;

    private int lastPostRemoteId = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(_dataList == null) {
            _dataList = new ArrayList<>();
        }
        _adapter = new PostingListAdapter(getContext(), _dataList);
        _adapter.setPositionListener(new PostingListAdapter.OnPositionFromEndReachedListener(1) {
            @Override
            public void onOffsetReached() {
                fetchNewPosts();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_posts, container, false);

        _postingManager = PostingManager.getInstance();

        _recyclerView = (RecyclerView) v.findViewById(R.id.allPosts_recyclerView);
        _recyclerView.setAdapter(_adapter);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));;

        // Init and populate Toolbar
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.allPosts_toolbar);
        toolbar.setTitle(getString(R.string.title_allPosts));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if(!(activity instanceof MainActivity)) {
                    return;
                }
                MainActivity mainActivity = (MainActivity) activity;
                mainActivity.openDrawer();
            }
        });
        if(_dataList.isEmpty()) {
            fetchNewPosts();
        }

        _postingManager.getPostingsAfterIdFromServer(lastPostRemoteId, new PostingManager.NewPostingFetchedListener() {
            @Override
            public void noNewPostings() {
            }

            @Override
            public void onPostingListChanged() {
                fetchNewPosts();
            }
        });
        return v;
    }

    private void fetchAllPosts() {
        PostingManager m = PostingManager.getInstance();
        m.resetPostingList();
        m.getAllPosts(getContext(), new PostingManager.PostingListener() {
            @Override
            public void onPostingListChanged() {
                updatePostList();

            }
        });
    }

    private void fetchNewPosts() {
        PostingManager m = PostingManager.getInstance();
        m.getAllPosts(getContext(), new PostingManager.PostingListener() {
            @Override
            public void onPostingListChanged() {
                ArrayList<Posting> newPostings = _postingManager.getAfterId(lastPostRemoteId,7);
                if(!newPostings.isEmpty()) {
                    lastPostRemoteId = Integer.parseInt(newPostings.get(newPostings.size()-1).getRemoteID());
                    updateNewPosts(newPostings);
                } else {
                    Toast.makeText(getContext(),getString(R.string.no_older_posts),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateNewPosts(ArrayList<Posting> newPostings){
        for(Posting p : newPostings) {
            if(!isInDataList(p)) {
                _dataList.add(p);
                _adapter.notifyDataSetChanged();
            }
        }
        _adapter.notifyDataSetChanged();
    }

    private void updatePostList() {
        _dataList.clear();
        _dataList.addAll(Posting.findWithQuery(Posting.class,"Select * FROM Posting ORDER BY _CREATED_TIMESTAMP DESC"));
        _adapter.notifyDataSetChanged();
    }

    private boolean isInDataList(Posting posting) {
        for(Posting p : _dataList) {
            if(p.getRemoteID() == posting.getRemoteID()) {
                return true;
            }
        }
        return false;
    }


}
