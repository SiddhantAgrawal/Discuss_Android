package com.discuss.ui.commented;

import com.discuss.datatypes.Question;

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

public interface CommentedPresenter {
    void init(Action0 onCompletedAction);
    void update(Action0 onCompletedAction);
    Observable<Boolean> refresh();
    Observable<Question> get(int position);
    int size();
}