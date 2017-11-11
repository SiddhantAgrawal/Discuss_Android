package com.discuss.ui.question.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.discuss.DiscussApplication;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.ui.BookMarkState;
import com.discuss.ui.LikeState;
import com.discuss.ui.QuestionLikeState;
import com.discuss.ui.QuestionSummary;
import com.discuss.ui.feed.impl.MainActivity;
import com.discuss.utils.Command;
import com.discuss.utils.EndlessScrollListener;
import com.discuss.utils.UIUtil;
import com.example.siddhantagrawal.check_discuss.R;

import javax.inject.Inject;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * @author Siddhant Agrawal
 * @author Deepak Thakur
 */
public class QuestionView extends Activity {
    CommentViewAdapter adapter;
    private ProgressDialog mProgressDialog;

    @Inject
    QuestionViewPresenter questionViewPresenter;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DiscussApplication) getApplication()).getMainComponent().inject(this);
        setContentView(R.layout.question);
        mProgressDialog = new ProgressDialog(QuestionView.this);
        mProgressDialog.setTitle("see question");
        mProgressDialog.setMessage("loading... thanks for your patience");
        mProgressDialog.show();
        Intent intent = getIntent();
        int questionID =  intent.getIntExtra("questionId", 0);

        questionViewPresenter.init(new Action0() {
            @Override
            public void call() {
                Log.e("QuestionView", "on completed of question presenter");
                ListView listview = (ListView) findViewById(R.id.question_view);
                adapter = new CommentViewAdapter(QuestionView.this, questionViewPresenter);
                listview.setAdapter(adapter);
                listview.setOnScrollListener(new EndlessScrollListener(new Command() {
                    @Override
                    public void execute() {
                        Log.e("QuestionView", "in execute");
                        questionViewPresenter.update(() -> adapter.notifyDataSetChanged());
                    }
                }, 4));
                mProgressDialog.dismiss();
            }
        }, questionID);


    }

    private class CommentViewAdapter extends BaseAdapter {

        private Context context;
        private QuestionViewPresenter questionViewPresenter;

        CommentViewAdapter(Context context, QuestionViewPresenter questionViewPresenter) {
            this.context = context;
            this.questionViewPresenter = questionViewPresenter;
        }

        @Override
        public int getCount() {
            return questionViewPresenter.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private View getQuestionView(ViewGroup parent) {
            try {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View itemView = inflater.inflate(R.layout.question_complete, parent, false);

                TextView textViewForQuestion = (TextView) itemView.findViewById(R.id.question_complete_question);
                TextView textViewForLikes = (TextView) itemView.findViewById(R.id.question_complete_like_value);
                TextView textViewForPostedBy = (TextView) itemView.findViewById(R.id.question_complete_postedby_value);
                TextView textViewForDifficultyLevel = (TextView) itemView.findViewById(R.id.question_complete_difficulty_value);

                questionViewPresenter.getQuestion().subscribe(new Action1<QuestionSummary>() {
                    @Override
                    public void call(QuestionSummary questionSummary) {
                        UIUtil.setTextView(itemView, R.id.question_complete_question, questionSummary.getText());
                        UIUtil.setTextView(itemView, R.id.question_complete_like_value, Integer.toString(questionSummary.getLikes()));
                        UIUtil.setTextView(itemView, R.id.question_complete_asked_by_value, questionSummary.getUserName());
                        UIUtil.setTextView(itemView, R.id.question_complete_difficulty_value, questionSummary.getDifficulty());
                        UIUtil.setTextView(itemView, R.id.question_complete_view_value, Integer.toString(questionSummary.getViews()));
                        UIUtil.setImageView(context, itemView, R.id.question_complete_image, questionSummary.getImageUrl());

                        ImageView likeImage = itemView.findViewById(R.id.question_short_like);
                        TextView textView = itemView.findViewById(R.id.question_short_like_value);

                        final LikeState likeState = new QuestionLikeState(questionSummary.getQuestionId(),
                                questionSummary.getLikes(),
                                questionSummary.isLiked(),
                                likeImage,
                                textView,
                                ContextCompat.getDrawable(CommentViewAdapter.this.context, R.drawable.like_icon),
                                ContextCompat.getDrawable(CommentViewAdapter.this.context, R.drawable.liked),
                                questionViewPresenter);

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
                                ContextCompat.getDrawable(CommentViewAdapter.this.context, R.drawable.bookmark),
                                ContextCompat.getDrawable(CommentViewAdapter.this.context, R.drawable.bookmark),
                                questionViewPresenter);

                        bookmarkImage.setOnTouchListener((view, motionEvent) -> {
                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                bookMarkState.pressUpdate();
                            }
                            return true;
                        });

                    }
                }, throwable -> {
                });
                return itemView;
            } catch (Exception e) {
            }
            return null;
        }

        private View getCommentView(final int position, ViewGroup parent) {
            try {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View itemView = inflater.inflate(R.layout.comment_short, parent, false);
                questionViewPresenter.getComment(position).subscribe(new Action1<Comment>() {
                    @Override
                    public void call(Comment comment) {

                        UIUtil.setTextView(itemView, R.id.comment_short_question, comment.getText());
                        UIUtil.setTextView(itemView, R.id.comment_short_like_value, Integer.toString(comment.getLikes()));
                        UIUtil.setTextView(itemView, R.id.comment_short_answered_by_value, comment.getUserName());
                        UIUtil.setImageView(context, itemView, R.id.comment_short_image, comment.getImageUrl());

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });


                return itemView;
            } catch (Exception e) {
            }
            return null;
        }
        @SuppressLint("SetTextI18n")
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(position == 0) {
                return getQuestionView(parent);
            } else {
                return getCommentView(position, parent);
            }
        }
    }

}