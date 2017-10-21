package com.discuss.ui.liked;

import java.io.Serializable;

import rx.Observable;
import rx.functions.Action0;

/**
 *
 * @author Deepak Thakur
 *
 * @see LikedPresenter
 *
 */

public interface LikedPresenter<T extends Serializable> {
    void init(Action0 onCompletedAction);
    void update(Action0 onCompletedAction);
    Observable<Boolean> refresh();
    Observable<T> get(int position);
    int size();
}