package com.discuss.ui.commented.impl;


import com.discuss.data.BookMarkRepository;
import com.discuss.data.DataRetriever;
import com.discuss.data.QuestionsAnsweredRepository;
import com.discuss.datatypes.Question;
import com.discuss.ui.commented.CommentedPresenter;

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
public class CommentedPresenterImpl implements CommentedPresenter {
    private final QuestionsAnsweredRepository answeredRepository;

    @Inject
    public CommentedPresenterImpl(QuestionsAnsweredRepository answeredRepository) {
        this.answeredRepository = answeredRepository;
    }

    @Override
    public void init(Action0 onCompletedAction) {
        answeredRepository.init(onCompletedAction);
    }

    @Override
    public void update(Action0 onCompletedAction) {
        answeredRepository.ensureKMoreQuestions(10, onCompletedAction);
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {});
        return Observable.just(true);
    }

    @Override
    public Observable<Question> get(int kth) {
        return answeredRepository.kthQuestion(kth);
    }

    @Override
    public int size() {
        return answeredRepository.estimatedSize();
    }
}
