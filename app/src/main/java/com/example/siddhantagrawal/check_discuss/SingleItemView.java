package com.example.siddhantagrawal.check_discuss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author Siddhant Agrawal
 * @author Deepak Thakur
 */
public class SingleItemView extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_complete);

        Intent intent = getIntent();

        TextView textViewForQuestion = (TextView) findViewById(R.id.question_complete_question);
        TextView textViewForLikes = (TextView) findViewById(R.id.question_complete_like_value);
        TextView textViewForPostedBy = (TextView) findViewById(R.id.question_complete_user_value);

        /* set results to the TextViews */
        textViewForQuestion.setText(intent.getStringExtra("questionText"));
        textViewForLikes.setText(Integer.toString(intent.getIntExtra("likes", 0)));
        textViewForPostedBy.setText(intent.getStringExtra("postedBy"));
    }
}