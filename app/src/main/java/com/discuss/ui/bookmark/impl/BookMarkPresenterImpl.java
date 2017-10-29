package com.discuss.ui.bookmark.impl;

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
    private final DataRetriever dataRetriever;
    private List<Question> questions;
    private int limit;
    private volatile boolean isLoading;
    private Observable<List<Question>> questionObservable;
    private final ReentrantLock lock = new ReentrantLock();

    @Inject
    public BookMarkPresenterImpl(DataRetriever dataRetriever) {
        this.dataRetriever = dataRetriever;
    }

    private void checkPreConditions() {
        if (null == dataRetriever || null == questions) {
            init(onCompleted);
        }
    }

    private void setQuestionObservableAndSubscribeForFirstSubscriber() {
        questionObservable = dataRetriever.   /* hot observable */
                getBookMarkedQuestions(questions.size(), limit, ""). /* TODO(Deepak): add proper values */
                onBackpressureBuffer().
                subscribeOn(Schedulers.io()).
                publish().
                refCount().
                observeOn(AndroidSchedulers.mainThread());
        questionObservable.subscribe(onNextQuestionsList, onError, (() -> {
            synchronized (lock) {
                isLoading = false;
            }
        }));
    }

    private final Action1<List<Question>> onNextQuestionsList = new Action1<List<Question>>() {
        @Override
        public void call(List<Question> fetchedQuestions) {
            questions.addAll(fetchedQuestions);
        }
    };

    private final Action1<Throwable> onError = throwable -> {};

    private final Action0 onCompleted = () -> {};


    @Override
    public void init(Action0 onCompletedAction) {
        questions = new CopyOnWriteArrayList<>(); /* update operations are in bulk and not to often to degrade the performance  */
        limit = 10;
        update(onCompletedAction);
    }

    @Override
    public void update(Action0 onCompletedAction) {
        checkPreConditions();
        synchronized (lock) {
            if (!isLoading) {
                isLoading = true;
                setQuestionObservableAndSubscribeForFirstSubscriber();
            }
            questionObservable.subscribe((a) -> {}, (a) ->{}, onCompletedAction);
        }
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {});
        return Observable.just(true);
    }

    @Override
    public Observable<Question> get(int position) {
        if (null != questions && questions.size() > position) {
            return Observable.just(questions.get(position));
        } else {
            update(() -> {});
            return dataRetriever.   /* cold observable */
                    getBookMarkedQuestions(position, 1, ""). /* TODO(Deepak): add proper values */
                    onBackpressureBuffer().
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).first().map(l -> l.get(0));

        }
    }

    @Override
    public int size() {
        return (null == questions) ? 0 : questions.size();
    }

}
