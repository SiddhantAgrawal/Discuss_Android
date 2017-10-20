package com.discuss.ui.feed.impl;

import com.discuss.datatypes.Question;
import com.discuss.fetcher.impl.DataFetcherImpl;
import com.discuss.ui.feed.MainFeedPresenter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Deepak Thakur
 */
public class MainFeedPresenterImpl implements MainFeedPresenter<Question> {
    DataFetcherImpl dataFetcher;
    List<Question> questions;
    private int limit;
    private volatile boolean isLoading;
    private Observable<List<Question>> questionObservable;
    private final ReentrantLock lock = new ReentrantLock();

    public MainFeedPresenterImpl() {}
    private void checkPreConditions() {
        if (null == dataFetcher || null == questions) {
            init(onCompleted);
        }
    }

    private void setQuestionObservableAndSubscribeForFirstSubscriber() {
        questionObservable = dataFetcher.   /* hot observable */
                getQuestions(questions.size(), limit, ""). /* TODO(Deepak): add proper values */
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

    public static class CustomSubscriber extends Subscriber<List<Question>> {

        private final Action1<List<Question>> onNextAction;
        private final Action1<Throwable> onThrowableAction;
        private final Action0 onCompletedAction;

        public CustomSubscriber(Action1<List<Question>> onNextAction,
                                Action1<Throwable> onThrowableAction,
                                Action0 onCompletedAction) {
            this.onNextAction = onNextAction;
            this.onThrowableAction = onThrowableAction;
            this.onCompletedAction = onCompletedAction;
        }

        @Override
        public void onCompleted() {
            if (null != this.onCompletedAction) {
                this.onCompletedAction.call();
            }
        }

        @Override
        public void onError(Throwable e) {
            if (null != this.onThrowableAction) {
                this.onThrowableAction.call(e);
            }
        }

        @Override
        public void onNext(List<Question> questions) {
            if (null != this.onNextAction) {
                this.onNextAction.call(questions);
            }
        }
    }


    @Override
    public void init(Action0 onCompletedAction) {
        dataFetcher = new DataFetcherImpl();
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
            return dataFetcher.   /* cold observable */
                    getQuestions(position, 1, ""). /* TODO(Deepak): add proper values */
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
