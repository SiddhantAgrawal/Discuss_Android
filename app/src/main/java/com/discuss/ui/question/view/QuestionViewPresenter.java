package com.discuss.ui.question.view;

import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.ui.CommentPresenter;
import com.discuss.ui.QuestionPresenter;
import com.discuss.ui.QuestionSummary;

import rx.Observable;
import rx.functions.Action0;

/**
 * @author Deepak Thakur
 *
 */
public interface QuestionViewPresenter extends QuestionPresenter, CommentPresenter {

    void init(Action0 action0, int questionID);

    void update(Action0 onCompletedAction);

    Observable<Boolean> refresh();

    Observable<Comment> getComment(int position);

    int size();

    Observable<QuestionSummary> getQuestion();
}
