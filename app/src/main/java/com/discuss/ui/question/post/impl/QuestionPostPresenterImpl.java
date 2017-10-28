package com.discuss.ui.question.post.impl;

import android.widget.ArrayAdapter;

import com.discuss.data.DataFetcher;
import com.discuss.datatypes.Category;
import com.discuss.datatypes.Question;
import com.discuss.ui.question.post.QuestionPostPresenter;
import com.discuss.ui.question.view.QuestionViewPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * @author Deepak Thakur
 */
public class QuestionPostPresenterImpl implements QuestionPostPresenter {

    private DataFetcher dataFetcher;
    @Inject
    public QuestionPostPresenterImpl(DataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }

    @Override
    public void init(Action0 action0) {

    }

    @Override
    public Observable<List<Category>> getCategories() {
        return dataFetcher.getCategory().onBackpressureBuffer().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }
}
