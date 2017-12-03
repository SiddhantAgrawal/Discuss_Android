package com.discuss.ui.question.view;

import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.ui.CommentPresenter;
import com.discuss.ui.CommentSummary;
import com.discuss.ui.QuestionPresenter;
import com.discuss.ui.QuestionSummary;

import rx.Observable;
import rx.Single;
import rx.functions.Action0;

/**
 * @author Deepak Thakur
 *
 */
public interface QuestionViewPresenter extends QuestionPresenter, CommentPresenter {

    void init(Action0 action0, int questionID);

    void update(Action0 onCompletedAction, Action0 onNoUpdate);

    Observable<Boolean> refresh();

    Single<CommentSummary> getComment(int position);

    int size();

    Single<QuestionSummary> getQuestion();

    int questionId();

    void save();

}
