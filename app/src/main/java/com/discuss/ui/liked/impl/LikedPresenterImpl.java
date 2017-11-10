package com.discuss.ui.liked.impl;

import com.discuss.data.DataRetriever;
import com.discuss.data.LikedQuestionsRepository;
import com.discuss.datatypes.Question;
import com.discuss.ui.liked.LikedPresenter;

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
public class LikedPresenterImpl implements LikedPresenter {
    private final LikedQuestionsRepository likedQuestionsRepository;

    @Inject
    public LikedPresenterImpl(LikedQuestionsRepository likedQuestionsRepository) {
        this.likedQuestionsRepository = likedQuestionsRepository;
    }

    @Override
    public void init(Action0 onCompletedAction) {
        likedQuestionsRepository.init(onCompletedAction);
    }

    @Override
    public void update(Action0 onCompletedAction) {
        likedQuestionsRepository.ensureKMoreQuestions(onCompletedAction);
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {});
        return Observable.just(true);
    }

    @Override
    public Observable<Question> get(int kth) {
        return likedQuestionsRepository.kthQuestion(kth);
    }

    @Override
    public int size() {
        return likedQuestionsRepository.estimatedSize();
    }
}
