package com.discuss.ui.feed;

import java.io.Serializable;
import java.util.List;

import rx.Observable;
import rx.functions.Action0;

/**
 *
 * @author Deepak Thakur
 *
 * @see com.discuss.ui.feed.impl.MainFeedPresenterImpl
 *
 */

public interface MainFeedPresenter<T extends Serializable> {
    void init(Action0 onCompletedAction);
    void update(Action0 onCompletedAction);
    Observable<Boolean> refresh();
    Observable<T> get(int position);
    int size();
}
