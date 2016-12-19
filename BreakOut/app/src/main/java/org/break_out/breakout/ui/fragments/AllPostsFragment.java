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
import android.widget.Toast;

import org.break_out.breakout.R;
import org.break_out.breakout.api.BreakoutClient;
import org.break_out.breakout.api.NewPosting;
import org.break_out.breakout.manager.PostingManager;
import org.break_out.breakout.sync.model.Posting;
import org.break_out.breakout.ui.activities.MainActivity;
import org.break_out.breakout.ui.adapters.PostingListAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Maximilian Duehr on 21.04.2016.
 */
public class AllPostsFragment extends BOFragment {

    private static final String TAG = "AllPostsFragment";

    private PostingListAdapter _adapter;
    private ArrayList<Posting> _dataList;

    private PostingManager _postingManager;

    private RecyclerView _recyclerView;
    private SwipeRefreshLayout _swipeLayout;
    private ProgressBar _progressBar;
    private RelativeLayout _progressWrapper;

    private int lastPostRemoteId = 0;

    private int currentOffset = 0;
    private final int FETCH_LIMIT = 50;

    private BreakoutClient client = this.createBreakoutClient(); // TODO: Dependency InjectioN!

    private BreakoutClient createBreakoutClient() {
        return new Retrofit.Builder()
                .baseUrl("https://backend.break-out.org") // TODO: Fetch this from config!
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(BreakoutClient.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (_dataList == null) {
            _dataList = new ArrayList<>();
        }
        _adapter = new PostingListAdapter(getContext(), _dataList);
//        _adapter.setPositionListener(new PostingListAdapter.OnPositionFromEndReachedListener(1) {
//            @Override
//            public void onOffsetReached() {
//                fetchOlderPostings();
//            }
//        });

        client.getAllPostings(currentOffset, FETCH_LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<NewPosting>>() {
                    @Override
                    public void call(List<NewPosting> newPostings) {
                        Log.d(TAG, "Received a new list of postings");
                        for(NewPosting np: newPostings) {
                            _dataList.add(np.transformToPosting());
                            _adapter.notifyDataSetChanged();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "Something went wrong whilst fetching posts from API");
                        throw new RuntimeException(throwable);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_posts, container, false);

        _postingManager = PostingManager.getInstance();

        _recyclerView = (RecyclerView) v.findViewById(R.id.allPosts_recyclerView);
        _progressBar = (ProgressBar) v.findViewById(R.id.allPosts_pb);
        _progressWrapper = (RelativeLayout) v.findViewById(R.id.allPosts_rl_progressWrapper);
        _swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.allPost_swipeRefresh);

        _recyclerView.setAdapter(_adapter);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        _swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
//        _swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (_dataList.isEmpty()) {
//                    fetchOlderPostings();
//                } else {
//                    refreshPostings();
//                }
//                Log.d(TAG, "onRefresh");
//            }
//        });

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
        if (_dataList.isEmpty()) {
//            fetchOlderPostings();
            Log.d(TAG, "datalist was empty");
        }
        return v;
    }

//    private void fetchAllPosts() {
//        PostingManager m = PostingManager.getInstance();
//        m.resetPostingList();
//        m.getAllPosts(getContext(), new PostingManager.PostingListener() {
//            @Override
//            public void onPostingListChanged() {
//                updatePostList();
//
//            }
//        }, new LoadingListener() {
//            @Override
//            public void onLoadingTriggered() {
//                _progressWrapper.setVisibility(View.VISIBLE);
//                _progressBar.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onLoadingDismissed() {
//                _progressWrapper.setVisibility(View.GONE);
//                _progressBar.setVisibility(View.GONE);
//            }
//        });
//    }
//
//    private void fetchOlderPostings() {
//        PostingManager m = PostingManager.getInstance();
//        m.getAllPosts(getContext(), new PostingManager.PostingListener() {
//            @Override
//            public void onPostingListChanged() {
//                _swipeLayout.setRefreshing(false);
//                ArrayList<Posting> newPostings = _postingManager.getBeforeId(lastPostRemoteId, 7);
//                if (!newPostings.isEmpty()) {
//                    lastPostRemoteId = newPostings.get(newPostings.size() - 1).getRemoteID();
//                    showOlderPosts(newPostings);
//                    _swipeLayout.setRefreshing(false);
//                } else {
//                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.no_older_postings), Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        }, new LoadingListener() {
//            @Override
//            public void onLoadingTriggered() {
//                _progressWrapper.setVisibility(View.VISIBLE);
//                _progressBar.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onLoadingDismissed() {
//                _progressWrapper.setVisibility(View.GONE);
//                _progressBar.setVisibility(View.GONE);
//            }
//        });

//
//    }

    public void refreshPostings() {
        PostingManager m = PostingManager.getInstance();
        if (_postingManager.getNewestPosting() != null) {
            m.getPostingsAfterIdFromServer(getContext(), _postingManager.getNewestPosting().getRemoteID(), new PostingManager.NewPostingFetchedListener() {
                @Override
                public void noNewPostings() {
                    _swipeLayout.setRefreshing(false);
                    Toast.makeText(getContext(), getString(R.string.no_new_postings), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPostingListChanged() {
                    if (!_dataList.isEmpty()) {
                        updateNewerPosts(_postingManager.getAfterId(_dataList.get(0).getRemoteID()));
                        _swipeLayout.setRefreshing(false);
                        Log.d(TAG, "onPostingListChanged called");
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Etwas ist schief gelaufen. Bitte in Kürze neu versuchen", Toast.LENGTH_SHORT).show();
        }

    }

    private void showOlderPosts(ArrayList<Posting> newPostings) {
        for (Posting p : newPostings) {
            if (!isInDataList(p)) {
                _dataList.add(p);
                _adapter.notifyDataSetChanged();
            }
        }
        _adapter.notifyDataSetChanged();
    }

    private void updateNewerPosts(ArrayList<Posting> newPostings) {
        for (Posting p : newPostings) {
            if (!isInDataList(p)) {
                _dataList.add(0, p);
                _adapter.notifyDataSetChanged();
            }
        }
        _adapter.notifyDataSetChanged();
    }

    private void updatePostList() {
        _dataList.clear();
        _dataList.addAll(Posting.findWithQuery(Posting.class, "Select * FROM Posting ORDER BY _CREATED_TIMESTAMP DESC"));
        _adapter.notifyDataSetChanged();
    }

    private boolean isInDataList(Posting posting) {
        for (Posting p : _dataList) {
            if (p.getRemoteID() == posting.getRemoteID()) {
                return true;
            }
        }
        return false;
    }

    public interface LoadingListener {
        void onLoadingTriggered();

        void onLoadingDismissed();
    }

}
