package com.discuss.ui.question.post;


import com.discuss.datatypes.Category;
import com.discuss.datatypes.Question;

import java.util.List;

import rx.Observable;
import rx.functions.Action0;

/**
 *
 * @author Deepak Thakur
 *
 */

public interface QuestionPostPresenter {
    void init(Action0 action0);
    Observable<List<Category>> getCategories();
}
