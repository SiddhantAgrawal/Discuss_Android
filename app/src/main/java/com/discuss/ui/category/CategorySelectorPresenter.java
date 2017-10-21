package com.discuss.ui.category;


import java.io.Serializable;

import rx.Observable;
import rx.functions.Action0;

public interface CategorySelectorPresenter<T extends Serializable> {
    void init(Action0 onCompletedAction);
    Observable<Boolean> refresh();
    Observable<T> get(int position);
    int size();
}
