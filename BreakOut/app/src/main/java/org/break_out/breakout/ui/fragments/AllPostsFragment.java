package org.break_out.breakout.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.break_out.breakout.R;
import org.break_out.breakout.api.BreakoutApiService;
import org.break_out.breakout.api.NewPosting;
import org.break_out.breakout.ui.activities.MainActivity;
import org.break_out.breakout.ui.adapters.PostingListAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by Maximilian Duehr on 21.04.2016.
 */
public class AllPostsFragment extends BOFragment {

    private static final String TAG = "AllPostsFragment";

    private PostingListAdapter _adapter;
    private ArrayList<NewPosting> _dataList;

    private RecyclerView _recyclerView;
    private SwipeRefreshLayout _swipeLayout;
    private ProgressBar _progressBar;
    private RelativeLayout _progressWrapper;

    private int currentOffset = 0;
    private final int FETCH_LIMIT = 10;

    private BreakoutApiService service;

    // TODO: Dependency Injection!
    private BreakoutApiService createBreakoutClient() {
        BreakoutApiService service = new BreakoutApiService(this.getActivity());
        return service;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = this.createBreakoutClient(); // TODO: Dependency Injection!
        if (_dataList == null) {
            _dataList = new ArrayList<>();
        }
        _adapter = new PostingListAdapter(getContext(), _dataList);
        getNextPostings();
        _adapter.setPositionListener(new PostingListAdapter.OnPositionFromEndReachedListener(1) {
            @Override
            public void onOffsetReached() {
                getNextPostings();
                Log.d(TAG,"offset reached");
            }
        });
    }

    private void getNextPostings() {
        service.getPostings(currentOffset, FETCH_LIMIT)
                .subscribe(new Action1<List<NewPosting>>() {
                    @Override
                    public void call(List<NewPosting> newPostings) {
                        Log.d(TAG, "Received a new list of postings");
                        if (newPostings.size() == 0) {
                            Log.i(TAG, "List of size was zero");
                        }
                        for (NewPosting np : newPostings) {
                            _dataList.add(np);
                        }
                        currentOffset += FETCH_LIMIT;
                        _adapter.notifyDataSetChanged();
                        _swipeLayout.setRefreshing(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "Something went wrong whilst fetching posts from API");
                        throw new RuntimeException(throwable); // TODO: Improve error handling
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_posts, container, false);

        _recyclerView = (RecyclerView) v.findViewById(R.id.allPosts_recyclerView);
        _progressBar = (ProgressBar) v.findViewById(R.id.allPosts_pb);
        _progressWrapper = (RelativeLayout) v.findViewById(R.id.allPosts_rl_progressWrapper);
        _swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.allPost_swipeRefresh);

        _recyclerView.setAdapter(_adapter);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        _swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        _swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentOffset = 0;
                _dataList.clear();
                _adapter.notifyDataSetChanged();
                getNextPostings();
                Log.d(TAG, "onRefresh: Resetting offset and loading postings from server");
            }
        });

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
        return v;
    }
}
