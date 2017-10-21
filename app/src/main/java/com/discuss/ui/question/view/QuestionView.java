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

import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.ui.question.view.impl.QuestionViewPresenterImpl;
import com.discuss.utils.EndlessScrollListener;
import com.example.siddhantagrawal.check_discuss.R;

import rx.functions.Action0;

/**
 * @author Siddhant Agrawal
 * @author Deepak Thakur
 */
public class QuestionView extends Activity {
    CommentViewAdapter adapter;
    QuestionViewPresenter questionViewPresenter;
    private ProgressDialog mProgressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);
        questionViewPresenter = new QuestionViewPresenterImpl();
        mProgressDialog = new ProgressDialog(QuestionView.this);
        mProgressDialog.setTitle("see question");
        mProgressDialog.setMessage("loading... thanks for your patience");
        mProgressDialog.show();
        Intent intent = getIntent();
        Question question = (Question) intent.getSerializableExtra("question");

        questionViewPresenter.init(new Action0() {
            @Override
            public void call() {
                ListView listview = (ListView) findViewById(R.id.question_view);
                adapter = new CommentViewAdapter(QuestionView.this, questionViewPresenter);
                listview.setAdapter(adapter);
                listview.setOnScrollListener(new EndlessScrollListener(() -> questionViewPresenter.update(() -> adapter.notifyDataSetChanged()), 4));
                mProgressDialog.dismiss();
            }
        }, question);


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

            textViewForQuestion.setText(questionViewPresenter.getQuestion().getText());
            textViewForLikes.setText(Integer.toString(questionViewPresenter.getQuestion().getLikes()));
            textViewForPostedBy.setText(questionViewPresenter.getQuestion().getUserName());
            textViewForDifficultyLevel.setText(questionViewPresenter.getQuestion().getDifficulty());

            return itemView;
        }

        private View getCommentView(final int position, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.comment_short, parent, false);
            final Comment comment = questionViewPresenter.getComment(position).toBlocking().first();

            TextView questionText = (TextView) itemView.findViewById(R.id.comment_short_question);
            TextView likes = (TextView) itemView.findViewById(R.id.comment_short_like_value);
            TextView postedBy = (TextView) itemView.findViewById(R.id.comment_short_user_value);

            questionText.setText(comment.getText());
            likes.setText(Integer.toString(comment.getLikes()));
            postedBy.setText(comment.getUserName());

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