package com.discuss.ui.feed;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.discuss.baseAdapters.QuestionViewAdapter;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.fetcher.impl.DataFetcherImpl;
import com.discuss.fragment.factory.BookmarkedQuestionsFragmentFactory;
import com.discuss.fragment.factory.FragmentFactory;
import com.discuss.fragment.factory.LikedQuestionsFragmentFactory;
import com.discuss.fragment.factory.UserAddedCommenstsFragmentFactory;
import com.discuss.ui.feed.impl.MainFeedPresenterImpl;
import com.discuss.views.AskQuestionView;
import com.example.siddhantagrawal.check_discuss.R;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * @author siddhant.agrawal
 * @author Deepak Thakur
 */
public class MainActivity extends AppCompatActivity {

    ListView listview;
    QuestionViewAdapter adapter;
    ProgressDialog mProgressDialog;
    ActionBarDrawerToggle mDrawerToggle;
    CharSequence mDrawerTitle;
    CharSequence mTitle;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    MainFeedPresenter<Question> mainFeedPresenter = new MainFeedPresenterImpl();

    private class EndlessScrollListener implements AbsListView.OnScrollListener {
        private volatile int visibleThreshold = 5;

        EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                mainFeedPresenter.update(new Action0() {
                    @Override
                    public void call() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mTitle = mDrawerTitle = getTitle();
        String[] menutitles = getResources().getStringArray(R.array.titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.slider_list);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menutitles));
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Observable<Fragment> fragment = null;
                FragmentFactory<Question> bookMarkFragmentFactory = new BookmarkedQuestionsFragmentFactory();
                FragmentFactory<Question> likedQuestionFragmentFactory = new LikedQuestionsFragmentFactory();
                FragmentFactory<Comment> userAddedCommentsFactory = new UserAddedCommenstsFragmentFactory();
                switch (position) {
                    case 0:
                        fragment = bookMarkFragmentFactory.createFragment();
                        break;
                    case 1:
                        fragment = likedQuestionFragmentFactory.createFragment();
                        Log.e("mains", "selected likes");
                        break;
                    case 2:
                        fragment = userAddedCommentsFactory.createFragment();
                        break;
                    default:
                        break;
                }

                fragment.subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Fragment>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Fragment fragment) {
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                });
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle("title1");
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("title2");
                invalidateOptionsMenu();
            }
        };

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle("discuss, lets discuss");
        mProgressDialog.setMessage("loading... thanks for your patience");
        mProgressDialog.show();

        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AskQuestionView.class);
                MainActivity.this.startActivity(intent);
            }
        });
        mainFeedPresenter.init(new Action0() {
            @Override
            public void call() {
                Log.e("MainActivity", "before Dismiss");
                listview = (ListView) findViewById(R.id.listview);
                adapter = new QuestionViewAdapter(MainActivity.this, mainFeedPresenter);
                listview.setAdapter(adapter);
                listview.setOnScrollListener(new EndlessScrollListener(4));
                mProgressDialog.dismiss();
                Log.e("MainActivity", "after Dismiss");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.string.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}