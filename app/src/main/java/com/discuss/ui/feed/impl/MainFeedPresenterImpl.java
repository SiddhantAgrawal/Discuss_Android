package com.discuss.ui.feed.impl;

import com.discuss.data.DataRetriever;
import com.discuss.data.QuestionRepository;
import com.discuss.data.SortBy;
import com.discuss.data.SortOrder;
import com.discuss.datatypes.Question;
import com.discuss.ui.feed.MainFeedPresenter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Deepak Thakur
 */
public class MainFeedPresenterImpl implements MainFeedPresenter {
    private final QuestionRepository questionRepository;
    private final SortBy sortBy;
    private final SortOrder sortOrder;

    @Inject
    public MainFeedPresenterImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
        this.sortBy = SortBy.LIKES;
        this.sortOrder = SortOrder.DESC;
    }

    @Override
    public void init(Action0 onCompletedAction) {
        questionRepository.init(onCompletedAction, this.sortBy, this.sortOrder);
    }

    @Override
    public void update(Action0 onCompletedAction) {
        questionRepository.ensureKMoreQuestions(10, onCompletedAction);
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {});
        return Observable.just(true);
    }

    @Override
    public Observable<Question> get(int kth) {
        return questionRepository.kthQuestion(kth, sortBy, sortOrder);
    }

    @Override
    public int size() {
        return questionRepository.estimatedSize();
    }

}
