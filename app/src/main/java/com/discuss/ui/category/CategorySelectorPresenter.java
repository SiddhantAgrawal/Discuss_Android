package com.discuss.ui.category;


import com.discuss.datatypes.UserCategoryPreference;

import java.io.Serializable;

import rx.Observable;
import rx.functions.Action0;

public interface CategorySelectorPresenter {
    void init(Action0 onCompletedAction);
    Observable<Boolean> refresh();
    Observable<UserCategoryPreference> get(int position);
    int size();
}
