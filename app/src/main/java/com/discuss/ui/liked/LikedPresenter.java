package com.discuss.ui.liked;

import com.discuss.datatypes.Question;
import com.discuss.ui.QuestionPresenter;
import com.discuss.ui.QuestionSummary;

import java.io.Serializable;

import rx.Observable;
import rx.Single;
import rx.functions.Action0;

/**
 *
 * @author Deepak Thakur
 *
 * @see LikedPresenter
 *
 */

public interface LikedPresenter extends QuestionPresenter {

    void init(Action0 onCompletedAction);

    void update(Action0 onCompletedAction);

    Observable<Boolean> refresh();

    Single<QuestionSummary> get(int position);

    int size();
}