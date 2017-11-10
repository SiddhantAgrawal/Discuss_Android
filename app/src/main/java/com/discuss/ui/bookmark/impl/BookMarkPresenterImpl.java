package com.discuss.ui.bookmark.impl;

import com.discuss.data.BookMarkRepository;
import com.discuss.data.DataRetriever;
import com.discuss.datatypes.Question;
import com.discuss.ui.bookmark.BookMarkPresenter;

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
public class BookMarkPresenterImpl implements BookMarkPresenter {
    private final BookMarkRepository bookMarkRepository;

    @Inject
    public BookMarkPresenterImpl(BookMarkRepository bookMarkRepository) {
        this.bookMarkRepository = bookMarkRepository;
    }

    @Override
    public void init(Action0 onCompletedAction) {
        bookMarkRepository.init(onCompletedAction);
    }

    @Override
    public void update(Action0 onCompletedAction) {
        bookMarkRepository.ensureKMoreQuestions(onCompletedAction);
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {});
        return Observable.just(true);
    }

    @Override
    public Observable<Question> get(int kth) {
        return bookMarkRepository.kthQuestion(kth);
    }

    @Override
    public int size() {
        return bookMarkRepository.estimatedSize();
    }

}
