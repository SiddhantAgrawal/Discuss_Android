package com.discuss.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.discuss.baseAdapters.CommentViewAdapter;
import com.discuss.datatypes.Comment;
import com.discuss.fetcher.impl.DataFetcherImpl;
import com.example.siddhantagrawal.check_discuss.R;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Siddhant Agrawal
 * @author Deepak Thakur
 */
public class QuestionView extends Activity {
    CommentViewAdapter adapter;
    ArrayList<Comment> populations;

    private final class Subs extends Subscriber<List<Comment>> {
        @Override
        public void onCompleted() {
            Log.e("loading comments now", "undating the comments");
            adapter.notifyDataSetChanged();
            loading = false;
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(List<Comment> questions) {
            Log.e("loading comments now", "got fresh list " + questions.size());
            Log.e("loading comments now", "the new size of comments is " + questions.size());
            populations.addAll(questions);
        }
    };

    private volatile boolean loading = false;

    private class EndlessScrollListener implements AbsListView.OnScrollListener {

        private volatile int visibleThreshold = 5;


        EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                Log.e("loading comments now", "plz wait");
                loading = true;
                new DataFetcherImpl().
                        getCommentsForQuestion(0,0,0,"").  /* TODO(Deepak): add proper values */
                        onBackpressureBuffer().
                        subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Subs()); /* TODO(Deepak):  do we really need a new Subscriber each time ??  */
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_complete);

        Intent intent = getIntent();

        TextView textViewForQuestion = (TextView) findViewById(R.id.question_complete_question);
        TextView textViewForLikes = (TextView) findViewById(R.id.question_complete_like_value);
        TextView textViewForPostedBy = (TextView) findViewById(R.id.question_complete_user_value);

        populations = (ArrayList<Comment>) intent.getSerializableExtra("comments");

        /* set results to the TextViews */
        textViewForQuestion.setText(intent.getStringExtra("questionText"));
        textViewForLikes.setText(Integer.toString(intent.getIntExtra("likes", 0)));
        textViewForPostedBy.setText(intent.getStringExtra("postedBy"));

        ListView listview = (ListView) findViewById(R.id.question_complete_commentList);
        adapter = new CommentViewAdapter(QuestionView.this, populations);

        listview.setAdapter(adapter);
        listview.setOnScrollListener(new EndlessScrollListener(4));
    }
}