package com.discuss.ui.question.view;

import com.discuss.datatypes.Question;

import rx.Observable;
import rx.functions.Action0;

/**
 * @author Deepak Thakur
 *
 */
public interface QuestionViewPresenter<T> {

    void init(Action0 action0, Question question);

    void update(Action0 onCompletedAction);

    Observable<Boolean> refresh();

    Observable<T> getComment(int position);

    int size();

    Question getQuestion();
}
