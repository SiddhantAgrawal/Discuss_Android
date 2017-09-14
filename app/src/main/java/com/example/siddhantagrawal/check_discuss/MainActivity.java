package com.example.siddhantagrawal.check_discuss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;


import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author siddhant.agrawal, Deepak Thakur
 *
 */
public class MainActivity extends Activity {

    ListView listview;
    ListViewAdapter adapter;
    ProgressDialog mProgressDialog;
    List<Population.Data> populations = new ArrayList<>();

    final Subscriber<Population> populationSubscriber = new Subscriber<Population>() {
        @Override
        public void onCompleted() {
            Log.e("list size", ""+populations.size());
            Log.e("MainActivity", "before Dismiss");
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(MainActivity.this, populations);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
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
        public void onNext(Population population) {
            populations.addAll(population.worldpopulation);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from listview_main.xml
        setContentView(R.layout.listview_main);

        Log.e("MainActity", "toStart");
        mProgressDialog = new ProgressDialog(MainActivity.this);
        // Set progressdialog title
        mProgressDialog.setTitle("Android JSON Parse Tutorial");
        // Set progressdialog message
        mProgressDialog.setMessage("Loading...");
       // mProgressDialog.setIndeterminate(false);
        // Show progressdialog
       mProgressDialog.show();



        new DataFetcherImpl().
                questions().onBackpressureBuffer().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(populationSubscriber);
    }

    protected void onDestroy() {
        super.onDestroy();
        if(populationSubscriber != null && !populationSubscriber.isUnsubscribed()) {
            populationSubscriber.unsubscribe();
        }
    }

}