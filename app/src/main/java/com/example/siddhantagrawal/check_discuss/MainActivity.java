package com.example.siddhantagrawal.check_discuss;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    ActionBarDrawerToggle mDrawerToggle;
    List<Question> populations = new ArrayList<>();
    CharSequence mDrawerTitle;
    CharSequence mTitle;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
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
                        getQuestions(0,0,0,""). /* TODO(Deepak): add proper values */
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
        ///
        String[] menutitles;
        TypedArray menuIcons;
// nav drawer title


         List<RowItem> rowItems;
         CustomMenuAdaptor adapter;
        mTitle = mDrawerTitle = getTitle();
        menutitles = getResources().getStringArray(R.array.titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.slider_list);

        rowItems = new ArrayList<RowItem>();

        for (int i = 0; i < menutitles.length; i++) {
            RowItem items = new RowItem(menutitles[i]);
            rowItems.add(items);
        }
        adapter = new CustomMenuAdaptor(getApplicationContext(), rowItems);
        String[] menu = new String[]{"Home","Android","Windows","Linux","Raspberry Pi","WordPress","Videos","Contact Us"};
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, menu));
      //  mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new FragmentOne();
                        break;
                    case 1:
                        fragment = new FragmentTwo();
                        break;
                    case 2:
                        fragment = new FragmentThree();
                        break;
                    default:
                        break;
                }
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
// update selected item and title, then close the drawer
                    //setTitle(menutitles[position]);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                else {
// error in creating fragment
                    Log.e("MainActivity", "Error in creating fragment");
                }
            }
        });
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name)
        {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle("title1");
// calling onPrepareOptionsMenu() to show action bar icons */
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("title2");
// calling onPrepareOptionsMenu() to hide action bar icons */
                invalidateOptionsMenu();
            }
        };
    //    mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
            Log.e("Main", "ohh, savedInstance is null");
            // on first time display view for first nav item
            //updateDisplay(0);
        }

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

/*    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }*/

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
// Handle action bar actions click
        switch (item.getItemId()) {
            case R.string.action_settings :
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    /***   * Called when invalidateOptionsMenu() is triggered   */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
// if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
// Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
// Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



        ///


    protected void onDestroy() {
        super.onDestroy();
        if (populationSubscriber != null && !populationSubscriber.isUnsubscribed()) {
            populationSubscriber.unsubscribe();
        }
    }

}