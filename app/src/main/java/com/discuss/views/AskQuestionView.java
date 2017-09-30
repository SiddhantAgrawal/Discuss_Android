package com.discuss.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.siddhantagrawal.check_discuss.R;

public class AskQuestionView extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_question);
        FloatingActionButton addQuestion = (FloatingActionButton) findViewById(R.id.ask_question_add);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AskQuestionView.this, "your question has been added successfully",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
