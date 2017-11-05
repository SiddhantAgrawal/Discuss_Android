package com.discuss.ui.question.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.discuss.DiscussApplication;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.utils.EndlessScrollListener;
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
                ListView listview = (ListView) findViewById(R.id.question_view);
                adapter = new CommentViewAdapter(QuestionView.this, questionViewPresenter);
                listview.setAdapter(adapter);
                listview.setOnScrollListener(new EndlessScrollListener(() -> questionViewPresenter.update(() -> adapter.notifyDataSetChanged()), 4));
                mProgressDialog.dismiss();
            }
        }, questionID);


    }

    private class CommentViewAdapter extends BaseAdapter {

        private Context context;
        private QuestionViewPresenter<Comment> questionViewPresenter;

        CommentViewAdapter(Context context, QuestionViewPresenter<Comment> questionViewPresenter) {
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
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.question_complete, parent, false);

            TextView textViewForQuestion = (TextView) itemView.findViewById(R.id.question_complete_question);
            TextView textViewForLikes = (TextView) itemView.findViewById(R.id.question_complete_like_value);
            TextView textViewForPostedBy = (TextView) itemView.findViewById(R.id.question_complete_postedby_value);
            TextView textViewForDifficultyLevel = (TextView) itemView.findViewById(R.id.question_complete_difficulty_value);

            questionViewPresenter.getQuestion().subscribe(new Action1<Question>() {
                @Override
                public void call(Question question) {
                    textViewForQuestion.setText(question.getText());
                    textViewForLikes.setText(question.getLikes());
                    textViewForPostedBy.setText(question.getUserName());
                    textViewForDifficultyLevel.setText(question.getDifficulty());

                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e("QuestionView", throwable.toString());
                }
            });

            return itemView;
        }

        private View getCommentView(final int position, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.comment_short, parent, false);
            questionViewPresenter.getComment(position).subscribe(new Action1<Comment>() {
                @Override
                public void call(Comment comment) {
                    TextView questionText = (TextView) itemView.findViewById(R.id.comment_short_question);
                    TextView likes = (TextView) itemView.findViewById(R.id.comment_short_like_value);
                    TextView postedBy = (TextView) itemView.findViewById(R.id.comment_short_user_value);

                    questionText.setText(comment.getText());
                    likes.setText(Integer.toString(comment.getLikes()));
                    postedBy.setText(comment.getUserName());
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e("QuestionView", throwable.toString());
                }
            });



            return itemView;
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