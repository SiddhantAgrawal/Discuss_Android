package com.discuss.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discuss.baseAdapters.CommentViewAdapter;
import com.discuss.datatypes.Comment;
import com.example.siddhantagrawal.check_discuss.R;

import android.widget.AbsListView;
import android.widget.ListView;

import com.discuss.baseAdapters.QuestionViewAdapter;
import com.discuss.datatypes.Question;
import com.discuss.fetcher.impl.DataFetcherImpl;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserAddedCommentsFragment extends Fragment {
    private volatile boolean loading = false;
    ArrayList<Comment> comments;
    private class EndlessScrollListener implements AbsListView.OnScrollListener {
        private volatile int visibleThreshold = 5;
        EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                loading = true;
                /* @todo Need to send correct offset and limits going forward */
                new DataFetcherImpl().
                        getUserAddedComments(0,0,""). /* TODO(Deepak): add proper values */
                        onBackpressureBuffer().
                        subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Subscriber<List<Comment>>() {
                            @Override
                            public void onCompleted() {
                                adapter.notifyDataSetChanged();
                                loading = false;
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onNext(List<Comment> fetchedComments) {
                                comments.addAll(fetchedComments);
                            }
                        }); /* TODO(Deepak):  do we really need a new Subscriber each time ??  */
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }
    private ListView listView;
    CommentViewAdapter adapter;

    @SuppressWarnings(value = "unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_added_comments, container, false);
        listView =  (ListView) itemView.findViewById(R.id.fragment_added_comments);

        comments = (ArrayList<Comment>) getArguments().getSerializable("data");
        adapter = new CommentViewAdapter(getActivity(), comments, null);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new EndlessScrollListener(4));
        return itemView;
    }
}
