package org.break_out.breakout.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.PostingManager;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.ui.activities.MainActivity;
import org.break_out.breakout.ui.adapters.PostingListAdapter;

import java.util.ArrayList;

/**
 * Created by Maximilian Duehr on 21.04.2016.
 */
public class AllPostsFragment extends BOFragment {

    private static final String TAG = "AllPostsFragment";

    private PostingListAdapter _adapter;
    private ArrayList<Posting> _dataList;

    private RecyclerView _recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (_dataList == null) {
            _dataList = new ArrayList<>();
        }
        _adapter = new PostingListAdapter(getContext(),_dataList);
        //Intent downloadAllIntent = new Intent(getContext(), MediaLoaderService.class);
        //getContext().startService(downloadAllIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_posts, container, false);

        _recyclerView = (RecyclerView) v.findViewById(R.id.allPosts_recyclerView);
        _recyclerView.setAdapter(_adapter);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Init and populate Toolbar
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.allPosts_toolbar);
        toolbar.setTitle(getString(R.string.title_allPosts));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (!(activity instanceof MainActivity)) {
                    return;
                }
                MainActivity mainActivity = (MainActivity) activity;
                mainActivity.openDrawer();
            }
        });

        fetchAllPosts();

        return v;
    }

    private void fetchAllPosts() {
        PostingManager m = PostingManager.getInstance();
        m.resetPostingList();
        PostingManager.getInstance().getAllPosts(getContext(),new PostingManager.PostingListener() {
            @Override
            public void onPostingListChanged() {
                updatePostList();
            }
        });
    }

    private void updatePostList() {
        _dataList.clear();
        _dataList.addAll(Posting.listAll(Posting.class));
        _adapter.notifyDataSetChanged();
    }
}
