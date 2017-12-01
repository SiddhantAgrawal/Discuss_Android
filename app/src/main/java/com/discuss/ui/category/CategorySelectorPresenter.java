package com.discuss.ui.category;


import com.discuss.datatypes.PersonCategoryPreference;

import rx.Observable;
import rx.functions.Action0;

public interface CategorySelectorPresenter {
    void init(Action0 onCompletedAction);
    Observable<Boolean> refresh();
    Observable<PersonCategoryPreference> get(int position);
    int size();
}
