package com.discuss.ui.commented;

import com.discuss.datatypes.Question;
import com.discuss.ui.QuestionPresenter;
import com.discuss.ui.QuestionSummary;

import java.io.Serializable;

import rx.Observable;
import rx.functions.Action0;

/**
 *
 * @author Deepak Thakur
 *
 * @see com.discuss.ui.liked.LikedPresenter
 *
 */

public interface CommentedPresenter extends QuestionPresenter {
    void init(Action0 onCompletedAction);
    void update(Action0 onCompletedAction);
    Observable<Boolean> refresh();
    Observable<QuestionSummary> get(int position);
    int size();
}