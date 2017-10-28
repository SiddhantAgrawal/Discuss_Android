package com.discuss.ui.question.view.impl;


import android.util.Log;

import com.discuss.data.DataFetcher;
import com.discuss.datatypes.Comment;
import com.discuss.datatypes.Question;
import com.discuss.data.impl.DataFetcherImpl;
import com.discuss.ui.question.view.QuestionViewPresenter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class QuestionViewPresenterImpl implements QuestionViewPresenter<Comment>{

    private Question question = null;
    private final DataFetcher dataFetcher;
    private List<Comment> comments;
    private int limit;
    private volatile boolean isLoading = false;
    private Observable<List<Comment>> commentsObservable;
    private final ReentrantLock lock = new ReentrantLock();

    @Inject
    public QuestionViewPresenterImpl(DataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }
    private void checkPreConditions() {
        if (null == dataFetcher || null == comments) {
            init(onCompleted, question);
        }
    }

    private void setCommentsObservableAndSubscribeForFirstSubscriber() {
        commentsObservable = dataFetcher.   /* hot observable */
                getCommentsForQuestion(question.getQuestionId(), comments.size(), limit, ""). /* TODO(Deepak): add proper values */
                onBackpressureBuffer().
                subscribeOn(Schedulers.io()).
                publish().
                refCount().
                observeOn(AndroidSchedulers.mainThread());

        commentsObservable.subscribe(onNextCommentsList, onError, (() -> {
            synchronized (lock) {
                isLoading = false;
            }
        }));
    }

    private final Action1<List<Comment>> onNextCommentsList = new Action1<List<Comment>>() {
        @Override
        public void call(List<Comment> fetchedComments) {
            comments.addAll(fetchedComments);
        }
    };

    private final Action1<Throwable> onError = throwable -> {};

    private final Action0 onCompleted = () -> {};


    @Override
    public void init(Action0 onCompletedAction, Question question) {
        this.question = question;
        comments = new CopyOnWriteArrayList<>(); /* update operations are in bulk and not to often to degrade the performance  */
        limit = 10;
        update(onCompletedAction);
    }

    @Override
    public void update(Action0 onCompletedAction) {
        checkPreConditions();
        synchronized (lock) {
            if (!isLoading) {
                isLoading = true;
                setCommentsObservableAndSubscribeForFirstSubscriber();
            }
            commentsObservable.subscribe((a) -> {}, (a) ->{}, onCompletedAction);
        }
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {}, question);
        return Observable.just(true);
    }

    @Override
    public Observable<Comment> getComment(int position) {
        if (null != comments && comments.size() > position) {
            return Observable.just(comments.get(position));
        } else {
            update(() -> {});
            return dataFetcher.   /* cold observable */
                    getCommentsForQuestion(question.getQuestionId(), position, 1, ""). /* TODO(Deepak): add proper values */
                    onBackpressureBuffer().
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).first().map(l -> l.get(0));

        }
    }

    @Override
    public int size() {
        return (null == comments) ? 0 : comments.size();
    }

    @Override
    public Question getQuestion() {
        return question;
    }
}
