package com.example.siddhantagrawal.check_discuss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;

import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;


import com.discuss.datatypes.Question;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author siddhant.agrawal
 * @author Deepak Thakur
 */
public class MainActivity extends Activity {

    ListView listview;
    ListViewAdapter adapter;
    ProgressDialog mProgressDialog;
    List<Question> populations = new ArrayList<>();
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
                loading = true;
                /* @todo Need to send correct offset and limits going forward */
                new DataFetcherImpl().
                        getQuestions(0,0,0,"").onBackpressureBuffer().
                        subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Subs()); /* TODO:  do we really need a new Subscriber each time ??  */
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    private final class Subs extends Subscriber<List<Question>> {
        @Override
        public void onCompleted() {
            adapter.notifyDataSetChanged();
            loading = false;
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(List<Question> questions) {
            populations.addAll(questions);
        }
    };


    final Subscriber<List<Question>> populationSubscriber = new Subscriber<List<Question>>() {
        @Override
        public void onCompleted() {
            Log.e("MainActivity", "before Dismiss");
            listview = (ListView) findViewById(R.id.listview);
            adapter = new ListViewAdapter(MainActivity.this, populations);
            listview.setAdapter(adapter);
            listview.setOnScrollListener(new EndlessScrollListener(4));
            mProgressDialog.dismiss();
            Log.e("MainActivity", "after Dismiss");

        }

        @Override
        public void onError(Throwable e) {
            Log.e("MainActivity", "error" + e);
            mProgressDialog.dismiss();
            // @todo(deepak): the difficult part
        }

        @Override
        public void onNext(List<Question> questions) {
            populations.addAll(questions);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_page);
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle("Android JSON Parse Tutorial");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();


        new DataFetcherImpl().
                getQuestions(0,0,0,"").onBackpressureBuffer().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(populationSubscriber);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (populationSubscriber != null && !populationSubscriber.isUnsubscribed()) {
            populationSubscriber.unsubscribe();
        }
    }

}