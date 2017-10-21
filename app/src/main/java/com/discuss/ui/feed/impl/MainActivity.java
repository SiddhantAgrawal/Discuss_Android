package com.discuss.ui.feed.impl;

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

import com.discuss.datatypes.Question;
import com.discuss.ui.category.CategorySelector;
import com.discuss.ui.commented.impl.CommentedQuestionFragment;
import com.discuss.ui.liked.impl.LikedQuestionsFragment;
import com.discuss.ui.bookmark.impl.BookMarkFragment;
import com.discuss.ui.feed.MainFeedPresenter;
import com.discuss.utils.EndlessScrollListener;
import com.discuss.views.AskQuestionView;
import com.discuss.ui.question.view.QuestionView;
import com.example.siddhantagrawal.check_discuss.R;

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
    MainFeedPresenter<Question> mainFeedPresenter = new MainFeedPresenterImpl();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
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
                        Log.e("Main", "bookmarkedQ");
                        fragmentView = new BookMarkFragment();
                        break;
                    case 1:
                        Log.e("Main", "likedQ");
                        fragmentView = new LikedQuestionsFragment();
                        break;
                    case 2:
                        Log.e("Main", "commentedQ");
                        fragmentView = new CommentedQuestionFragment();
                        break;
                    case 3:
                        Log.e("Main", "category selection");
                        fragmentView = new CategorySelector();
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
            listview.setOnScrollListener(new EndlessScrollListener(() -> mainFeedPresenter.update(() -> adapter.notifyDataSetChanged()), 4));
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
        private MainFeedPresenter<Question> mainFeedPresenter;

        public QuestionViewAdapter(Context context, MainFeedPresenter<Question> mainFeedPresenter) {
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

        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.question_short, parent, false); /* TODO(Deepak): See if this has performance issues */
            final Question question = mainFeedPresenter.get(position).toBlocking().first();

            TextView questionText = (TextView) itemView.findViewById(R.id.question_short_question);
            TextView likes = (TextView) itemView.findViewById(R.id.question_short_like_value);
            TextView postedBy = (TextView) itemView.findViewById(R.id.question_short_postedby_value);
            TextView difficulty = (TextView) itemView.findViewById(R.id.question_short_difficulty_value);

            questionText.setText(question.getText());
            likes.setText(Integer.toString(question.getLikes()));
            postedBy.setText(question.getUserName());
            difficulty.setText(question.getDifficulty());
            ImageView imageView = itemView.findViewById(R.id.question_short_like_button);
            if (question.isLiked())
                imageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.QuestionViewAdapter.this.context, R.drawable.liked));
            else
                imageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.QuestionViewAdapter.this.context, R.drawable.like_icon));
            final boolean questionOrigionallyLiked = question.isLiked();
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        Log.e("user has liked :- ", String.valueOf(question.isLiked()));
                        if (question.isLiked()) {
                            imageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.QuestionViewAdapter.this.context, R.drawable.like_icon));
                            if (questionOrigionallyLiked)
                                likes.setText(Integer.toString(question.getLikes() - 1));
                            else
                                likes.setText(Integer.toString(question.getLikes()));
                        } else {
                            imageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.QuestionViewAdapter.this.context, R.drawable.liked));
                            if (!questionOrigionallyLiked)
                                likes.setText(Integer.toString(question.getLikes() + 1));
                            else
                                likes.setText(Integer.toString(question.getLikes()));
                        }
                        question.setLiked(!question.isLiked());
                    }
                    return true;
                }
            });
            itemView.setOnClickListener(arg0 -> {
                Intent intent = new Intent(context, QuestionView.class);
                intent.putExtra("question", question);
                context.startActivity(intent);
            });
            return itemView;
        }
    }


}