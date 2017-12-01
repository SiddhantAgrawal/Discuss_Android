package com.discuss.ui.comment.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.discuss.ui.CommentSummary;
import com.example.siddhantagrawal.check_discuss.R;

import javax.inject.Inject;

import rx.Subscriber;

/**
 *
 * @author Deepak Thakur
 */
public class UserComment extends Activity {

    @Inject
    public UserCommentPresenter userCommentPresenter;

    private volatile boolean viewMode = true;
    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_comment);
        ProgressDialog mProgressDialog = new ProgressDialog(UserComment.this);
        mProgressDialog.setTitle("view comment");
        mProgressDialog.setMessage("loading... thanks for your patience");
        mProgressDialog.show();
        Intent intent = getIntent();
        int questionID =  intent.getIntExtra("questionId", 0);

        ViewSwitcher simpleViewSwitcher = (ViewSwitcher) findViewById(R.id.user_comment_comment_view_switch);
        Button button = (Button) findViewById(R.id.user_comment_edit_button);
        final EditText editText = (EditText) findViewById(R.id.user_comment_comment_edit_view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewMode) {
                    editText.setText(userCommentPresenter.getEditedComment());
                    button.setText("Save the changes");
                    simpleViewSwitcher.showNext();
                    viewMode = !viewMode;
                } else {
                    button.setText("Edit your Comment");
                    userCommentPresenter.setEditedComment(editText.getText().toString());
                    simpleViewSwitcher.showPrevious();
                    viewMode = !viewMode;
                }

            }
        });
        userCommentPresenter.init(questionID).subscribe(new Subscriber<CommentSummary>() {
            @Override
            public void onCompleted() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(CommentSummary commentSummary) {
                if(null != commentSummary) {
                    TextView textView = (TextView) findViewById(R.id.user_comment_comment_text_view);
                    textView.setText(commentSummary.getText());
                }
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        if(null != userCommentPresenter) {
            userCommentPresenter.saveEditedComment();
        }
    }
}
