package com.discuss.baseAdapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.example.siddhantagrawal.check_discuss.R;

import java.util.List;


public class CommentViewAdapter extends BaseAdapter {

    private Context context;
    private List<Comment> data;
    private Question question;

    public CommentViewAdapter(Context context,
                              List<Comment> arraylist, Question question) {
        this.context = context;
        data = arraylist;
        this.question = question;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(position == 0) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.question_complete, parent, false); /* TODO(Deepak): See if this has performance issues */

            TextView textViewForQuestion = (TextView) itemView.findViewById(R.id.question_complete_question);
            TextView textViewForLikes = (TextView) itemView.findViewById(R.id.question_complete_like_value);
            TextView textViewForPostedBy = (TextView) itemView.findViewById(R.id.question_complete_postedby_value);
            TextView textViewForDifficultyLevel = (TextView) itemView.findViewById(R.id.question_complete_difficulty_value);

            textViewForQuestion.setText(question.getText());
            textViewForLikes.setText(Integer.toString(question.getLikes()));
            textViewForPostedBy.setText(question.getUserName());
            textViewForDifficultyLevel.setText(question.getDifficulty());

            return itemView;
        } else {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.comment_short, parent, false); /* TODO(Deepak): See if this has performance issues */
            final Comment comment = data.get(position);

            TextView questionText = (TextView) itemView.findViewById(R.id.comment_short_question);
            TextView likes = (TextView) itemView.findViewById(R.id.comment_short_like_value);
            TextView postedBy = (TextView) itemView.findViewById(R.id.comment_short_user_value);

            questionText.setText(comment.getText());
            likes.setText(Integer.toString(comment.getLikes()));
            postedBy.setText(comment.getUserName());

            return itemView;
        }
    }
}
