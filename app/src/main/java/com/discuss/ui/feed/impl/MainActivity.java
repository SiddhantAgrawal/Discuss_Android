package com.discuss.ui.feed.impl;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.discuss.DiscussApplication;
import com.discuss.ui.BookMarkState;
import com.discuss.ui.LikeState;
import com.discuss.ui.QuestionLikeState;
import com.discuss.ui.QuestionSummary;
import com.discuss.ui.category.CategorySelector;
import com.discuss.ui.commented.impl.CommentedQuestionFragment;
import com.discuss.ui.liked.impl.LikedQuestionsFragment;
import com.discuss.ui.bookmark.impl.BookMarkFragment;
import com.discuss.ui.feed.MainFeedPresenter;
import com.discuss.utils.Command;
import com.discuss.utils.EndlessScrollListener;
import com.discuss.ui.question.post.impl.AskQuestionView;
import com.discuss.ui.question.view.QuestionView;
import com.discuss.utils.UIUtil;
import com.example.siddhantagrawal.check_discuss.R;

import javax.inject.Inject;

/**
 * @author siddhant.agrawal
 * @author Deepak Thakur
 */
public class MainActivity extends AppCompatActivity {

    ListView listview;
    QuestionViewAdapter adapter;
    ProgressDialog mProgressDialog;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;

    @Inject
    public MainFeedPresenter mainFeedPresenter;
    @Inject
    public BookMarkFragment bookMarkFragment;
    @Inject
    public LikedQuestionsFragment likedQuestionsFragment;
    @Inject
    public CommentedQuestionFragment commentedQuestionFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DiscussApplication) getApplication()).getMainComponent().inject(this);
        setContentView(R.layout.main_activity);
        setTitle("Question Feed");
        String[] menutitles = getResources().getStringArray(R.array.titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.slider_list);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menutitles));
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                com.discuss.ui.View fragmentView = null;
                switch (position) {
                    case 0:
                        fragmentView = bookMarkFragment;
                        break;
                    case 1:
                        fragmentView = likedQuestionsFragment;
                        break;
                    case 2:
                        fragmentView = commentedQuestionFragment;
                        break;
                    case 3:
                        mDrawerLayout.closeDrawer(mDrawerList);
                        Intent intent = new Intent(MainActivity.this, CategorySelector.class);
                        MainActivity.this.startActivity(intent);
                    default:
                        break;
                }

                if(fragmentView instanceof Fragment) {
                    final Fragment fragment = (Fragment) fragmentView;
                    fragmentView.init(() -> {
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                    });
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                setTitle("Question Feed");
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
        mainFeedPresenter.init(() -> {
            listview = (ListView) findViewById(R.id.listview);
            adapter = new QuestionViewAdapter(MainActivity.this, mainFeedPresenter);
            listview.setAdapter(adapter);
            listview.setOnScrollListener(new EndlessScrollListener(new Command() {
                @Override
                public void execute() {
                    mainFeedPresenter.update(() -> adapter.notifyDataSetChanged());
                }
            }, 4));
            mProgressDialog.dismiss();
        });
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            if(getFragmentManager().getBackStackEntryCount() == 1) {
                this.getSupportActionBar().setTitle("Question Feed");
            }
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

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public static class QuestionViewAdapter extends BaseAdapter {

        // Declare Variables
        private Context context;
        private MainFeedPresenter mainFeedPresenter;

        public QuestionViewAdapter(Context context, MainFeedPresenter mainFeedPresenter) {
            this.context = context;
            this.mainFeedPresenter = mainFeedPresenter;
        }

        @Override
        public int getCount() {
            return mainFeedPresenter.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ClickableViewAccessibility")
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.question_short, parent, false);
            final QuestionSummary questionSummary = mainFeedPresenter.get(position).toBlocking().value();

            UIUtil.setTextView(itemView, R.id.question_short_question, questionSummary.getText());
            UIUtil.setTextView(itemView, R.id.question_short_like_value, Integer.toString(questionSummary.getLikes()));
            UIUtil.setTextView(itemView, R.id.question_short_difficulty_value, questionSummary.getDifficulty());
            UIUtil.setTextView(itemView, R.id.question_short_view_value, Integer.toString(questionSummary.getViews()));
            UIUtil.setImageView(context, itemView, R.id.question_short_image, questionSummary.getImageUrl());

            ImageView likeImage = itemView.findViewById(R.id.question_short_like);
            TextView textView = itemView.findViewById(R.id.question_short_like_value);

            final LikeState likeState = new QuestionLikeState(questionSummary.getQuestionId(),
                    questionSummary.getLikes(),
                    questionSummary.isLiked(),
                    likeImage,
                    textView,
                    ContextCompat.getDrawable(MainActivity.QuestionViewAdapter.this.context, R.drawable.like_icon),
                    ContextCompat.getDrawable(MainActivity.QuestionViewAdapter.this.context, R.drawable.liked),
                    mainFeedPresenter);

            likeImage.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    likeState.pressUpdate();
                }
                return true;
            });

            ImageView bookmarkImage = itemView.findViewById(R.id.question_short_bookmark);

            final BookMarkState bookMarkState = new BookMarkState(questionSummary.getQuestionId(),
                    questionSummary.isBookmarked(),
                    bookmarkImage,
                    ContextCompat.getDrawable(MainActivity.QuestionViewAdapter.this.context, R.drawable.bookmark),
                    ContextCompat.getDrawable(MainActivity.QuestionViewAdapter.this.context, R.drawable.bookmark),
                    mainFeedPresenter);

            bookmarkImage.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    bookMarkState.pressUpdate();
                }
                return true;
            });


            itemView.setOnClickListener(arg0 -> {
                Intent intent = new Intent(context, QuestionView.class);
                intent.putExtra("questionId", questionSummary.getQuestionId());
                context.startActivity(intent);
            });
            return itemView;
        }
    }


}