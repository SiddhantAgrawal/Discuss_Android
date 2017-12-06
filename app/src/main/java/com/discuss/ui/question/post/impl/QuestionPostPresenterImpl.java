package com.discuss.ui.question.post.impl;

import com.discuss.data.DataRetriever;
import com.discuss.datatypes.Category;
import com.discuss.ui.question.post.QuestionPostPresenter;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * @author Deepak Thakur
 */
public class QuestionPostPresenterImpl implements QuestionPostPresenter {

    private DataRetriever dataRetriever;
    @Inject
    public QuestionPostPresenterImpl(DataRetriever dataRetriever) {
        this.dataRetriever = dataRetriever;
    }

    @Override
    public void init(Action0 action0) {

    }

    @Override
    public Single<List<Category>> getCategories() {
        return dataRetriever.getCategory().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }
}
